package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.GrnDetailResponseDTO;
import com.group_project.wfms_backend.dto.auth.GrnResponseDTO;
import com.group_project.wfms_backend.model.GRN;
import com.group_project.wfms_backend.model.GrnDetails;
import com.group_project.wfms_backend.repository.GRNRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GRNService {

    private final GRNRepository grnRepository;

    @Transactional(readOnly = true)
    public GrnResponseDTO getGrnById(Integer id) {
        GRN grn = grnRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GRN not found with id: " + id));
        return mapToResponseDTO(grn);
    }

    @Transactional(readOnly = true)
    public List<GrnResponseDTO> getAllGrns() {
        return grnRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private GrnResponseDTO mapToResponseDTO(GRN grn) {
        GrnResponseDTO dto = new GrnResponseDTO();
        dto.setGrnId(grn.getGrnId());
        dto.setGrnNumber(grn.getGrnNumber());
        dto.setInvoiceNumber(grn.getInvoiceNumber());
        dto.setDate(grn.getDate());
        dto.setTotalAmount(grn.getTotalAmount());
        dto.setRemarks(grn.getRemarks());
        dto.setCreatedAt(grn.getCreatedAt());

        if (grn.getSupplier() != null) {
            dto.setSupplierId(grn.getSupplier().getSupId());
            dto.setSupplierName(grn.getSupplier().getSupName());
            dto.setSupplierAddress(grn.getSupplier().getAddress());
            dto.setSupplierMobile(grn.getSupplier().getMobile());
            dto.setSupplierEmail(grn.getSupplier().getEmail());
        }

        if (grn.getSupplyOrder() != null) {
            dto.setSupplyOrderId(grn.getSupplyOrder().getSupplyId());
            dto.setSupplyOrderInvoiceNumber(grn.getSupplyOrder().getInvoiceNumber());
            dto.setTransportCost(grn.getSupplyOrder().getTransport());
            dto.setCuttingFee(grn.getSupplyOrder().getCuttingFee());
        }

        if (grn.getGrnDetails() != null) {
            dto.setGrnDetails(grn.getGrnDetails().stream()
                    .map(this::mapToDetailResponseDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private GrnDetailResponseDTO mapToDetailResponseDTO(GrnDetails detail) {
        GrnDetailResponseDTO dto = new GrnDetailResponseDTO();
        dto.setId(detail.getId());
        dto.setAmount(detail.getAmount());
        dto.setDate(detail.getDate());

        if (detail.getSupplyRawMaterialDetails() != null) {
            dto.setLogNumber(detail.getSupplyRawMaterialDetails().getLogNumber());
            dto.setLengthFt(detail.getSupplyRawMaterialDetails().getLengthFt());
            dto.setGirthFt(detail.getSupplyRawMaterialDetails().getGirthFt());
            dto.setTotalQuantityCft(detail.getSupplyRawMaterialDetails().getTotalQuantityCft());
            dto.setUnitPrice(detail.getSupplyRawMaterialDetails().getPrice());
            
            if (detail.getSupplyRawMaterialDetails().getRawMaterialItem() != null) {
                dto.setRmId(detail.getSupplyRawMaterialDetails().getRawMaterialItem().getRmId());
                dto.setRmName(detail.getSupplyRawMaterialDetails().getRawMaterialItem().getRmName());
            }
        }

        return dto;
    }
}
