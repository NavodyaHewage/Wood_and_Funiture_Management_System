package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.IncomeAccountDTO;
import com.group_project.wfms_backend.model.IncomeAccount;
import com.group_project.wfms_backend.model.User;
import com.group_project.wfms_backend.repository.IncomeAccountRepository;
import com.group_project.wfms_backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeAccountService {
    @Autowired
    private IncomeAccountRepository incomeAccountRepository;
    @Autowired private UserRepository userRepository;

    public IncomeAccountDTO createManualIncome(IncomeAccountDTO dto) {
        IncomeAccount income = new IncomeAccount();
        income.setDate(dto.getDate());
        income.setAmount(dto.getAmount());
        income.setDescription(dto.getDescription());

        if (dto.getCreatedById() != null) {
            User creator = userRepository.findById(dto.getCreatedById())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            income.setCreatedBy(creator);
        }

        IncomeAccount saved = incomeAccountRepository.save(income);
        return convertToDTO(saved);
    }

    public List<IncomeAccountDTO> getAllIncomeRecords() {
        return incomeAccountRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private IncomeAccountDTO convertToDTO(IncomeAccount entity) {
        IncomeAccountDTO dto = new IncomeAccountDTO();
        dto.setIncomeId(entity.getIncomeId());
        dto.setDate(entity.getDate());
        dto.setAmount(entity.getAmount());
        dto.setDescription(entity.getDescription());

        if (entity.getReceipt() != null) {
            dto.setReceiptId(entity.getReceipt().getReceiptId());
        }

        if (entity.getCreatedBy() != null) {
            // User entity එකේ PK එක userId ද නැත්නම් id ද කියා පරීක්ෂා කර මෙතන හදන්න
            dto.setCreatedById(entity.getCreatedBy().getUserId());
        }
        return dto;
    }

}
