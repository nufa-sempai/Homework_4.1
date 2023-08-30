package org.example.mapper;

import org.example.dto.StudentDtoIn;
import org.example.dto.StudentDtoOut;
import org.example.exception.FacultyNotFoundException;
import org.example.model.Student;
import org.example.repository.FacultyRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StudentMapper {
    private final FacultyMapper facultyMapper;
    private final FacultyRepository facultyRepository;

    public StudentMapper(FacultyMapper facultyMapper, FacultyRepository facultyRepository) {
        this.facultyMapper = facultyMapper;
        this.facultyRepository = facultyRepository;
    }

    public StudentDtoOut toDto(Student student) {
        StudentDtoOut studentDtoOut = new StudentDtoOut();
        studentDtoOut.setId(student.getId());
        studentDtoOut.setName(student.getName());
        studentDtoOut.setAge(student.getAge());
        Optional.ofNullable(student.getFaculty())
                .ifPresent(faculty -> studentDtoOut.setFaculty(facultyMapper.toDto(faculty)));
        return studentDtoOut;
    }

    public Student toEntity(StudentDtoIn studentDtoIn) {
        Student student = new Student();
        student.setName(studentDtoIn.getName());
        student.setAge(studentDtoIn.getAge());
        student.setFaculty(
                facultyRepository.findById(
                        studentDtoIn.getFacultyId()
                ).orElseThrow(() -> new FacultyNotFoundException(studentDtoIn.getFacultyId()))
        );
        return student;
    }
}