package com.zote.policy.service.domain.models.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageParams {
    private int page;
    private int size;
    private String sortField;
    private Sort.Direction direction;

    public Pageable toPageable() {
        int pageNo = page < 0 ? 0 : page - 1;
        return PageRequest.of(pageNo, size, direction, sortField);
    }
}
