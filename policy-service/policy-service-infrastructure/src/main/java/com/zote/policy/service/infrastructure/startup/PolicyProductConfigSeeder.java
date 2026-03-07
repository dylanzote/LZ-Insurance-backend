package com.zote.policy.service.infrastructure.startup;

import com.zote.policy.service.domain.enums.PolicyTermUnit;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.enums.RequiredDocumentType;
import com.zote.policy.service.domain.models.BillingPlan;
import com.zote.policy.service.domain.models.PolicyProductConfig;
import com.zote.policy.service.domain.models.PolicyRequiredDocument;
import com.zote.policy.service.domain.ports.outbound.ProductConfigRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PolicyProductConfigSeeder implements CommandLineRunner {

    private final ProductConfigRepositoryPort productConfigRepositoryPort;

    @Override
    public void run(String... args) {
        seedAutoAnnual();
        seedHomeAnnual();
        seedTravelTrip();
        seedHealthAnnual();
        seedLifeTerm20();
        seedMotorcycleAnnual();
    }

    private void seedAutoAnnual() {
        String productId = "AUTO_STD_ANNUAL";
        if (productConfigRepositoryPort.existsByProductId(productId)) return;

        var config = productConfigRepositoryPort.saveProductConfig(
                PolicyProductConfig.builder()
                        .id(UUID.randomUUID().toString())
                        .productId(productId)
                        .policyType(PolicyType.AUTO)
                        .termUnit(PolicyTermUnit.MONTHS)
                        .termLength(12)
                        .defaultBillingPlan(BillingPlan.MONTHLY)
                        .allowFullPayment(true)
                        .allowInstallments(true)
                        .installmentsCount(12)
                        .requireDocuments(true)
                        .requiresUnderwriting(false)
                        .build()
        );

        saveDocs(config.getId(), List.of(
                RequiredDocumentType.ID_CARD,
                RequiredDocumentType.DRIVER_LICENSE,
                RequiredDocumentType.VEHICLE_REGISTRATION,
                RequiredDocumentType.PROOF_OF_ADDRESS
        ));
        log.info("Seeded AUTO product config");
    }

    private void seedHomeAnnual() {
        String productId = "HOME_STD_ANNUAL";
        if (productConfigRepositoryPort.existsByProductId(productId)) return;

        var config = productConfigRepositoryPort.saveProductConfig(
                PolicyProductConfig.builder()
                        .id(UUID.randomUUID().toString())
                        .productId(productId)
                        .policyType(PolicyType.HOME)
                        .termUnit(PolicyTermUnit.MONTHS)
                        .termLength(12)
                        .defaultBillingPlan(BillingPlan.MONTHLY)
                        .allowFullPayment(true)
                        .allowInstallments(true)
                        .installmentsCount(12)
                        .requireDocuments(true)
                        .requiresUnderwriting(false)
                        .build()
        );

        saveDocs(config.getId(), List.of(
                RequiredDocumentType.ID_CARD,
                RequiredDocumentType.PROOF_OF_ADDRESS,
                RequiredDocumentType.PROPERTY_OWNERSHIP_PROOF
        ));
        log.info("Seeded HOME product config");
    }

    private void seedTravelTrip() {
        String productId = "TRAVEL_STD_TRIP";
        if (productConfigRepositoryPort.existsByProductId(productId)) return;

        var config = productConfigRepositoryPort.saveProductConfig(
                PolicyProductConfig.builder()
                        .id(UUID.randomUUID().toString())
                        .productId(productId)
                        .policyType(PolicyType.TRAVEL)
                        .termUnit(PolicyTermUnit.TRIP_DATES)
                        .termLength(null)
                        .defaultBillingPlan(BillingPlan.FULL)
                        .allowFullPayment(true)
                        .allowInstallments(false)
                        .installmentsCount(null)
                        .requireDocuments(true)
                        .requiresUnderwriting(false)
                        .build()
        );

        saveDocs(config.getId(), List.of(
                RequiredDocumentType.ID_CARD,
                RequiredDocumentType.PASSPORT,
                RequiredDocumentType.TRAVEL_ITINERARY
        ));
        log.info("Seeded TRAVEL product config");
    }

    private void seedHealthAnnual() {
        String productId = "HEALTH_STD_ANNUAL";
        if (productConfigRepositoryPort.existsByProductId(productId)) return;

        var config = productConfigRepositoryPort.saveProductConfig(
                PolicyProductConfig.builder()
                        .id(UUID.randomUUID().toString())
                        .productId(productId)
                        .policyType(PolicyType.HEALTH)
                        .termUnit(PolicyTermUnit.MONTHS)
                        .termLength(12)
                        .defaultBillingPlan(BillingPlan.MONTHLY)
                        .allowFullPayment(true)
                        .allowInstallments(true)
                        .installmentsCount(12)
                        .requireDocuments(true)
                        .requiresUnderwriting(true)
                        .build()
        );

        saveDocs(config.getId(), List.of(
                RequiredDocumentType.ID_CARD,
                RequiredDocumentType.MEDICAL_REPORT
        ));
        log.info("Seeded HEALTH product config");
    }

    private void seedLifeTerm20() {
        String productId = "LIFE_TERM_20";
        if (productConfigRepositoryPort.existsByProductId(productId)) return;

        var config = productConfigRepositoryPort.saveProductConfig(
                PolicyProductConfig.builder()
                        .id(UUID.randomUUID().toString())
                        .productId(productId)
                        .policyType(PolicyType.LIFE)
                        .termUnit(PolicyTermUnit.YEARS)
                        .termLength(20)
                        .defaultBillingPlan(BillingPlan.MONTHLY)
                        .allowFullPayment(true)
                        .allowInstallments(true)
                        .installmentsCount(12)
                        .requireDocuments(true)
                        .requiresUnderwriting(true)
                        .build()
        );

        saveDocs(config.getId(), List.of(
                RequiredDocumentType.ID_CARD,
                RequiredDocumentType.MEDICAL_REPORT,
                RequiredDocumentType.BENEFICIARY_ID
        ));
        log.info("Seeded LIFE product config");
    }

    private void seedMotorcycleAnnual() {
        String productId = "MOTO_STD_ANNUAL";
        if (productConfigRepositoryPort.existsByProductId(productId)) return;

        var config = productConfigRepositoryPort.saveProductConfig(
                PolicyProductConfig.builder()
                        .id(UUID.randomUUID().toString())
                        .productId(productId)
                        .policyType(PolicyType.MOTORCYCLE)
                        .termUnit(PolicyTermUnit.MONTHS)
                        .termLength(12)
                        .defaultBillingPlan(BillingPlan.MONTHLY)
                        .allowFullPayment(true)
                        .allowInstallments(true)
                        .installmentsCount(12)
                        .requireDocuments(true)
                        .requiresUnderwriting(false)
                        .build()
        );

        saveDocs(config.getId(), List.of(
                RequiredDocumentType.ID_CARD,
                RequiredDocumentType.DRIVER_LICENSE,
                RequiredDocumentType.VEHICLE_REGISTRATION
        ));
        log.info("Seeded MOTORCYCLE product config");
    }

    private void saveDocs(String productConfigId, List<RequiredDocumentType> docs) {
        docs.forEach(doc -> productConfigRepositoryPort.saveRequiredDocument(
                PolicyRequiredDocument.builder()
                        .id(UUID.randomUUID().toString())
                        .productConfigId(productConfigId)
                        .documentType(doc)
                        .mandatory(true)
                        .build()
        ));
    }
}
