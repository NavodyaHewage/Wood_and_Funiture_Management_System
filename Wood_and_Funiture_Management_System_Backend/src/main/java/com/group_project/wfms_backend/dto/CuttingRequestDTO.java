package com.group_project.wfms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuttingRequestDTO {
    private List<Integer> rawMaterialLogIds;
    private List<CutProductDTO> cutProducts;
}
