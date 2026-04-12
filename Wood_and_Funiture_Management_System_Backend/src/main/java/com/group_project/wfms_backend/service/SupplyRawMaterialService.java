package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.*;
import com.group_project.wfms_backend.model.*;
import com.group_project.wfms_backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplyRawMaterialService {

    private final SupplyRawMaterialRepository supplyRawMaterialRepository;
    private final SupplyRawMaterialDetailsRepository supplyRawMaterialDetailsRepository;
    private final CustomerRepository customerRepository;
    private final RawMaterialItemRepository rawMaterialItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public SupplyRawMaterialResponseDTO createSupplyRawMaterial(SupplyRawMaterialRequestDTO request) {
        Customer supplier = customerRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + request.getSupplierId()));
        
        RawMaterialItem mainRmItem = rawMaterialItemRepository.findById(request.getRmId())
                .orElseThrow(() -> new EntityNotFoundException("Raw Material Item not found with id: " + request.getRmId()));

        User createdBy = null;
        if (request.getCreatedById() != null) {
            createdBy = userRepository.findById(request.getCreatedById())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getCreatedById()));
        }

        SupplyRawMaterial supply = new SupplyRawMaterial();
        supply.setSupplier(supplier);
        supply.setRawMaterialItem(mainRmItem);
        supply.setInvoiceNumber(request.getInvoiceNumber());
        supply.setTransport(request.getTransport() != null ? request.getTransport() : BigDecimal.ZERO);
        supply.setSupplyDate(request.getSupplyDate());
        supply.setCreatedBy(createdBy);

        List<SupplyRawMaterialDetails> details = request.getSupplyDetails().stream().map(detailRequest -> {
            SupplyRawMaterialDetails detail = new SupplyRawMaterialDetails();
            detail.setSupplyRawMaterial(supply);
            
            RawMaterialItem detailRmItem = rawMaterialItemRepository.findById(detailRequest.getRmId())
                    .orElseThrow(() -> new EntityNotFoundException("Raw Material Item not found for detail with id: " + detailRequest.getRmId()));
            
            detail.setRawMaterialItem(detailRmItem);
            detail.setLogNumber(detailRequest.getLogNumber());
            detail.setLengthFt(detailRequest.getLengthFt());
            detail.setGirthFt(detailRequest.getGirthFt());
            detail.setPrice(detailRequest.getPrice());
            return detail;
        }).collect(Collectors.toList());

        supply.setSupplyDetails(details);
        
        // Calculate total amount manually since child totals are DB-generated
        BigDecimal totalAmount = calculateTotalAmount(request.getSupplyDetails());
        supply.setTotalAmount(totalAmount);

        SupplyRawMaterial savedSupply = supplyRawMaterialRepository.save(supply);
        return mapToResponseDTO(savedSupply);
    }

    @Transactional
    public SupplyRawMaterialResponseDTO updateSupplyRawMaterial(Integer id, SupplyRawMaterialRequestDTO request) {
        SupplyRawMaterial supply = supplyRawMaterialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supply record not found with id: " + id));

        Customer supplier = customerRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + request.getSupplierId()));
        
        RawMaterialItem mainRmItem = rawMaterialItemRepository.findById(request.getRmId())
                .orElseThrow(() -> new EntityNotFoundException("Raw Material Item not found with id: " + request.getRmId()));

        supply.setSupplier(supplier);
        supply.setRawMaterialItem(mainRmItem);
        supply.setInvoiceNumber(request.getInvoiceNumber());
        supply.setTransport(request.getTransport() != null ? request.getTransport() : BigDecimal.ZERO);
        supply.setSupplyDate(request.getSupplyDate());

        // Clear existing details and add new ones
        supply.getSupplyDetails().clear();
        
        List<SupplyRawMaterialDetails> newDetails = request.getSupplyDetails().stream().map(detailRequest -> {
            SupplyRawMaterialDetails detail = new SupplyRawMaterialDetails();
            detail.setSupplyRawMaterial(supply);
            
            RawMaterialItem detailRmItem = rawMaterialItemRepository.findById(detailRequest.getRmId())
                    .orElseThrow(() -> new EntityNotFoundException("Raw Material Item not found for detail with id: " + detailRequest.getRmId()));
            
            detail.setRawMaterialItem(detailRmItem);
            detail.setLogNumber(detailRequest.getLogNumber());
            detail.setLengthFt(detailRequest.getLengthFt());
            detail.setGirthFt(detailRequest.getGirthFt());
            detail.setPrice(detailRequest.getPrice());
            return detail;
        }).collect(Collectors.toList());

        supply.getSupplyDetails().addAll(newDetails);
        
        BigDecimal totalAmount = calculateTotalAmount(request.getSupplyDetails());
        supply.setTotalAmount(totalAmount);

        SupplyRawMaterial savedSupply = supplyRawMaterialRepository.save(supply);
        return mapToResponseDTO(savedSupply);
    }

    @Transactional(readOnly = true)
    public SupplyRawMaterialResponseDTO getSupplyRawMaterialById(Integer id) {
        SupplyRawMaterial supply = supplyRawMaterialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supply record not found with id: " + id));
        return mapToResponseDTO(supply);
    }

    @Transactional(readOnly = true)
    public List<SupplyRawMaterialResponseDTO> getAllSupplyRawMaterials() {
        return supplyRawMaterialRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSupplyRawMaterial(Integer id) {
        if (!supplyRawMaterialRepository.existsById(id)) {
            throw new EntityNotFoundException("Supply record not found with id: " + id);
        }
        supplyRawMaterialRepository.deleteById(id);
    }

    private BigDecimal calculateTotalAmount(List<SupplyRawMaterialDetailRequestDTO> details) {
        BigDecimal total = BigDecimal.ZERO;
        for (SupplyRawMaterialDetailRequestDTO detail : details) {
            // Formula: (Length * Girth * Girth / 12) * Price
            BigDecimal quantity = detail.getLengthFt()
                    .multiply(detail.getGirthFt())
                    .multiply(detail.getGirthFt())
                    .divide(new BigDecimal("12"), 3, RoundingMode.HALF_UP);
            
            BigDecimal lineTotal = quantity.multiply(detail.getPrice());
            total = total.add(lineTotal);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private SupplyRawMaterialResponseDTO mapToResponseDTO(SupplyRawMaterial supply) {
        SupplyRawMaterialResponseDTO dto = new SupplyRawMaterialResponseDTO();
        dto.setSupplyId(supply.getSupplyId());
        if (supply.getSupplier() != null) {
            dto.setSupplierId(supply.getSupplier().getCusId());
            dto.setSupplierName(supply.getSupplier().getCusName());
        }
        if (supply.getRawMaterialItem() != null) {
            dto.setRmId(supply.getRawMaterialItem().getRmId());
            dto.setRmName(supply.getRawMaterialItem().getRmName());
        }
        dto.setInvoiceNumber(supply.getInvoiceNumber());
        dto.setTotalAmount(supply.getTotalAmount());
        dto.setTransport(supply.getTransport());
        dto.setSupplyDate(supply.getSupplyDate());
        
        if (supply.getSupplyDetails() != null) {
            dto.setSupplyDetails(supply.getSupplyDetails().stream().map(this::mapToDetailResponseDTO).collect(Collectors.toList()));
        }
        
        return dto;
    }

    private SupplyRawMaterialDetailResponseDTO mapToDetailResponseDTO(SupplyRawMaterialDetails detail) {
        SupplyRawMaterialDetailResponseDTO dto = new SupplyRawMaterialDetailResponseDTO();
        dto.setId(detail.getId());
        if (detail.getRawMaterialItem() != null) {
            dto.setRmId(detail.getRawMaterialItem().getRmId());
            dto.setRmName(detail.getRawMaterialItem().getRmName());
        }
        dto.setLogNumber(detail.getLogNumber());
        dto.setLengthFt(detail.getLengthFt());
        dto.setGirthFt(detail.getGirthFt());
        dto.setTotalQuantityCft(detail.getTotalQuantityCft());
        dto.setPrice(detail.getPrice());
        dto.setLineTotal(detail.getLineTotal());
        return dto;
    }
}
