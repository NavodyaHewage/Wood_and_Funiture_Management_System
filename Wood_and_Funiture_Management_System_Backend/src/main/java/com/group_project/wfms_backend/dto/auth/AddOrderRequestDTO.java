package com.group_project.wfms_backend.dto.auth;
//order eka add krana ekna

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddOrderRequestDTO {
    private Integer customerId;
    private List<AddOrderItemDTO> items;
}
