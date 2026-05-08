package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.model.Quotation;
import com.group_project.wfms_backend.model.QuotationStatus;
import com.group_project.wfms_backend.repository.QuotationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuotationReminderService {

    private final QuotationRepository quotationRepository;

    /**
     * Runs every day at 9:00 AM to check for expiring and expired quotations.
     * Logic:
     * - Expired: validUntil < today AND status is PENDING
     * - Expiring Soon: validUntil is within next 3 days AND status is PENDING
     */
    @Scheduled(cron = "0 0 9 * * *")
    public String checkQuotationExpirations() {
        log.info("Starting scheduled quotation expiry check...");
        LocalDate today = LocalDate.now();
        LocalDate threeDaysFromNow = today.plusDays(3);
        StringBuilder summary = new StringBuilder("Quotation Expiry Check Summary (" + today + "):\n");

        // 1. Find Expired
        List<Quotation> expired = quotationRepository.findExpiredQuotations(today, QuotationStatus.PENDING);
        summary.append("- Expired: ").append(expired.size()).append("\n");
        if (!expired.isEmpty()) {
            log.warn("Found {} expired quotations.", expired.size());
            expired.forEach(q -> log.info("EXPIRED: #{} for customer {}", q.getQuotationId(), q.getCustomer().getCusName()));
        }

        // 2. Find Expiring Soon
        List<Quotation> expiringSoon = quotationRepository.findExpiringSoon(QuotationStatus.PENDING, today, threeDaysFromNow);
        summary.append("- Expiring Soon: ").append(expiringSoon.size()).append("\n");
        if (!expiringSoon.isEmpty()) {
            log.info("Found {} expiring soon quotations.", expiringSoon.size());
            expiringSoon.forEach(q -> log.info("SOON: #{} for customer {}", q.getQuotationId(), q.getCustomer().getCusName()));
        }
        
        log.info("Quotation expiry check completed.");
        return summary.toString();
    }

    // Example of future extension method
    /*
    private void sendEmailNotification(Quotation quotation, String type) {
        // Implementation for email sending logic
    }
    */
}
