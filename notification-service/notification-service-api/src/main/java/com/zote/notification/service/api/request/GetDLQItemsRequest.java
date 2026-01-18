package com.zote.notification.service.api.request;

import com.zote.notification.service.domain.model.GetDLQQuery;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class GetDLQItemsRequest {
    private Boolean processed;
    private int page = 0;
    private int size = 20;

    public GetDLQQuery toGetDLQQuery() {
        var query = new GetDLQQuery();
        BeanUtils.copyProperties(this, query);
        return query;
    }
}

