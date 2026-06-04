package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.*;
import com.group_project.wfms_backend.model.*;
import com.group_project.wfms_backend.repository.*;
import com.group_project.wfms_backend.security.UserDetailsImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplyRawMaterialService {

    private final SupplyRawMaterialRepository supplyRawMaterialRepository;
    private final SupplyRawMaterialDetailsRepository supplyRawMaterialDetailsRepository;
    private final SupplierRepository supplierRepository;
    private final RawMaterialItemRepository rawMaterialItemRepository;
    private final UserRepository userRepository;
    private final GRNRepository grnRepository;
    private final GrnDetailsRepository grnDetailsRepository;
    private final RawMaterialCuttingFeeRepository cuttingFeeRepository;
    private final ExpenseAccountRepository expenseAccountRepository;
    private final EmployeeRepository employeeRepository;
    private final ExpenseTypeRepository expenseTypeRepository;
    private final EntityManager entityManager;

    @Transactional
    public SupplyRawMaterialResponseDTO createSupplyRawMaterial(SupplyRawMaterialRequestDTO request) {
        // Fetch supplier from Supplier table
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new EntityNotFoundException("Supplier record not found with id: " + request.getSupplierId()));

        RawMaterialItem mainRmItem = rawMaterialItemRepository.findById(request.getRmId())
                .orElseThrow(() -> new EntityNotFoundException("Raw Material Item not found with id: " + request.getRmId()));

        User createdBy = null;
        if (request.getCreatedById() != null) {
            createdBy = userRepository.findById(request.getCreatedById())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getCreatedById()));
        }

        SupplyRawMaterial supply = new SupplyRawMaterial();
        supply.setSupplier(supplier);
        supply.setRawMaterialItem(mainRmItem);
        
        String generatedInvoice = generateInvoiceNumber();
        supply.setInvoiceNumber(generatedInvoice);
        supply.setSupplyDate(request.getSupplyDate());
        supply.setCreatedBy(createdBy);

        // Calculate Gross Total and Details
        BigDecimal grossTotal = BigDecimal.ZERO;
        List<SupplyRawMaterialDetails> details = request.getSupplyDetails().stream().map(detailRequest -> {
            SupplyRawMaterialDetails detail = new SupplyRawMaterialDetails();
            detail.setSupplyRawMaterial(supply);
            
            RawMaterialItem detailRmItem = rawMaterialItemRepository.findById(detailRequest.getRmId())
                    .orElseThrow(() -> new EntityNotFoundException("Raw Material Item not found for detail with id: " + detailRequest.getRmId()));
            
            detail.setRawMaterialItem(detailRmItem);
            detail.setLogNumber(detailRequest.getLogNumber());
            detail.setLengthFt(detailRequest.getLengthFt());
            detail.setGirthFt(detailRequest.getGirthFt());
            detail.setPrice(detailRequest.getPrice());
            
            // Formula: CFT = (L * G * G) / 2304
            BigDecimal quantityCft = detailRequest.getLengthFt()
                    .multiply(detailRequest.getGirthFt())
                    .multiply(detailRequest.getGirthFt())
                    .divide(new BigDecimal("2304"), 4, RoundingMode.HALF_UP);
            
            BigDecimal lineTotal = quantityCft.multiply(detailRequest.getPrice()).setScale(2, RoundingMode.HALF_UP);
            
            detail.setTotalQuantityCft(quantityCft);
            detail.setLineTotal(lineTotal);
            
            return detail;
        }).collect(Collectors.toList());

        grossTotal = details.stream()
                .map(SupplyRawMaterialDetails::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        supply.setSupplyDetails(details);
        supply.setTotalAmount(grossTotal);

        boolean isTreeSeller = "Tree Seller".equalsIgnoreCase(supplier.getSupCat());
        BigDecimal netAmount = grossTotal;

        BigDecimal transport = request.getTransport() != null ? request.getTransport() : BigDecimal.ZERO;
        BigDecimal cuttingFee = request.getCuttingFee() != null ? request.getCuttingFee() : BigDecimal.ZERO;
        
        supply.setTransport(transport);
        supply.setCuttingFee(cuttingFee);
        netAmount = grossTotal.subtract(transport).subtract(cuttingFee);
        
        if (cuttingFee.compareTo(BigDecimal.ZERO) > 0 && request.getCuttingFeeEmployeeId() != null) {
            Employee employee = employeeRepository.findById(request.getCuttingFeeEmployeeId())
                    .orElseThrow(() -> new EntityNotFoundException("Employee for cutting fee not found with id: " + request.getCuttingFeeEmployeeId()));
            
            RawMaterialCuttingFee cf = new RawMaterialCuttingFee();
            cf.setSupplyRawMaterial(supply);
            cf.setEmployee(employee);
            cf.setFee(cuttingFee);
            cf.setDate(request.getSupplyDate());
            cf.setRemarks("Auto-generated from Supply: " + request.getInvoiceNumber());
            
            if (supply.getCuttingFees() == null) {
                supply.setCuttingFees(new java.util.ArrayList<>());
            }
            supply.getCuttingFees().add(cf);
        }

        SupplyRawMaterial savedSupply = supplyRawMaterialRepository.save(supply);

        // 1. Auto-generate GRN
        GRN grn = new GRN();
        grn.setGrnNumber(generateGrnNumber());
        grn.setInvoiceNumber(savedSupply.getInvoiceNumber());
        grn.setSupplier(supplier);
        grn.setSupplyOrder(savedSupply);
        grn.setDate(request.getSupplyDate());
        grn.setAmount(grossTotal);
        grn.setTotalAmount(grossTotal);
        grn.setCreatedBy(createdBy);
        grn.setRemarks("Auto-generated for Supply ID: " + savedSupply.getSupplyId());
        GRN savedGrn = grnRepository.save(grn);

        // 2. Save GRN Details
        for (SupplyRawMaterialDetails detail : savedSupply.getSupplyDetails()) {
            GrnDetails grnDetail = new GrnDetails();
            grnDetail.setGrn(savedGrn);
            grnDetail.setSupplyRawMaterialDetails(detail);
            grnDetail.setGrnNumber(savedGrn.getGrnNumber());
            grnDetail.setDate(savedGrn.getDate());
            grnDetail.setAmount(detail.getLineTotal());
            grnDetailsRepository.save(grnDetail);
        }

        // 3. Record expense
        Expenseaccount savedExpense = tryRecordExpense(request, savedSupply, savedGrn, netAmount, supplier, createdBy);
        if (savedExpense != null) {
            savedGrn.setExpense(savedExpense);
            grnRepository.save(savedGrn);
        }

        SupplyRawMaterialResponseDTO response = mapToResponseDTO(savedSupply);
        response.setGrnId(savedGrn.getGrnId());
        response.setIsTreeSeller(isTreeSeller);
        response.setNetAmount(netAmount);
        return response;
    }

    @Transactional
    public SupplyRawMaterialResponseDTO updateSupplyRawMaterial(Integer id, SupplyRawMaterialRequestDTO request) {
        return createSupplyRawMaterial(request);
    }

    @Transactional(readOnly = true)
    public SupplyRawMaterialResponseDTO getSupplyRawMaterialById(Integer id) {
        SupplyRawMaterial supply = supplyRawMaterialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supply record not found with id: " + id));
        return mapToResponseDTO(supply);
    }

    @Transactional(readOnly = true)
    public List<SupplyRawMaterialResponseDTO> getAllSupplyRawMaterials() {
        return supplyRawMaterialRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupplyRawMaterialResponseDTO> getSupplyRawMaterialsForLoggedSupplier(UserDetailsImpl userDetails, String email) {
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        Supplier supplier = findSupplierByEmail(email)
                .or(() -> findSupplierForUser(userDetails, user, email))
                .orElse(null);
        System.out.println("Suppliyer Details : "+ supplier);

        if (supplier == null) {
            return Collections.emptyList();
        }

        return supplyRawMaterialRepository.findBySupplierSupId(supplier.getSupId()).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private Optional<Supplier> findSupplierByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }

        return supplierRepository.findByEmail(email.trim());
    }

    private Optional<Supplier> findSupplierForUser(UserDetailsImpl userDetails, User user, String email) {
        List<String> candidates = new ArrayList<>();
        addCandidate(candidates, email);
        addCandidate(candidates, userDetails.getEmail());
        addCandidate(candidates, userDetails.getUsername());

        if (user != null) {
            addCandidate(candidates, user.getEmail());
            addCandidate(candidates, user.getUsername());
            addCandidate(candidates, user.getPhoneNumber());
            addCandidate(candidates, user.getUserDetails());

            if (user.getUserDetails() != null) {
                for (String token : user.getUserDetails().split("[,;|\\n\\r]+")) {
                    addCandidate(candidates, token);
                }
            }
        }

        for (String candidate : candidates) {
            Optional<Supplier> supplier = supplierRepository.findByEmail(candidate)
                    .or(() -> supplierRepository.findBySupNameIgnoreCase(candidate))
                    .or(() -> supplierRepository.findByMobile(candidate))
                    .or(() -> supplierRepository.findByNic(candidate));

            if (supplier.isPresent()) {
                return supplier;
            }
        }

        return Optional.empty();
    }

    private void addCandidate(List<String> candidates, String value) {
        if (value != null && !value.trim().isEmpty()) {
            candidates.add(value.trim());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Expenseaccount tryRecordExpense(SupplyRawMaterialRequestDTO request,
                                  SupplyRawMaterial savedSupply,
                                  GRN savedGrn,
                                  BigDecimal netAmount,
                                  Supplier supplier,
                                  User createdBy) {
        try {
            ExpenseType expenseType = expenseTypeRepository.findByTypeName("Raw Material Purchase")
                    .or(() -> expenseTypeRepository.findByTypeName("Raw Material"))
                    .orElseGet(() -> {
                        ExpenseType newType = new ExpenseType();
                        newType.setTypeName("Raw Material Purchase");
                        newType.setDescription("Expenses related to Raw Material Procurement");
                        return expenseTypeRepository.save(newType);
                    });

            Expenseaccount expense = new Expenseaccount();
            expense.setExpenseType(expenseType);
            expense.setDate(request.getSupplyDate());
            expense.setAmount(netAmount);
            expense.setGrn(savedGrn);
            expense.setUser(createdBy);
            expense.setPaidTo(supplier.getSupName());
            expense.setDescription("Payment for Raw Material Supply: " + savedSupply.getInvoiceNumber() + 
                    " (GRN: " + savedGrn.getGrnNumber() + ", Supplier: " + supplier.getSupName() + ")");
            
            return expenseAccountRepository.save(expense);
        } catch (Exception e) {
            System.err.println("[WARN] Expense account entry skipped: " + e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Integer getGrnIdByInvoiceNumber(String invoiceNumber) {
        return grnRepository.findByInvoiceNumber(invoiceNumber)
                .map(GRN::getGrnId)
                .orElse(null);
    }

    @Transactional
    public void deleteSupplyRawMaterial(Integer id) {
        if (!supplyRawMaterialRepository.existsById(id)) {
            throw new EntityNotFoundException("Supply record not found with id: " + id);
        }
        supplyRawMaterialRepository.deleteById(id);
    }

    private String generateInvoiceNumber() {
        try {
            Object result = entityManager
                    .createNativeQuery("CALL Generate_Invoice_Number()")
                    .getSingleResult();
            if (result instanceof Object[]) {
                return String.valueOf(((Object[]) result)[0]);
            }
            return String.valueOf(result);
        } catch (Exception e) {
            String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
            long count = supplyRawMaterialRepository.count() + 1;
            return String.format("INV%s%04d", datePart, count);
        }
    }

    private String generateGrnNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = grnRepository.count() + 1;
        return String.format("GRN-%s-%04d", datePart, count);
    }

    private SupplyRawMaterialResponseDTO mapToResponseDTO(SupplyRawMaterial supply) {
        SupplyRawMaterialResponseDTO dto = new SupplyRawMaterialResponseDTO();
        dto.setSupplyId(supply.getSupplyId());
        
        if (supply.getSupplier() != null) {
            dto.setSupplierId(supply.getSupplier().getSupId());
            dto.setSupplierName(supply.getSupplier().getSupName());
        }
        
        // Find associated GRN ID
        grnRepository.findBySupplyOrder(supply).ifPresent(grn -> dto.setGrnId(grn.getGrnId()));
        
        if (supply.getRawMaterialItem() != null) {
            dto.setRmId(supply.getRawMaterialItem().getRmId());
            dto.setRmName(supply.getRawMaterialItem().getRmName());
        }
        
        dto.setInvoiceNumber(supply.getInvoiceNumber());
        dto.setTotalAmount(supply.getTotalAmount());
        dto.setTransport(supply.getTransport());
        
        // Calculate total CFT from details
        if (supply.getSupplyDetails() != null) {
            BigDecimal totalCft = supply.getSupplyDetails().stream()
                .map(d -> d.getTotalQuantityCft() != null ? d.getTotalQuantityCft() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setTotalQuantityCft(totalCft);
            
            dto.setSupplyDetails(supply.getSupplyDetails().stream()
                .map(this::mapToDetailResponseDTO)
                .collect(Collectors.toList()));
        }
        
        dto.setSupplyDate(supply.getSupplyDate());
        
        // Net amount calculation for dashboard
        BigDecimal cuttingFee = supply.getCuttingFee() != null ? supply.getCuttingFee() : BigDecimal.ZERO;
        BigDecimal transport = supply.getTransport() != null ? supply.getTransport() : BigDecimal.ZERO;
        dto.setNetAmount(supply.getTotalAmount().subtract(transport).subtract(cuttingFee));
        
        return dto;
    }

    private SupplyRawMaterialDetailResponseDTO mapToDetailResponseDTO(SupplyRawMaterialDetails detail) {
        SupplyRawMaterialDetailResponseDTO dto = new SupplyRawMaterialDetailResponseDTO();
        dto.setId(detail.getId());
        if (detail.getRawMaterialItem() != null) {
            dto.setRmId(detail.getRawMaterialItem().getRmId());
            dto.setRmName(detail.getRawMaterialItem().getRmName());
        }
        dto.setLogNumber(detail.getLogNumber());
        dto.setLengthFt(detail.getLengthFt());
        dto.setGirthFt(detail.getGirthFt());
        dto.setTotalQuantityCft(detail.getTotalQuantityCft());
        dto.setPrice(detail.getPrice());
        dto.setLineTotal(detail.getLineTotal());
        return dto;
    }
}
