package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.model.DesignationSalary;
import com.group_project.wfms_backend.service.DesignationSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/designation-salary")
@CrossOrigin(origins = "*")
public class DesignationSalaryController {

    @Autowired
    private DesignationSalaryService service;

    @GetMapping
    public List<DesignationSalary> getAll() {
        return service.getAll();
    }

    @PostMapping
    public DesignationSalary save(@RequestBody DesignationSalary ds) {
        return service.save(ds);
    }

}
