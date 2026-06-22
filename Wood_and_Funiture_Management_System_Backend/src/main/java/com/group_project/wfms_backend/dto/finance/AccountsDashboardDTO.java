package com.group_project.wfms_backend.dto.finance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.group_project.wfms_backend.dto.auth.ExpenseAccountDTO;
import com.group_project.wfms_backend.dto.auth.IncomeAccountDTO;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountsDashboardDTO {
    // 1. Balance Sheet - Assets
    private BigDecimal cashAndEquivalents;
    private BigDecimal accountsReceivable;
    private BigDecimal finishedProductsValue;
    private BigDecimal rawMaterialsValue;
    private BigDecimal loansReceivable;

    // 1. Balance Sheet - Liabilities
    private BigDecimal pendingRawMaterialPayments;
    private BigDecimal supplierPayables;
    private BigDecimal shortTermLoansPayable;
    private BigDecimal otherLiabilities;

    // 2. Income and Expense Summary
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;

    // 3. Inventory Status
    private List<ProductStockStatusDTO> finishedProducts;
    private List<RawMaterialStatusDTO> rawMaterials;

    // 4. Financial Indicators
    private BigDecimal totalCurrentAssets;
    private BigDecimal totalCurrentLiabilities;
    private BigDecimal workingCapital;
    private BigDecimal totalAssets;

    // 5. Incomes and Expenses Details
    private List<IncomeAccountDTO> incomes;
    private List<ExpenseAccountDTO> expenses;
}
