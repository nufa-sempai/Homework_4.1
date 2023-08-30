package org.example.controller;

import org.example.dto.FacultyDtoOut;
import org.example.dto.StudentDtoIn;
import org.example.dto.StudentDtoOut;
import org.example.service.AvatarService;
import org.example.service.StudentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final AvatarService avatarService;

    public StudentController(StudentService studentService,
                             AvatarService avatarService) {
        this.studentService = studentService;
        this.avatarService = avatarService;
    }

    @PostMapping
    public StudentDtoOut create(@RequestBody StudentDtoIn studentDtoIn) {
        return studentService.create(studentDtoIn);
    }

    @GetMapping("/{id}")
    public StudentDtoOut get(@PathVariable long id) {
        return studentService.get(id);
    }

    @PutMapping("/{id}")
    public StudentDtoOut update(@PathVariable long id,
                                @RequestBody StudentDtoIn studentDtoIn) {
        return studentService.update(id, studentDtoIn);
    }

    @DeleteMapping("/{id}")
    public StudentDtoOut delete(@PathVariable long id) {
        return studentService.delete(id);
    }

    @GetMapping("/{age}/students")
    public Collection<StudentDtoOut> findAll(@PathVariable(required = false) int age) {
        return studentService.findAll(age);
    }

    @GetMapping("/filter")
    public Collection<StudentDtoOut> findStudentsByAgeBetween(@RequestParam int from,
                                                              @RequestParam int to) {
        return studentService.findStudentsByAgeBetween(from, to);
    }

    @GetMapping("/{id}/faculty")
    public FacultyDtoOut findStudentsFaculty(@PathVariable Long id) {
        return studentService.findStudentsFaculty(id);
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable("id") long studentId,
                                               @RequestParam MultipartFile avatarImage) throws IOException {
        avatarService.uploadAvatar(studentId, avatarImage);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/total-count")
    public ResponseEntity<Integer> getTotalCountStudents() {
        return ResponseEntity.ok(studentService.getTotalCountStudents());
    }

    @GetMapping("/avg-age")
    public ResponseEntity<Double> getAvgAgeStudents() {
        return ResponseEntity.ok(studentService.getAvgAgeStudents());
    }

    @GetMapping("/last-five")
    public Collection<StudentDtoOut> getLastFiveStudents() {
        return studentService.getLastFiveStudents();
    }
}