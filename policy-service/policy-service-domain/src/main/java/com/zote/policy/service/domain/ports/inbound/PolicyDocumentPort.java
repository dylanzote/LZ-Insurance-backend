package com.zote.policy.service.domain.ports.inbound;

import com.zote.policy.service.domain.enums.PolicyDocumentType;
import com.zote.policy.service.domain.models.PolicyDocument;
import com.zote.policy.service.domain.models.data.*;

import java.util.List;

/**
 * Port for document management (11.1–11.8).
 */
public interface PolicyDocumentPort {

    /** Upload document (11.1). */
    PolicyDocument uploadDocument(UploadDocumentData data);

    /** Verify document (11.2). */
    PolicyDocument verifyDocument(VerifyDocumentData data);

    /** Reject document (11.2). */
    PolicyDocument rejectDocument(RejectDocumentData data);

    /** Retrieve all documents for a policy (11.4). */
    List<PolicyDocument> getDocumentsByPolicyId(String policyId);

    /** Check if all required documents are uploaded and verified (11.5). */
    boolean hasAllRequiredDocumentsVerified(String policyId);

    /** Delete document (11.7). */
    void deleteDocument(String documentId);

    /** Replace document (11.7). */
    PolicyDocument replaceDocument(ReplaceDocumentData data);

    /** Get single document by id. */
    PolicyDocument getDocumentById(String documentId);
}
