package org.example.controller;

import org.example.dto.FacultyDtoIn;
import org.example.dto.FacultyDtoOut;
import org.example.dto.StudentDtoOut;
import org.example.service.FacultyService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService service;

    public FacultyController(FacultyService service) {
        this.service = service;
    }

    @PostMapping
    public FacultyDtoOut create(@RequestBody FacultyDtoIn facultyDtoIn) {
        return service.create(facultyDtoIn);
    }

    @GetMapping("{id}")
    public FacultyDtoOut get(@PathVariable long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public FacultyDtoOut update(@PathVariable long id, @RequestBody FacultyDtoIn facultyDtoIn) {
        return service.update(id, facultyDtoIn);
    }

    @DeleteMapping("{id}")
    public FacultyDtoOut delete(@PathVariable long id) {
        return service.delete(id);
    }

    @GetMapping("/filter")
    public Collection<FacultyDtoOut> filter(@RequestParam String colorOrName) {
        return service.findByColorOrName(colorOrName);
    }

    @GetMapping("/{facultyName}/students")
    public Collection<StudentDtoOut> findAllStudentsOnFaculty(@PathVariable String facultyName) {
        return service.findStudentsByFaculty(facultyName);
    }
}