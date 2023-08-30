package org.example.mapper;

import org.example.dto.AvatarDto;
import org.example.model.Avatar;
import org.example.repository.AvatarRepository;
import org.springframework.stereotype.Component;

@Component
public class AvatarMapper {
    private AvatarRepository avatarRepository;
    private StudentMapper studentMapper;

    public AvatarMapper(AvatarRepository avatarRepository, StudentMapper studentMapper) {
        this.avatarRepository = avatarRepository;
        this.studentMapper = studentMapper;
    }

    public AvatarDto toDto(Avatar avatar) {

        return AvatarDto.builder()
                .id(avatar.getId())
                .filePath(avatar.getFilePath())
                .fileSize(avatar.getFileSize())
                .mediaType(avatar.getMediaType())
                .studentId(avatarRepository.studentId(avatar.getId()))
                .build();
    }
}