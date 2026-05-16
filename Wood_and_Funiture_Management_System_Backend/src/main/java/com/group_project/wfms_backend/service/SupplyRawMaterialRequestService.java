package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.*;
import com.group_project.wfms_backend.model.*;
import com.group_project.wfms_backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplyRawMaterialRequestService {

    private final SupplyRawMaterialRequestRepository requestRepository;
    private final SupplyRawMaterialRequestDetailsRepository requestDetailsRepository;
    private final SupplierRepository supplierRepository;
    private final RawMaterialItemRepository rawMaterialItemRepository;
    private final UserRepository userRepository;
    private final SupplyRawMaterialService supplyRawMaterialService;

    @Transactional
    public SupplyRequestResponseDTO createRequest(SupplyRequestDTO dto) {
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));

        User creator = null;
        if (dto.getCreatedBy() != null) {
            creator = userRepository.findById(dto.getCreatedBy())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
        }

        SupplyRawMaterialRequest request = new SupplyRawMaterialRequest();
        request.setSupplier(supplier);
        request.setTransportBySupplier(dto.getTransportBySupplier());
        request.setTransportNotes(dto.getTransportNotes());
        request.setRemarks(dto.getRemarks());
        request.setCreatedBy(creator);
        request.setStatus(SupplyRawMaterialRequest.RequestStatus.Pending);

        List<SupplyRawMaterialRequestDetails> details = dto.getDetails().stream().map(d -> {
            SupplyRawMaterialRequestDetails detail = new SupplyRawMaterialRequestDetails();
            detail.setRequest(request);
            detail.setRawMaterialItem(rawMaterialItemRepository.findById(d.getRmId())
                    .orElseThrow(() -> new EntityNotFoundException("Raw Material Item not found")));
            detail.setAdminRequestedCft(d.getAdminRequestedCft());
            detail.setSupplierApprovedCft(d.getSupplierApprovedCft());
            detail.setUnitPrice(d.getUnitPrice());
            detail.setRemarks(d.getRemarks());
            return detail;
        }).collect(Collectors.toList());

        request.setDetails(details);
        // Explicitly set the request on each detail (redundant but safe)
        details.forEach(d -> d.setRequest(request));
        
        SupplyRawMaterialRequest saved = requestRepository.save(request);
        return mapToResponseDTO(saved);
    }

    @Transactional
    public void deleteRequest(Integer requestId) {
        SupplyRawMaterialRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));
        
        if (request.getStatus() != SupplyRawMaterialRequest.RequestStatus.Pending) {
            throw new RuntimeException("Cannot delete a request that is already processed");
        }
        
        requestRepository.delete(request);
    }

    @Transactional
    public SupplyRequestResponseDTO processSupplierApproval(Integer requestId, List<SupplyRequestDetailDTO> detailsDto) {
        SupplyRawMaterialRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));
        
        checkSupplierPermission(request);

        for (SupplyRequestDetailDTO dDto : detailsDto) {
            SupplyRawMaterialRequestDetails detail = request.getDetails().stream()
                    .filter(d -> d.getRawMaterialItem().getRmId().equals(dDto.getRmId()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Detail not found for RM: " + dDto.getRmId()));
            
            detail.setSupplierApprovedCft(dDto.getSupplierApprovedCft());
            detail.setUnitPrice(dDto.getUnitPrice());
            detail.setRemarks(dDto.getRemarks());
        }

        request.setApprovedDate(LocalDateTime.now());
        request.setStatus(SupplyRawMaterialRequest.RequestStatus.Approved);
        
        SupplyRawMaterialRequest saved = requestRepository.save(request);
        return mapToResponseDTO(saved);
    }

    @Transactional
    public SupplyRequestResponseDTO updateStatus(Integer requestId, String status) {
        SupplyRawMaterialRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));
        
        checkSupplierPermission(request);
        
        request.setStatus(SupplyRawMaterialRequest.RequestStatus.valueOf(status));
        return mapToResponseDTO(requestRepository.save(request));
    }

    private void checkSupplierPermission(SupplyRawMaterialRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        if (currentUser.getRole() == UserRole.SUPPLIER) {
            String email = currentUser.getEmail();
            if (request.getSupplier() == null || !request.getSupplier().getEmail().equalsIgnoreCase(email)) {
                throw new AccessDeniedException("You are not authorized to access this request");
            }
        }
    }

    @Transactional(readOnly = true)
    public List<SupplyRequestResponseDTO> getAllRequests() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (currentUser.getRole() == UserRole.SUPPLIER) {
            // Find the supplier associated with this user by email
            String email = currentUser.getEmail();
            return supplierRepository.findByEmail(email).stream()
                    .flatMap(s -> requestRepository.findBySupplier(s).stream())
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());
        }

        // Admin and Manager can see all
        return requestRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SupplyRequestResponseDTO getRequestById(Integer id) {
        return requestRepository.findById(id).map(this::mapToResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));
    }

    @Transactional
    public SupplyRawMaterialResponseDTO convertToSupplyOrder(Integer requestId, SupplyRawMaterialRequestDTO supplyOrderDto) {
        SupplyRawMaterialRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        if (request.getStatus() == SupplyRawMaterialRequest.RequestStatus.Converted) {
            throw new IllegalStateException("Request already converted to supply order");
        }

        // Use the existing SupplyRawMaterialService to create the actual order
        SupplyRawMaterialResponseDTO response = supplyRawMaterialService.createSupplyRawMaterial(supplyOrderDto);

        request.setStatus(SupplyRawMaterialRequest.RequestStatus.Converted);
        requestRepository.save(request);

        return response;
    }

    private SupplyRequestResponseDTO mapToResponseDTO(SupplyRawMaterialRequest request) {
        SupplyRequestResponseDTO dto = new SupplyRequestResponseDTO();
        dto.setRequestId(request.getRequestId());
        dto.setSupplierId(request.getSupplier().getSupId());
        dto.setSupplierName(request.getSupplier().getSupName());
        dto.setRequestDate(request.getRequestDate());
        dto.setStatus(request.getStatus().name());
        dto.setTransportBySupplier(request.getTransportBySupplier());
        dto.setTransportNotes(request.getTransportNotes());
        dto.setRemarks(request.getRemarks());
        if (request.getCreatedBy() != null) {
            dto.setCreatedById(request.getCreatedBy().getUserId());
            dto.setCreatedByUsername(request.getCreatedBy().getUsername());
        }
        dto.setApprovedDate(request.getApprovedDate());
        dto.setDetails(request.getDetails().stream().map(this::mapToDetailResponseDTO).collect(Collectors.toList()));
        return dto;
    }

    private SupplyRequestDetailResponseDTO mapToDetailResponseDTO(SupplyRawMaterialRequestDetails detail) {
        SupplyRequestDetailResponseDTO dto = new SupplyRequestDetailResponseDTO();
        dto.setRequestDetailId(detail.getRequestDetailId());
        dto.setRmId(detail.getRawMaterialItem().getRmId());
        dto.setRmName(detail.getRawMaterialItem().getRmName());
        dto.setAdminRequestedCft(detail.getAdminRequestedCft());
        dto.setSupplierApprovedCft(detail.getSupplierApprovedCft());
        dto.setUnitPrice(detail.getUnitPrice());
        dto.setRemarks(detail.getRemarks());
        return dto;
    }
}
