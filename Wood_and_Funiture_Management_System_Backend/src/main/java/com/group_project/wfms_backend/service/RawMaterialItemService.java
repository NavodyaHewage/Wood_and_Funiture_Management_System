package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.RawMaterialItemResponseDTO;
import com.group_project.wfms_backend.model.RawMaterialItem;
import com.group_project.wfms_backend.repository.RawMaterialItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RawMaterialItemService {

    private final RawMaterialItemRepository rawMaterialItemRepository;

    public List<RawMaterialItemResponseDTO> getAllRawMaterialItems() {
        return rawMaterialItemRepository.findAll().stream()
                .filter(item -> item.getPricePerCft() != null)
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private RawMaterialItemResponseDTO mapToResponseDTO(RawMaterialItem item) {
        return RawMaterialItemResponseDTO.builder()
                .rmId(item.getRmId())
                .rmName(item.getRmName())
                .pricePerCft(item.getPricePerCft())
                .description(item.getDescription())
                .build();
    }
}
