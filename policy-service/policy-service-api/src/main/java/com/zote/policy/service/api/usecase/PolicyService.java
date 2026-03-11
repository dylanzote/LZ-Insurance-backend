package com.zote.policy.service.api.usecase;

import com.zote.policy.service.api.controller.PolicyApi;
import com.zote.policy.service.api.request.*;
import com.zote.policy.service.api.response.*;
import com.zote.policy.service.domain.models.ComplianceViolation;
import com.zote.policy.service.domain.models.PolicyAuditLog;
import com.zote.policy.service.domain.models.PolicyStatusHistory;
import com.zote.policy.service.domain.models.data.PageParams;
import com.zote.policy.service.domain.ports.inbound.PolicyPort;
import com.zote.policy.service.domain.ports.outbound.PolicyDefaultsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyService implements PolicyApi {

    private final PolicyPort policyPort;
    private final PolicyDefaultsPort policyDefaultsPort;

    @Override
    public PolicyResponse createPolicyFromQuote(CreatePolicyFromQuoteRequest request) {
        log.info("Creating policy from quote: {}", request.getQuoteId());
        return PolicyResponse.fromPolicy(policyPort.createPolicyFromQuote(request.getQuoteId()));
    }

    @Override
    public PolicyResponse issuePolicy(IssuePolicyRequest request) {
        log.info("Issuing policy: {}", request.getPolicyId());
        return PolicyResponse.fromPolicy(policyPort.issuePolicy(request.toData()));
    }

    @Override
    public PaymentResponse recordPayment(RecordPaymentRequest request) {
        log.info("Recording payment for policy: {}", request.getPolicyId());
        return PaymentResponse.fromPayment(policyPort.recordPayment(request.toData()));
    }

    @Override
    public PaymentResponse recordFailedPayment(RecordFailedPaymentRequest request) {
        log.info("Recording failed payment for policy: {}", request.getPolicyId());
        return PaymentResponse.fromPayment(policyPort.recordFailedPayment(request.toData()));
    }

    @Override
    public int runPaymentMonitoring() {
        log.info("Running payment monitoring");
        return policyPort.runPaymentMonitoring();
    }

    @Override
    public PolicyResponse endorsePolicy(EndorsePolicyRequest request) {
        log.info("Endorsing policy: {}", request.getPolicyId());
        return PolicyResponse.fromPolicy(policyPort.endorsePolicy(request.toData()));
    }

    @Override
    public EndorsementRequestResponse requestEndorsement(RequestEndorsementRequest request) {
        log.info("Requesting endorsement for policy: {}", request.getPolicyId());
        return EndorsementRequestResponse.fromEndorsementRequest(policyPort.requestEndorsement(request.toData()));
    }

    @Override
    public PolicyResponse approveEndorsement(ApproveEndorsementRequest request) {
        log.info("Approving endorsement request: {}", request.getEndorsementRequestId());
        return PolicyResponse.fromPolicy(policyPort.approveEndorsement(request.toData()));
    }

    @Override
    public EndorsementRequestResponse rejectEndorsement(RejectEndorsementRequest request) {
        log.info("Rejecting endorsement request: {}", request.getEndorsementRequestId());
        return EndorsementRequestResponse.fromEndorsementRequest(policyPort.rejectEndorsement(request.toData()));
    }

    @Override
    public PolicyResponse cancelPolicy(CancelPolicyRequest request) {
        log.info("Cancelling policy: {}", request.getPolicyId());
        return PolicyResponse.fromPolicy(policyPort.cancelPolicy(request.toData()));
    }

    @Override
    public CancellationRequestResponse requestCancellation(RequestCancellationRequest request) {
        log.info("Requesting cancellation for policy: {}", request.getPolicyId());
        return CancellationRequestResponse.fromCancellationRequest(policyPort.requestCancellation(request.toData()));
    }

    @Override
    public PolicyResponse approveCancellationRequest(ApproveCancellationRequest request) {
        log.info("Approving cancellation request: {}", request.getCancellationRequestId());
        return PolicyResponse.fromPolicy(policyPort.approveCancellationRequest(request.toData()));
    }

    @Override
    public CancellationRequestResponse rejectCancellationRequest(RejectCancellationRequest request) {
        log.info("Rejecting cancellation request: {}", request.getCancellationRequestId());
        return CancellationRequestResponse.fromCancellationRequest(policyPort.rejectCancellationRequest(request.toData()));
    }

    @Override
    public int cancelPoliciesForNonPayment(Integer gracePeriodDays) {
        int effective = gracePeriodDays != null ? gracePeriodDays : policyDefaultsPort.getDefaultGracePeriodDays();
        log.info("Cancelling policies for non-payment (grace period {} days)", effective);
        return policyPort.cancelPoliciesForNonPayment(effective);
    }

    @Override
    public RenewalResultResponse renewPolicy(RenewPolicyRequest request) {
        log.info("Renewing policy: {}", request.getPolicyId());
        return RenewalResultResponse.fromRenewalResult(policyPort.renewPolicy(request.toData()));
    }

    @Override
    public PolicyResponse acceptRenewalQuote(AcceptRenewalRequest request) {
        log.info("Accepting renewal quote: {}", request.getRenewalQuoteId());
        return PolicyResponse.fromPolicy(policyPort.acceptRenewalQuote(request.toData()));
    }

    @Override
    public PolicyResponse suspendPolicy(SuspendPolicyRequest request) {
        log.info("Suspending policy: {}", request.getPolicyId());
        return PolicyResponse.fromPolicy(policyPort.suspendPolicy(request.toData()));
    }

    @Override
    public PolicyResponse reinstatePolicy(ReinstatePolicyRequest request) {
        log.info("Reinstating policy: {}", request.getPolicyId());
        return PolicyResponse.fromPolicy(policyPort.reinstatePolicy(request.toData()));
    }

    @Override
    public PolicyResponse getPolicyById(String policyId) {
        log.info("Getting policy by id: {}", policyId);
        return PolicyResponse.fromPolicy(policyPort.findPolicyById(policyId));
    }

    @Override
    public PolicyDetailResponse getPolicyDetails(String policyId) {
        log.info("Getting full policy details for: {}", policyId);
        return PolicyDetailResponse.fromPolicy(policyPort.findPolicyDetailsById(policyId));
    }

    @Override
    public PolicyResponse getPolicyByNumber(String policyNumber) {
        log.info("Getting policy by number: {}", policyNumber);
        return PolicyResponse.fromPolicy(policyPort.findByPolicyNumber(policyNumber));
    }

    @Override
    public List<PolicyVersionResponse> getPolicyVersions(String policyId) {
        log.info("Getting policy versions for: {}", policyId);
        return policyPort.getPolicyVersions(policyId).stream()
                .map(PolicyVersionResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public PolicyAuditLogPageResponse getPolicyAuditLog(String policyId, int page, int size) {
        log.info("Getting policy audit log for: {}", policyId);
        var pageParams = PageParams.builder()
                .page(page + 1)
                .size(size)
                .sortField("changedAt")
                .direction(Sort.Direction.DESC)
                .build();
        Page<PolicyAuditLog> auditPage = policyPort.getPolicyAuditLog(policyId, pageParams);
        Page<PolicyAuditLogResponse> responsePage = auditPage.map(PolicyAuditLogResponse::from);
        return PolicyAuditLogPageResponse.from(responsePage);
    }

    @Override
    public ComplianceViolationPageResponse getPolicyComplianceViolations(String policyId, int page, int size) {
        log.info("Getting compliance violations for policy: {}", policyId);
        var pageParams = PageParams.builder()
                .page(page + 1)
                .size(size)
                .sortField("detectedAt")
                .direction(Sort.Direction.DESC)
                .build();
        Page<ComplianceViolation> violationsPage = policyPort.getComplianceViolations(policyId, pageParams);
        Page<ComplianceViolationResponse> responsePage = violationsPage.map(ComplianceViolationResponse::from);
        return ComplianceViolationPageResponse.from(responsePage);
    }

    @Override
    public PolicyAuditTrailPageResponse getPolicyAuditTrail(String policyId, int page, int size, String sortField, Sort.Direction sortDirection) {
        log.info("Getting policy audit trail (status history) for: {}", policyId);
        var pageParams = PageParams.builder()
                .page(page)
                .size(size)
                .sortField(sortField)
                .direction(sortDirection)
                .build();
        Page<PolicyStatusHistory> auditPage = policyPort.getPolicyAuditTrail(policyId, pageParams);
        Page<PolicyStatusHistoryResponse> responsePage = auditPage.map(PolicyStatusHistoryResponse::from);
        return PolicyAuditTrailPageResponse.from(responsePage);
    }

    @Override
    public PolicyPageResponse listPolicies(PolicySearchRequest request, int page, int size, String sortField, Sort.Direction sortDirection) {
        log.info("Listing policies with criteria: {}", request);
        var criteria = request.toCriteria();
        var pageParams = PageParams.builder()
                .page(page)
                .size(size)
                .sortField(sortField)
                .direction(sortDirection)
                .build();
        var pageResult = policyPort.listPolicies(criteria, pageParams);
        return new PolicyPageResponse(pageResult.map(PolicyResponse::fromPolicy));
    }

    @Override
    public PolicyPageResponse listPoliciesByCustomer(String customerId, int page, int size, String sortField, Sort.Direction sortDirection) {
        log.info("Listing policies for customer: {}", customerId);
        var pageParams = PageParams.builder()
                .page(page)
                .size(size)
                .sortField(sortField)
                .direction(sortDirection)
                .build();
        var pageResult = policyPort.listPoliciesByCustomer(customerId, pageParams);
        return new PolicyPageResponse(pageResult.map(PolicyResponse::fromPolicy));
    }

    @Override
    public PolicySummaryResponse getCustomerPolicySummary(String customerId) {
        log.info("Getting policy summary for customer: {}", customerId);
        return PolicySummaryResponse.fromPolicySummary(policyPort.getCustomerPolicySummary(customerId));
    }

    @Override
    public List<PolicyResponse> getRenewalReminders(Integer daysAhead) {
        int effectiveDays = daysAhead != null ? daysAhead : policyDefaultsPort.getRenewalReminderDaysAhead();
        log.info("Getting renewal reminders for {} days ahead", effectiveDays);
        return policyPort.getRenewalReminders(effectiveDays).stream()
                .map(PolicyResponse::fromPolicy)
                .collect(Collectors.toList());
    }

    @Override
    public int generateRenewalQuotes(Integer daysAhead) {
        int effectiveDays = daysAhead != null ? daysAhead : policyDefaultsPort.getRenewalReminderDaysAhead();
        log.info("Generating renewal quotes for policies expiring in {} days", effectiveDays);
        return policyPort.generateRenewalQuotesForExpiringPolicies(effectiveDays);
    }

    @Override
    public boolean canReinstate(String policyId) {
        log.info("Checking if policy can be reinstated: {}", policyId);
        return policyPort.canReinstate(policyId);
    }

    @Override
    public BillingScheduleResponse getNextPaymentDue(String policyId) {
        log.info("Getting next payment due for policy: {}", policyId);
        return policyPort.getNextPaymentDue(policyId)
                .map(BillingScheduleResponse::fromBillingSchedule)
                .orElse(null);
    }

    @Override
    public ResponseEntity<?> exportPolicies(PolicySearchRequest request, String format, int limit) {
        log.info("Exporting policies as {} (limit {})", format, limit);
        int size = Math.min(Math.max(1, limit), 50000);
        var pageParams = PageParams.builder()
                .page(1)
                .size(size)
                .sortField("createdAt")
                .direction(Sort.Direction.DESC)
                .build();
        var pageResult = policyPort.listPolicies(request.toCriteria(), pageParams);
        List<PolicyResponse> policies = pageResult.getContent().stream()
                .map(PolicyResponse::fromPolicy)
                .collect(Collectors.toList());

        if ("csv".equalsIgnoreCase(format)) {
            String csv = toCsv(policies);
            byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"policies.csv\"")
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .contentLength(bytes.length)
                    .body(bytes);
        }
        if ("excel".equalsIgnoreCase(format) || "xlsx".equalsIgnoreCase(format)) {
            byte[] bytes = toExcel(policies);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"policies.xlsx\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(bytes.length)
                    .body(bytes);
        }
        if ("pdf".equalsIgnoreCase(format)) {
            byte[] bytes = toPdf(policies);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"policies.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(bytes.length)
                    .body(bytes);
        }

        return ResponseEntity.ok(new PolicyPageResponse(
                policies,
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.getNumber() + 1,
                pageResult.isFirst(),
                pageResult.isLast(),
                pageResult.hasNext(),
                pageResult.hasPrevious()
        ));
    }

    private String toCsv(List<PolicyResponse> policies) {
        var sb = new StringBuilder();
        sb.append("id,policyNumber,productId,customerId,agentId,branchId,status,type,premiumTotal,effectiveDate,expiryDate,createdAt\n");
        for (var p : policies) {
            sb.append(escapeCsv(p.getId())).append(",");
            sb.append(escapeCsv(p.getPolicyNumber())).append(",");
            sb.append(escapeCsv(p.getProductId())).append(",");
            sb.append(escapeCsv(p.getCustomerId())).append(",");
            sb.append(escapeCsv(p.getAgentId())).append(",");
            sb.append(escapeCsv(p.getBranchId())).append(",");
            sb.append(escapeCsv(p.getStatus() != null ? p.getStatus().name() : "")).append(",");
            sb.append(escapeCsv(p.getType() != null ? p.getType().name() : "")).append(",");
            sb.append(p.getPremiumTotal() != null ? p.getPremiumTotal() : "").append(",");
            sb.append(escapeCsv(p.getEffectiveDate() != null ? p.getEffectiveDate().toString() : "")).append(",");
            sb.append(escapeCsv(p.getExpiryDate() != null ? p.getExpiryDate().toString() : "")).append(",");
            sb.append(escapeCsv(p.getCreatedAt() != null ? p.getCreatedAt().toString() : ""));
            sb.append("\n");
        }
        return sb.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private byte[] toExcel(List<PolicyResponse> policies) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Policies");
            String[] headers = {"id", "policyNumber", "productId", "customerId", "agentId", "branchId", "status", "type", "premiumTotal", "effectiveDate", "expiryDate", "createdAt"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            int r = 1;
            for (var p : policies) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(p.getPolicyNumber());
                row.createCell(2).setCellValue(p.getProductId());
                row.createCell(3).setCellValue(p.getCustomerId());
                row.createCell(4).setCellValue(p.getAgentId() != null ? p.getAgentId() : "");
                row.createCell(5).setCellValue(p.getBranchId() != null ? p.getBranchId() : "");
                row.createCell(6).setCellValue(p.getStatus() != null ? p.getStatus().name() : "");
                row.createCell(7).setCellValue(p.getType() != null ? p.getType().name() : "");
                row.createCell(8).setCellValue(p.getPremiumTotal() != null ? p.getPremiumTotal().doubleValue() : 0);
                row.createCell(9).setCellValue(p.getEffectiveDate() != null ? p.getEffectiveDate().toString() : "");
                row.createCell(10).setCellValue(p.getExpiryDate() != null ? p.getExpiryDate().toString() : "");
                row.createCell(11).setCellValue(p.getCreatedAt() != null ? p.getCreatedAt().toString() : "");
            }
            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new com.zote.common.utils.exceptions.FunctionalError("Failed to generate Excel: " + e.getMessage());
        }
    }

    private byte[] toPdf(List<PolicyResponse> policies) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(doc, out);
            doc.open();
            doc.add(new Paragraph("Policy Export Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            doc.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            String[] headers = {"Policy #", "Product", "Customer", "Status", "Premium", "Effective", "Expiry", "Created"};
            for (String h : headers) {
                PdfPCell c = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
                table.addCell(c);
            }
            for (var p : policies) {
                table.addCell(p.getPolicyNumber() != null ? p.getPolicyNumber() : "");
                table.addCell(p.getProductId() != null ? p.getProductId() : "");
                table.addCell(p.getCustomerId() != null ? p.getCustomerId() : "");
                table.addCell(p.getStatus() != null ? p.getStatus().name() : "");
                table.addCell(p.getPremiumTotal() != null ? p.getPremiumTotal().toString() : "");
                table.addCell(p.getEffectiveDate() != null ? p.getEffectiveDate().toString() : "");
                table.addCell(p.getExpiryDate() != null ? p.getExpiryDate().toString() : "");
                table.addCell(p.getCreatedAt() != null ? p.getCreatedAt().toString() : "");
            }
            doc.add(table);
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new com.zote.common.utils.exceptions.FunctionalError("Failed to generate PDF: " + e.getMessage());
        }
    }

    @Override
    public PortfolioSnapshotResponse getPortfolioSnapshot() {
        return PortfolioSnapshotResponse.from(policyPort.getPortfolioSnapshot());
    }

    @Override
    public PolicyKpisResponse getPolicyKpis() {
        return PolicyKpisResponse.from(policyPort.getPolicyKpis());
    }

    @Override
    public RenewalRatesResponse getRenewalRates(LocalDate from, LocalDate to, String periodType) {
        return RenewalRatesResponse.from(policyPort.getRenewalRates(from, to, periodType));
    }

    @Override
    public CancellationRatesResponse getCancellationRates(LocalDate from, LocalDate to, String periodType) {
        return CancellationRatesResponse.from(policyPort.getCancellationRates(from, to, periodType));
    }

    @Override
    public PremiumSummaryResponse getPremiumSummary(String dimension) {
        return PremiumSummaryResponse.from(policyPort.getPremiumSummary(dimension));
    }

    @Override
    public PolicyTrendsResponse getPolicyTrends(LocalDate from, LocalDate to, String periodType) {
        return PolicyTrendsResponse.from(policyPort.getPolicyTrends(from, to, periodType));
    }

    @Override
    public void publishAnalyticsSnapshot() {
        policyPort.publishAnalyticsSnapshot();
    }
}
