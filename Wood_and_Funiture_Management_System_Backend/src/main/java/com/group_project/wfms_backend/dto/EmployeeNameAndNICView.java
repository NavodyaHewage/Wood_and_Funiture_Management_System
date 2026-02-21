package com.group_project.wfms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeNameAndNICView {
    private Integer id;
    private String fullName;
    private String nic;
}
