package com.zote.policy.service.api.controller;

import com.zote.common.utils.models.Permissions;
import com.zote.policy.service.api.request.*;
import com.zote.policy.service.api.response.PolicyDocumentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Policy Document API")
@RestController
@RequestMapping("/policy/documents/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface PolicyDocumentApi {

    @Operation(summary = "Upload document (11.1)")
    @PostMapping("upload")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    PolicyDocumentResponse uploadDocument(@Valid @RequestBody UploadDocumentRequest request);

    @Operation(summary = "Verify document (11.2)")
    @PostMapping("verify")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PolicyDocumentResponse verifyDocument(@Valid @RequestBody VerifyDocumentRequest request);

    @Operation(summary = "Reject document (11.2)")
    @PostMapping("reject")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PolicyDocumentResponse rejectDocument(@Valid @RequestBody RejectDocumentRequest request);

    @Operation(summary = "Retrieve all documents for policy (11.4)")
    @GetMapping("policy/{policyId}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    List<PolicyDocumentResponse> getDocumentsByPolicyId(@PathVariable("policyId") String policyId);

    @Operation(summary = "Check if all required documents verified (11.5)")
    @GetMapping("policy/{policyId}/required-verified")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    boolean hasAllRequiredDocumentsVerified(@PathVariable("policyId") String policyId);

    @Operation(summary = "Delete document (11.7)")
    @DeleteMapping("{documentId}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    void deleteDocument(@PathVariable("documentId") String documentId);

    @Operation(summary = "Replace document (11.7)")
    @PutMapping("replace")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PolicyDocumentResponse replaceDocument(@Valid @RequestBody ReplaceDocumentRequest request);

    @Operation(summary = "Get document by id")
    @GetMapping("{documentId}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    PolicyDocumentResponse getDocumentById(@PathVariable("documentId") String documentId);
}
