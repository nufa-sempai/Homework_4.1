package org.example.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvatarDto {
    private long id;

    private String filePath;

    private long fileSize;

    private String mediaType;

    private long studentId;
}