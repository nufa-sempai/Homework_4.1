package org.example.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.AvatarDto;
import org.example.model.Avatar;
import org.example.service.AvatarService;
import org.example.service.StudentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    private final StudentService studentService;

    private final AvatarService avatarService;

    public AvatarController(StudentService studentService, AvatarService avatarService) {
        this.studentService = studentService;
        this.avatarService = avatarService;
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<byte[]> downloadAvatar(@PathVariable("id") Long studentId) {
        Avatar avatar = studentService.findAvatarByStudentId(studentId);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        httpHeaders.setContentLength(avatar.getFileSize());

        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(avatar.getData());
    }

    @GetMapping("/{id}")
    public void downloadAvatar(@PathVariable("id") long studentId,
                               HttpServletResponse response) throws IOException {
        Avatar avatar = studentService.findAvatarByStudentId(studentId);
        Path path = Path.of(avatar.getFilePath());
        try (InputStream is = Files.newInputStream(path);
             ServletOutputStream os = response.getOutputStream();) {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(avatar.getMediaType());
            response.setContentLengthLong(avatar.getFileSize());
            is.transferTo(os);
        }
    }

    @GetMapping()
    public List<AvatarDto> getAllAvatars(@RequestParam int pageNumber,
                                         @RequestParam int pageSize) {
        return avatarService.findAll(pageNumber, pageSize);
    }

}