package com.zote.user.service.api.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageDto {
    private String imageUrl;
}
