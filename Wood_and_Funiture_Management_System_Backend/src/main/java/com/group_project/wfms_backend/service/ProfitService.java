package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.ProfitSummaryDTO;
import com.group_project.wfms_backend.repository.ExpenseAccountRepository;
import com.group_project.wfms_backend.repository.IncomeAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class ProfitService {
    @Autowired
    private IncomeAccountRepository incomeRepo;

    @Autowired
    private ExpenseAccountRepository expenseRepo;

    public ProfitSummaryDTO getProfitSummary(LocalDate start, LocalDate end) {
        // මුළු ආදායම ලබා ගැනීම (null නම් 0.00 ලෙස සලකයි)
        BigDecimal totalIncome = incomeRepo.getTotalIncome(start, end);
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;

        // මුළු වියදම ලබා ගැනීම
        BigDecimal totalExpense = expenseRepo.getTotalExpense(start, end);
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        // ශුද්ධ ලාභය = ආදායම - වියදම
        BigDecimal netProfit = totalIncome.subtract(totalExpense);

        String period = start.toString() + " to " + end.toString();

        return new ProfitSummaryDTO(totalIncome, totalExpense, netProfit, period);
    }
}
