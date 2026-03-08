package com.group_project.wfms_backend.service;


import com.group_project.wfms_backend.dto.auth.QuotationDetailsRequestDTO;
import com.group_project.wfms_backend.dto.auth.QuotationDetailsResponseDTO;
import com.group_project.wfms_backend.dto.auth.QuotationRequestDTO;
import com.group_project.wfms_backend.dto.auth.QuotationResponseDTO;
import com.group_project.wfms_backend.model.*;
import com.group_project.wfms_backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final QuotationDetailsRepository quotationDetailsRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ProuctCategoryRepository productCategoryRepository;

    // ─── CREATE ────────────────────────────────────────────────────────────────

    public QuotationResponseDTO createQuotation(QuotationRequestDTO requestDTO) {
        Customer customer = customerRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Customer not found with ID: " + requestDTO.getCustomerId()));

        User createdBy = null;
        if (requestDTO.getCreatedBy() != null) {
            createdBy = userRepository.findById(requestDTO.getCreatedBy())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "User not found with ID: " + requestDTO.getCreatedBy()));
        }

        Quotation quotation = new Quotation();
        quotation.setCustomer(customer);
        quotation.setQuotationDate(requestDTO.getQuotationDate());
        quotation.setValidUntil(requestDTO.getValidUntil());
        quotation.setStatus(requestDTO.getStatus() != null
                ? requestDTO.getStatus() : QuotationStatus.PENDING);
        quotation.setRemarks(requestDTO.getRemarks());
        quotation.setCreatedBy(createdBy);

        if (requestDTO.getDetails() != null && !requestDTO.getDetails().isEmpty()) {
            List<QuotationDetails> detailsList = mapToDetailEntities(
                    requestDTO.getDetails(), quotation);
            quotation.setDetails(detailsList);
            quotation.setTotalAmount(calculateTotal(detailsList));
        } else {
            quotation.setTotalAmount(BigDecimal.ZERO);
        }

        return mapToResponseDTO(quotationRepository.save(quotation));
    }

    // ─── READ ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public QuotationResponseDTO getQuotationById(Integer quotationId) {
        return mapToResponseDTO(findQuotationById(quotationId));
    }

    @Transactional(readOnly = true)
    public List<QuotationResponseDTO> getAllQuotations() {
        return quotationRepository.findAllOrderByDateDesc()
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuotationResponseDTO> getQuotationsByCustomer(Integer customerId) {
        return quotationRepository.findByCustomer_CusId(customerId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuotationResponseDTO> getQuotationsByStatus(QuotationStatus status) {
        return quotationRepository.findByStatus(status)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuotationResponseDTO> getQuotationsByDateRange(
            LocalDate startDate, LocalDate endDate) {
        return quotationRepository.findByQuotationDateBetween(startDate, endDate)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    public QuotationResponseDTO updateQuotation(
            Integer quotationId, QuotationRequestDTO requestDTO) {

        Quotation quotation = findQuotationById(quotationId);

        Customer customer = customerRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Customer not found with ID: " + requestDTO.getCustomerId()));

        quotation.setCustomer(customer);
        quotation.setQuotationDate(requestDTO.getQuotationDate());
        quotation.setValidUntil(requestDTO.getValidUntil());
        quotation.setStatus(requestDTO.getStatus());
        quotation.setRemarks(requestDTO.getRemarks());

        if (requestDTO.getCreatedBy() != null) {
            User user = userRepository.findById(requestDTO.getCreatedBy())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "User not found with ID: " + requestDTO.getCreatedBy()));
            quotation.setCreatedBy(user);
        }

        quotation.getDetails().clear();
        if (requestDTO.getDetails() != null && !requestDTO.getDetails().isEmpty()) {
            List<QuotationDetails> detailsList = mapToDetailEntities(
                    requestDTO.getDetails(), quotation);
            quotation.getDetails().addAll(detailsList);
            quotation.setTotalAmount(calculateTotal(detailsList));
        } else {
            quotation.setTotalAmount(BigDecimal.ZERO);
        }

        return mapToResponseDTO(quotationRepository.save(quotation));
    }

    public QuotationResponseDTO updateQuotationStatus(
            Integer quotationId, QuotationStatus status) {
        Quotation quotation = findQuotationById(quotationId);
        quotation.setStatus(status);
        return mapToResponseDTO(quotationRepository.save(quotation));
    }

    // ─── CONVERT TO ORDER ──────────────────────────────────────────────────────

    public QuotationResponseDTO convertQuotationToOrder(Integer quotationId) {
        Quotation quotation = findQuotationById(quotationId);
        if (quotation.getStatus() != QuotationStatus.APPROVED) {
            throw new IllegalStateException(
                    "Only Approved quotations can be converted to an order.");
        }
        quotation.setStatus(QuotationStatus.CONVERTED);
        return mapToResponseDTO(quotationRepository.save(quotation));
    }

    // ─── DELETE ────────────────────────────────────────────────────────────────

    public void deleteQuotation(Integer quotationId) {
        quotationRepository.delete(findQuotationById(quotationId));
    }

    // ─── PRIVATE HELPERS ───────────────────────────────────────────────────────

    private Quotation findQuotationById(Integer quotationId) {
        return quotationRepository.findById(quotationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Quotation not found with ID: " + quotationId));
    }

    private List<QuotationDetails> mapToDetailEntities(
            List<QuotationDetailsRequestDTO> dtoList, Quotation quotation) {
        return dtoList.stream().map(dto -> {
            ProductCategory category = productCategoryRepository
                    .findById(dto.getProductCatId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Product category not found with ID: " + dto.getProductCatId()));
            QuotationDetails detail = new QuotationDetails();
            detail.setQuotation(quotation);
            detail.setProductCategory(category);
            detail.setName(dto.getName());
            detail.setQuantity(dto.getQuantity());
            detail.setPrice(dto.getPrice());
            return detail;
        }).collect(Collectors.toList());
    }

    private BigDecimal calculateTotal(List<QuotationDetails> details) {
        return details.stream()
                .map(d -> d.getQuantity().multiply(d.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private QuotationResponseDTO mapToResponseDTO(Quotation quotation) {
        QuotationResponseDTO dto = new QuotationResponseDTO();
        dto.setQuotationId(quotation.getQuotationId());
        dto.setTotalAmount(quotation.getTotalAmount());
        dto.setStatus(quotation.getStatus());
        dto.setQuotationDate(quotation.getQuotationDate());
        dto.setValidUntil(quotation.getValidUntil());
        dto.setRemarks(quotation.getRemarks());

        if (quotation.getCustomer() != null) {
            dto.setCustomerId(quotation.getCustomer().getCusId());
            dto.setCustomerName(quotation.getCustomer().getCusName());
        }
        if (quotation.getCreatedBy() != null) {
            dto.setCreatedBy(quotation.getCreatedBy().getUserId());
            dto.setCreatedByName(quotation.getCreatedBy().getUsername());
        }
        if (quotation.getDetails() != null) {
            dto.setDetails(quotation.getDetails().stream()
                    .map(this::mapDetailToResponseDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    private QuotationDetailsResponseDTO mapDetailToResponseDTO(QuotationDetails detail) {
        QuotationDetailsResponseDTO dto = new QuotationDetailsResponseDTO();
        dto.setDetailsId(detail.getDetailsId());
        dto.setQuotationId(detail.getQuotation().getQuotationId());
        dto.setName(detail.getName());
        dto.setQuantity(detail.getQuantity());
        dto.setPrice(detail.getPrice());
        dto.setLineTotal(detail.getQuantity().multiply(detail.getPrice()));

        if (detail.getProductCategory() != null) {
            dto.setProductCatId(detail.getProductCategory().getProductCatid());
            dto.setProductCatName(detail.getProductCategory().getMaterialCategory());
        }
        return dto;
    }
}