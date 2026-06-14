package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.ProfitSummaryDTO;
import com.group_project.wfms_backend.dto.finance.AccountsDashboardDTO;
import com.group_project.wfms_backend.dto.finance.ProductStockStatusDTO;
import com.group_project.wfms_backend.dto.finance.RawMaterialStatusDTO;
import com.group_project.wfms_backend.dto.auth.ExpenseAccountDTO;
import com.group_project.wfms_backend.dto.auth.IncomeAccountDTO;
import com.group_project.wfms_backend.model.*;
import com.group_project.wfms_backend.repository.AssetAccountRepository;
import com.group_project.wfms_backend.repository.ExpenseAccountRepository;
import com.group_project.wfms_backend.repository.IncomeAccountRepository;
import com.group_project.wfms_backend.repository.ProductStockRepository;
import com.group_project.wfms_backend.repository.SupplyRawMaterialDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountsDashboardService {

    @Autowired
    private AssetAccountRepository assetAccountRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    private SupplyRawMaterialDetailsRepository rawMaterialDetailsRepository;

    @Autowired
    private ProfitService profitService;

    @Autowired
    private IncomeAccountRepository incomeAccountRepository;

    @Autowired
    private ExpenseAccountRepository expenseAccountRepository;

    public AccountsDashboardDTO getDashboardData(LocalDate start, LocalDate end) {
        AccountsDashboardDTO dto = new AccountsDashboardDTO();

        // 1. Calculate Assets
        BigDecimal cashAndEquivalents = BigDecimal.ZERO;
        BigDecimal accountsReceivable = BigDecimal.ZERO; // Placeholder if not explicitly tracked
        BigDecimal loansReceivable = BigDecimal.ZERO; // Placeholder if not explicitly tracked
        
        List<AssetAccount> assets = assetAccountRepository.findAll();
        for (AssetAccount asset : assets) {
            if (asset.getAssetType() == AssetType.Cash) {
                cashAndEquivalents = cashAndEquivalents.add(asset.getCurrentValue() != null ? asset.getCurrentValue() : asset.getPurchaseValue());
            }
        }
        
        // Product Stock (Finished Products)
        List<ProductStock> productStocks = productStockRepository.findAll();
        List<ProductStockStatusDTO> finishedProducts = new ArrayList<>();
        BigDecimal finishedProductsValue = BigDecimal.ZERO;
        
        for (ProductStock stock : productStocks) {
            BigDecimal unitPrice = stock.getProductCategory().getUnitPrice() != null ? stock.getProductCategory().getUnitPrice() : BigDecimal.ZERO;
            BigDecimal totalStockValue = stock.getAvailableQuantity().multiply(unitPrice);
            finishedProductsValue = finishedProductsValue.add(totalStockValue);
            
            finishedProducts.add(new ProductStockStatusDTO(
                    stock.getProductCategory().getMaterialCategory(), // product name
                    stock.getProductCategory().getMaterialCategory(), // product category
                    stock.getAvailableQuantity(),
                    unitPrice,
                    totalStockValue
            ));
        }

        // Raw Materials Stock
        List<SupplyRawMaterialDetails> rawMaterials = rawMaterialDetailsRepository.findAll();
        List<RawMaterialStatusDTO> rawMaterialDTOs = new ArrayList<>();
        BigDecimal rawMaterialsValue = BigDecimal.ZERO;
        BigDecimal pendingRawMaterialPayments = BigDecimal.ZERO;
        
        for (SupplyRawMaterialDetails rm : rawMaterials) {
            BigDecimal lineTotal = rm.getLineTotal() != null ? rm.getLineTotal() : BigDecimal.ZERO;
            
            // Assuming PENDING means available stock that is not yet cut/used
            if (rm.getStatus() == CuttingStatus.PENDING) {
                rawMaterialsValue = rawMaterialsValue.add(lineTotal);
                
                rawMaterialDTOs.add(new RawMaterialStatusDTO(
                        rm.getRawMaterialItem() != null ? rm.getRawMaterialItem().getRmName() : "Unknown",
                        rm.getStatus().toString(),
                        rm.getTotalQuantityCft(),
                        rm.getPrice(),
                        lineTotal
                ));
            }
            // For liabilities, we could assume some are unpaid. Placeholder logic:
            // If payment status was tracked, we would sum it here.
        }

        dto.setCashAndEquivalents(cashAndEquivalents);
        dto.setAccountsReceivable(accountsReceivable);
        dto.setLoansReceivable(loansReceivable);
        dto.setFinishedProductsValue(finishedProductsValue);
        dto.setRawMaterialsValue(rawMaterialsValue);

        // 2. Calculate Liabilities
        BigDecimal supplierPayables = BigDecimal.ZERO;
        BigDecimal shortTermLoansPayable = BigDecimal.ZERO;
        BigDecimal otherLiabilities = BigDecimal.ZERO;
        
        dto.setPendingRawMaterialPayments(pendingRawMaterialPayments);
        dto.setSupplierPayables(supplierPayables);
        dto.setShortTermLoansPayable(shortTermLoansPayable);
        dto.setOtherLiabilities(otherLiabilities);

        // 3. Income and Expense Summary
        ProfitSummaryDTO profitSummary = profitService.getProfitSummary(start, end);
        dto.setTotalIncome(profitSummary.getTotalIncome());
        dto.setTotalExpenses(profitSummary.getTotalExpense());
        dto.setNetProfit(profitSummary.getNetProfit());

        // 4. Inventory Status
        dto.setFinishedProducts(finishedProducts);
        dto.setRawMaterials(rawMaterialDTOs);

        // Fetch Incomes
        List<IncomeAccount> incomeEntities = incomeAccountRepository.findByDateBetween(start, end);
        List<IncomeAccountDTO> incomes = new ArrayList<>();
        for (IncomeAccount inc : incomeEntities) {
            IncomeAccountDTO incDto = new IncomeAccountDTO();
            incDto.setIncomeId(inc.getIncomeId());
            incDto.setDate(inc.getDate());
            incDto.setAmount(inc.getAmount());
            incDto.setDescription(inc.getDescription());
            incDto.setReceiptId(inc.getReceipt() != null ? inc.getReceipt().getReceiptId() : null);
            incDto.setCreatedById(inc.getCreatedBy() != null ? inc.getCreatedBy().getUserId() : null);
            incomes.add(incDto);
        }
        dto.setIncomes(incomes);

        // Fetch Expenses
        List<Expenseaccount> expenseEntities = expenseAccountRepository.findByDateBetween(start, end);
        List<ExpenseAccountDTO> expenses = new ArrayList<>();
        for (Expenseaccount exp : expenseEntities) {
            ExpenseAccountDTO expDto = new ExpenseAccountDTO();
            expDto.setExpenseId(exp.getExpenseId());
            expDto.setDate(exp.getDate());
            expDto.setAmount(exp.getAmount());
            expDto.setDescription(exp.getDescription());
            expDto.setPaidTo(exp.getPaidTo());
            expDto.setRemarks(exp.getRemarks());
            expDto.setExpenseTypeId(exp.getExpenseType() != null ? exp.getExpenseType().getExpenseTypeId() : null);
            expDto.setGrnId(exp.getGrn() != null ? exp.getGrn().getGrnId() : null);
            expDto.setUserId(exp.getUser() != null ? exp.getUser().getUserId() : null);
            expenses.add(expDto);
        }
        dto.setExpenses(expenses);

        // 5. Financial Indicators
        BigDecimal totalCurrentAssets = cashAndEquivalents.add(accountsReceivable).add(finishedProductsValue).add(rawMaterialsValue).add(loansReceivable);
        BigDecimal totalCurrentLiabilities = pendingRawMaterialPayments.add(supplierPayables).add(shortTermLoansPayable).add(otherLiabilities);
        BigDecimal workingCapital = totalCurrentAssets.subtract(totalCurrentLiabilities);
        BigDecimal totalAssets = totalCurrentAssets; // Adding non-current assets if necessary later

        dto.setTotalCurrentAssets(totalCurrentAssets);
        dto.setTotalCurrentLiabilities(totalCurrentLiabilities);
        dto.setWorkingCapital(workingCapital);
        dto.setTotalAssets(totalAssets);

        return dto;
    }
}
