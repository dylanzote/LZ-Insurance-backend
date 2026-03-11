package com.zote.policy.service.api.controller;

import com.zote.common.utils.models.Permissions;
import com.zote.policy.service.api.request.AcceptQuoteRequest;
import com.zote.policy.service.api.request.CreateQuoteRequest;
import com.zote.policy.service.api.request.UpdateQuoteRequest;
import com.zote.policy.service.api.request.AddUnderwritingNoteRequest;
import com.zote.policy.service.api.request.UnderwritingDecisionRequest;
import com.zote.policy.service.api.response.QuotePageResponse;
import com.zote.policy.service.api.response.QuoteResponse;
import com.zote.policy.service.api.response.UnderwritingNoteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Quote API")
@RestController
@RequestMapping("/quote/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface QuoteApi {

    @Operation(summary = "Create quote")
    @PostMapping("create")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    QuoteResponse createQuote(@Valid @RequestBody CreateQuoteRequest request);

    @Operation(summary = "Update quote")
    @PutMapping("update")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    QuoteResponse updateQuote(@Valid @RequestBody UpdateQuoteRequest request);

    @Operation(summary = "Accept quote")
    @PostMapping("accept")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    QuoteResponse acceptQuote(@Valid @RequestBody AcceptQuoteRequest request);

    @Operation(summary = "Record underwriting decision")
    @PostMapping("underwriting-decision")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    QuoteResponse recordUnderwritingDecision(@Valid @RequestBody UnderwritingDecisionRequest request);

    @Operation(summary = "Add underwriting note")
    @PostMapping("underwriting-note")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    UnderwritingNoteResponse addUnderwritingNote(@Valid @RequestBody AddUnderwritingNoteRequest request);

    @Operation(summary = "Get underwriting notes by quote id")
    @GetMapping("underwriting-notes/{quoteId}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    List<UnderwritingNoteResponse> getUnderwritingNotes(@PathVariable("quoteId") String quoteId);

    @Operation(summary = "Get quote by id")
    @GetMapping("get/{id}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    QuoteResponse getQuoteById(@PathVariable("id") String quoteId);

    @Operation(summary = "Get quote by quote number")
    @GetMapping("get-by-number/{quoteNumber}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    QuoteResponse getQuoteByNumber(@PathVariable("quoteNumber") String quoteNumber);

    @Operation(summary = "Get quotes by customer")
    @GetMapping("customer/{customerId}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    QuotePageResponse getQuotesByCustomer(
            @PathVariable("customerId") String customerId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortField", defaultValue = "createdAt") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") Sort.Direction sortDirection);
}
