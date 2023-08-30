package org.example.service;

import jakarta.annotation.Nullable;
import org.example.dto.FacultyDtoOut;
import org.example.dto.StudentDtoIn;
import org.example.dto.StudentDtoOut;
import org.example.exception.AvatarNotFoundException;
import org.example.exception.FacultyNotFoundException;
import org.example.exception.StudentNotFoundException;
import org.example.mapper.FacultyMapper;
import org.example.mapper.StudentMapper;
import org.example.model.Avatar;
import org.example.model.Student;
import org.example.repository.AvatarRepository;
import org.example.repository.FacultyRepository;
import org.example.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AvatarRepository avatarRepository;
    private final StudentMapper studentMapper;
    private final FacultyMapper facultyMapper;

    public StudentService(StudentRepository studentRepository,
                          StudentMapper studentMapper,
                          FacultyRepository facultyRepository,
                          FacultyMapper facultyMapper,
                          AvatarRepository avatarRepository
    ) {
        this.studentMapper = studentMapper;
        this.facultyMapper = facultyMapper;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.avatarRepository = avatarRepository;
    }

    public StudentDtoOut create(StudentDtoIn studentDtoIn) {
        return studentMapper.toDto(
                studentRepository.save(
                        studentMapper.toEntity(studentDtoIn)
                )
        );
    }
    public StudentDtoOut get(long id) {
        return studentRepository.findById(id)
                .map(studentMapper::toDto)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }
    public StudentDtoOut update(long studentId, StudentDtoIn studentDtoIn) {
        Student updatedStudent = studentRepository.findById(studentId)
                .map(student -> {
                    student.setName(studentDtoIn.getName());
                    student.setAge(studentDtoIn.getAge());
                    long facultyId = studentDtoIn.getFacultyId();
                    student.setFaculty(
                            facultyRepository.findById(facultyId)
                                    .orElseThrow(() -> new FacultyNotFoundException(facultyId))
                    );
                    return student;
                })
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        studentRepository.save(updatedStudent);
        return studentMapper.toDto(updatedStudent);
    }
    public StudentDtoOut delete(long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        studentRepository.deleteById(id);
        return studentMapper.toDto(student);
    }
    public Collection<StudentDtoOut> findStudentsByAgeBetween(int from, int to) {
        return studentRepository.findStudentsByAgeBetween(from, to).stream()
                .map(studentMapper::toDto)
                .toList();
    }
    public Collection<StudentDtoOut> findAll(@Nullable Integer age) {
        return Optional.ofNullable(age)
                .map(studentRepository::findStudentsByAge)
                .orElseGet(studentRepository::findAll).stream()
                .map(studentMapper::toDto)
                .toList();
    }
    public FacultyDtoOut findStudentsFaculty(Long studentId) {
        return studentRepository.findById(studentId)
                .map(Student::getFaculty)
                .map(facultyMapper::toDto)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
    }

    public Avatar findAvatarByStudentId(long studentId) {
        return avatarRepository.findByStudent_id(studentId)
                .orElseThrow(() -> new AvatarNotFoundException(studentId));
    }
    public Integer getTotalCountStudents() {
        return studentRepository.getTotalCountStudents();
    }

    public Double getAvgAgeStudents() {
        return studentRepository.getAvgAgeStudents();
    }

    public Collection<StudentDtoOut> getLastFiveStudents() {
        return studentRepository.getLastFiveStudents().stream()
                .map(studentMapper::toDto)
                .toList();
    }
}