package com.zote.resourcebundle.adapter.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class TemplatePageResponse {
    private List<TemplateResponse> data;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    @JsonProperty("isFirst")
    private boolean isFirst;
    @JsonProperty("isLast")
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;



    public TemplatePageResponse(Page<TemplateResponse> templatePage) {
        this(templatePage.getContent(), templatePage.getTotalElements(), templatePage.getTotalPages(), templatePage.getNumber() + 1, templatePage.isFirst(), templatePage.isLast(), templatePage.hasNext(), templatePage.hasPrevious());
    }

}
