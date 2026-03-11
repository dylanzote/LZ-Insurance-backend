package com.zote.policy.service.domain.ports.inbound;

import com.zote.policy.service.domain.models.Quote;
import com.zote.policy.service.domain.models.UnderwritingNote;
import com.zote.policy.service.domain.models.data.AcceptQuoteData;
import com.zote.policy.service.domain.models.data.CreateQuoteData;
import com.zote.policy.service.domain.models.data.AddUnderwritingNoteData;
import com.zote.policy.service.domain.models.data.UpdateQuoteData;
import com.zote.policy.service.domain.models.data.UnderwritingDecisionData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface QuotePort {

    Quote createQuote(CreateQuoteData data);

    Quote updateQuote(UpdateQuoteData data);

    Quote acceptQuote(AcceptQuoteData data);

    Quote recordUnderwritingDecision(UnderwritingDecisionData data);

    UnderwritingNote addUnderwritingNote(AddUnderwritingNoteData data);

    List<UnderwritingNote> getUnderwritingNotes(String quoteId);

    Quote findQuoteById(String quoteId);

    Quote findQuoteByQuoteNumber(String quoteNumber);

    Page<Quote> getQuotesByCustomer(String customerId, int page, int size, String sortField, Sort.Direction direction);
}
