package com.group_project.wfms_backend.service;


import com.group_project.wfms_backend.dto.auth.EquityAccountDTO;
import com.group_project.wfms_backend.model.EquityAccount;
import com.group_project.wfms_backend.model.EquityType;
import com.group_project.wfms_backend.model.User;
import com.group_project.wfms_backend.repository.EquityAccountRepository;
import com.group_project.wfms_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class EquityAccountService {
    @Autowired
    private EquityAccountRepository equityRepository;

    @Autowired
    private UserRepository userRepository; // මෙහි findById(Integer) වැඩ කරයි

    public EquityAccountDTO saveOrUpdate(EquityAccountDTO dto) {
        EquityAccount account = new EquityAccount();

        if (dto.getEquityId() != null) {
            account = equityRepository.findById(dto.getEquityId())
                    .orElseThrow(() -> new RuntimeException("Account not found"));
        }

        account.setDate(dto.getDate());
        account.setAmount(dto.getAmount());
        account.setDescription(dto.getDescription());

        if (dto.getType() != null) {
            account.setType(EquityType.valueOf(dto.getType()));
        }

        // User සොයන්නේ Integer ID එකෙන්
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            account.setCreatedBy(user);
        }

        EquityAccount saved = equityRepository.save(account);
        return mapToDTO(saved);
    }

    public List<EquityAccountDTO> getAll() {
        return equityRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void delete(Integer id) {
        equityRepository.deleteById(id);
    }

    private EquityAccountDTO mapToDTO(EquityAccount account) {
        EquityAccountDTO dto = new EquityAccountDTO();
        dto.setEquityId(account.getEquityId());
        dto.setDate(account.getDate());
        dto.setAmount(account.getAmount());
        dto.setDescription(account.getDescription());
        dto.setType(account.getType().name());

        if (account.getCreatedBy() != null) {
            dto.setUserId(account.getCreatedBy().getUserId()); // මෙහිදී Integer values ගැලපේ
        }
        return dto;
    }}
