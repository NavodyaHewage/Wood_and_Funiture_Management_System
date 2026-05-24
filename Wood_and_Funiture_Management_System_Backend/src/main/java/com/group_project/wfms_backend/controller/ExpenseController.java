package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.ExpenseAccountDTO;
import com.group_project.wfms_backend.dto.auth.ExpenseTypeDTO;
import com.group_project.wfms_backend.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
@CrossOrigin(origins = "*") // Frontend එකත් එක්ක ලේසියෙන් Connect වෙන්න
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    // 0. සියලුම වියදම් වර්ග ලබා ගැනීම (Read All Expense Types)
    @GetMapping("/types")
    public ResponseEntity<List<ExpenseTypeDTO>> getAllExpenseTypes() {
        List<ExpenseTypeDTO> types = expenseService.getAllExpenseTypes();
        return new ResponseEntity<>(types, HttpStatus.OK);
    }

    // 1. සියලුම වියදම් ලබා ගැනීම (Read All)
    @GetMapping("/all")
    public ResponseEntity<List<ExpenseAccountDTO>> getAllExpenses() {
        List<ExpenseAccountDTO> expenses = expenseService.getAllExpenses();
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    // 2. අලුත් වියදමක් ඇතුළත් කිරීම (Create)
    @PostMapping("/save")
    public ResponseEntity<ExpenseAccountDTO> createExpense(@RequestBody ExpenseAccountDTO dto) {
        ExpenseAccountDTO savedExpense = expenseService.saveExpense(dto);
        return new ResponseEntity<>(savedExpense, HttpStatus.CREATED);
    }

    // 3. පවතින වියදමක් යාවත්කාලීන කිරීම (Update)
    @PutMapping("/update/{id}")
    public ResponseEntity<ExpenseAccountDTO> updateExpense(@PathVariable Integer id, @RequestBody ExpenseAccountDTO dto) {
        dto.setExpenseId(id); // URL එකේ එන ID එක DTO එකට සෙට් කිරීම
        ExpenseAccountDTO updatedExpense = expenseService.saveExpense(dto);
        return new ResponseEntity<>(updatedExpense, HttpStatus.OK);
    }

    // 4. වියදමක් මකා දැමීම (Delete)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Integer id) {
        try {
            expenseService.deleteExpense(id);
            return new ResponseEntity<>("Expense deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
