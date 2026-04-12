package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.CuttingFeeRequestDTO;
import com.group_project.wfms_backend.dto.auth.CuttingFeeResponseDTO;
import com.group_project.wfms_backend.model.Employee;
import com.group_project.wfms_backend.model.RawMaterialCuttingFee;
import com.group_project.wfms_backend.model.SupplyRawMaterial;
import com.group_project.wfms_backend.repository.EmployeeRepository;
import com.group_project.wfms_backend.repository.RawMaterialCuttingFeeRepository;
import com.group_project.wfms_backend.repository.SupplyRawMaterialRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuttingFeeService {

    private final RawMaterialCuttingFeeRepository cuttingFeeRepository;
    private final SupplyRawMaterialRepository supplyRawMaterialRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public CuttingFeeResponseDTO createCuttingFee(CuttingFeeRequestDTO request) {
        SupplyRawMaterial supply = supplyRawMaterialRepository.findById(request.getSupplyId())
                .orElseThrow(() -> new EntityNotFoundException("Supply record not found with id: " + request.getSupplyId()));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        RawMaterialCuttingFee cuttingFee = new RawMaterialCuttingFee();
        cuttingFee.setSupplyRawMaterial(supply);
        cuttingFee.setEmployee(employee);
        cuttingFee.setFee(request.getFee());
        cuttingFee.setDate(request.getDate());
        cuttingFee.setRemarks(request.getRemarks());

        RawMaterialCuttingFee savedFee = cuttingFeeRepository.save(cuttingFee);
        return mapToResponseDTO(savedFee);
    }

    @Transactional
    public CuttingFeeResponseDTO updateCuttingFee(Integer id, CuttingFeeRequestDTO request) {
        RawMaterialCuttingFee cuttingFee = cuttingFeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cutting fee record not found with id: " + id));

        SupplyRawMaterial supply = supplyRawMaterialRepository.findById(request.getSupplyId())
                .orElseThrow(() -> new EntityNotFoundException("Supply record not found with id: " + request.getSupplyId()));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        cuttingFee.setSupplyRawMaterial(supply);
        cuttingFee.setEmployee(employee);
        cuttingFee.setFee(request.getFee());
        cuttingFee.setDate(request.getDate());
        cuttingFee.setRemarks(request.getRemarks());

        RawMaterialCuttingFee savedFee = cuttingFeeRepository.save(cuttingFee);
        return mapToResponseDTO(savedFee);
    }

    @Transactional(readOnly = true)
    public CuttingFeeResponseDTO getCuttingFeeById(Integer id) {
        RawMaterialCuttingFee cuttingFee = cuttingFeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cutting fee record not found with id: " + id));
        return mapToResponseDTO(cuttingFee);
    }

    @Transactional(readOnly = true)
    public List<CuttingFeeResponseDTO> getAllCuttingFees() {
        return cuttingFeeRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCuttingFee(Integer id) {
        if (!cuttingFeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Cutting fee record not found with id: " + id);
        }
        cuttingFeeRepository.deleteById(id);
    }

    private CuttingFeeResponseDTO mapToResponseDTO(RawMaterialCuttingFee cuttingFee) {
        CuttingFeeResponseDTO dto = new CuttingFeeResponseDTO();
        dto.setId(cuttingFee.getId());
        
        if (cuttingFee.getSupplyRawMaterial() != null) {
            dto.setSupplyId(cuttingFee.getSupplyRawMaterial().getSupplyId());
            dto.setInvoiceNumber(cuttingFee.getSupplyRawMaterial().getInvoiceNumber());
            if (cuttingFee.getSupplyRawMaterial().getSupplier() != null) {
                dto.setSupplierName(cuttingFee.getSupplyRawMaterial().getSupplier().getCusName());
            }
        }
        
        if (cuttingFee.getEmployee() != null) {
            dto.setEmployeeId(cuttingFee.getEmployee().getId());
            dto.setEmployeeName(cuttingFee.getEmployee().getFullName());
        }
        
        dto.setFee(cuttingFee.getFee());
        dto.setDate(cuttingFee.getDate());
        dto.setRemarks(cuttingFee.getRemarks());
        
        return dto;
    }
}
