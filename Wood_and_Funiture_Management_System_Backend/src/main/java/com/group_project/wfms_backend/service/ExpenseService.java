package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.ExpenseAccountDTO;
import com.group_project.wfms_backend.model.Expenseaccount;
import com.group_project.wfms_backend.repository.ExpenseAccountRepository;
import com.group_project.wfms_backend.repository.ExpenseTypeRepository;
import com.group_project.wfms_backend.repository.GRNRepository;
import com.group_project.wfms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class ExpenseService {
    @Autowired
    private ExpenseAccountRepository expenseRepo;

    @Autowired
    private ExpenseTypeRepository typeRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private GRNRepository grnRepo;

    // සියලුම වියදම් ලබා ගැනීම
    public List<ExpenseAccountDTO> getAllExpenses() {
        return expenseRepo.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // අලුත් වියදමක් ඇතුළත් කිරීම හෝ යාවත්කාලීන කිරීම
    public ExpenseAccountDTO saveExpense(ExpenseAccountDTO dto) {
        Expenseaccount entity = mapToEntity(dto);
        Expenseaccount savedEntity = expenseRepo.save(entity);
        return mapToDTO(savedEntity);
    }

    // වියදමක් මකා දැමීම
    public void deleteExpense(Integer id) {
        if (expenseRepo.existsById(id)) {
            expenseRepo.deleteById(id);
        } else {
            throw new RuntimeException("Expense record not found with id: " + id);
        }
    }

    // Entity එකක් DTO එකක් බවට පත් කිරීම (Mapping)
    private ExpenseAccountDTO mapToDTO(Expenseaccount entity) {
        ExpenseAccountDTO dto = new ExpenseAccountDTO();
        dto.setExpenseId(entity.getExpenseId());
        dto.setDate(entity.getDate());
        dto.setAmount(entity.getAmount());
        dto.setDescription(entity.getDescription());

        dto.setExpenseTypeId(entity.getExpenseType().getExpenseTypeId());
        dto.setUserId(entity.getUser().getUserId());

        // GRN එකක් තිබේ නම් පමණක් ID එක සෙට් කරන්න
        if (entity.getGrn() != null) {
            dto.setGrnId(entity.getGrn().getGrnId());
        }

        return dto;
    }

    // DTO එකක් Entity එකක් බවට පත් කිරීම (Mapping)
    private Expenseaccount mapToEntity(ExpenseAccountDTO dto) {
        Expenseaccount entity = new Expenseaccount();

        // Update කරන අවස්ථාවකදී පරණ ID එක ලබා දීම
        if (dto.getExpenseId() != null) {
            entity.setExpenseId(dto.getExpenseId());
        }

        entity.setDate(dto.getDate());
        entity.setAmount(dto.getAmount());
        entity.setDescription(dto.getDescription());

        // Foreign Key සම්බන්දතා පරීක්ෂා කිරීම
        entity.setExpenseType(typeRepo.findById(dto.getExpenseTypeId())
                .orElseThrow(() -> new RuntimeException("Expense Type ID invalid")));

        entity.setUser(userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User ID invalid")));

        // GRN එක Optional නිසා එය තිබේ නම් පමණක් සොයා බලන්න
        if (dto.getGrnId() != null) {
            entity.setGrn(grnRepo.findById(dto.getGrnId())
                    .orElseThrow(() -> new RuntimeException("GRN ID invalid")));
        }

        return entity;
    }
}
