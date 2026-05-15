package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.model.DesignationSalary;
import com.group_project.wfms_backend.repository.DesignationSalaryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DesignationSalaryService {

    @Autowired
    private DesignationSalaryRepository repository;

    public List<DesignationSalary> getAll() {
        return repository.findAll();
    }

    public DesignationSalary getByDesignation(String name) {
        // Critical lookup method used by Payroll and Loan services to determine the base rate for an employee's designation.
        return repository.findByDesignationNameAndIsActiveTrue(name)
                .orElseThrow(() -> new EntityNotFoundException("No active salary mapping found for designation: " + name));
    }

    public DesignationSalary save(DesignationSalary ds) {
        return repository.save(ds);
    }
}
