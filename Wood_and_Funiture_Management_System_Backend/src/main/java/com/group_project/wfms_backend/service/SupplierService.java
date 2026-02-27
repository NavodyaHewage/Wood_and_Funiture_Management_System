package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.model.Supplier;
import com.group_project.wfms_backend.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier getSupplierById(Integer id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
    }

    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier updateSupplier(Integer id, Supplier supplierDetails) {
        Supplier supplier = getSupplierById(id);
        supplier.setSupName(supplierDetails.getSupName());
        supplier.setSupCat(supplierDetails.getSupCat());
        supplier.setMobile(supplierDetails.getMobile());
        supplier.setAddress(supplierDetails.getAddress());
        supplier.setEmail(supplierDetails.getEmail());
        supplier.setIsActive(supplierDetails.getIsActive());
        return supplierRepository.save(supplier);
    }

    @Transactional
    public void deleteSupplier(Integer id) {
        Supplier supplier = getSupplierById(id);
        supplierRepository.delete(supplier);
    }
}
