package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.EquityAccountDTO;
import com.group_project.wfms_backend.service.EquityAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equity")
@CrossOrigin
public class EquityAccountController {
    @Autowired
    private EquityAccountService equityService;

    @PostMapping("/save")
    public ResponseEntity<EquityAccountDTO> save(@RequestBody EquityAccountDTO dto) {
        return ResponseEntity.ok(equityService.saveOrUpdate(dto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<EquityAccountDTO>> getAll() {
        return ResponseEntity.ok(equityService.getAll());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        equityService.delete(id);
        return ResponseEntity.ok("Deleted");
    }
}
