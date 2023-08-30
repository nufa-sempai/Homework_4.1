package service;

import org.example.dto.FacultyDtoOut;
import org.example.dto.StudentDtoIn;
import org.example.dto.StudentDtoOut;
import org.example.exception.FacultyNotFoundException;
import org.example.exception.StudentNotFoundException;
import org.example.mapper.FacultyMapper;
import org.example.mapper.StudentMapper;
import org.example.model.Faculty;
import org.example.model.Student;
import org.example.repository.AvatarRepository;
import org.example.repository.FacultyRepository;
import org.example.repository.StudentRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("studentService")
class StudentServiceTest {
    private static final StudentDtoIn STUDENT_DTO_IN = new StudentDtoIn();
    private static final StudentDtoOut STUDENT_DTO_OUT = new StudentDtoOut();
    private static final Student STUDENT = new Student();
    private static final long STUDENT_ID = 1L;
    private static final Faculty FACULTY = new Faculty();
    private StudentService studentService;
    private final FacultyMapper facultyMapper = new FacultyMapper();
    private final FacultyRepository facultyRepository = mock(FacultyRepository.class);
    private final StudentRepository studentRepository = mock(StudentRepository.class);
    private final AvatarRepository avatarRepository = mock(AvatarRepository.class);

    @BeforeEach
    void prepare() {
        studentService = new StudentService(
                studentRepository,
                new StudentMapper(facultyMapper, facultyRepository),
                facultyRepository,
                facultyMapper,
                avatarRepository
        );

        FacultyDtoOut facultyDtoOut = new FacultyDtoOut();
        facultyDtoOut.setId(1L);
        facultyDtoOut.setName("name");
        facultyDtoOut.setColor("color");

        FACULTY.setId(1L);
        FACULTY.setName("name");
        FACULTY.setColor("color");

        STUDENT_DTO_IN.setName("name");
        STUDENT_DTO_IN.setAge(11);
        STUDENT_DTO_IN.setFacultyId(1);

        STUDENT.setId(1L);
        STUDENT.setName("name");
        STUDENT.setAge(11);
        STUDENT.setFaculty(FACULTY);

        STUDENT_DTO_OUT.setId(1L);
        STUDENT_DTO_OUT.setName("name");
        STUDENT_DTO_OUT.setAge(11);
        STUDENT_DTO_OUT.setFaculty(facultyDtoOut);
    }

    @Nested
    @Tag("studentServiceCrud")
    class CrudTest {
        @Test
        @DisplayName("createPositiveTest")
        void shouldConvertDtoInToEntityThenConvertToDtoOutAndReturnDtoOut() {
            when(studentRepository.save(any())).thenReturn(STUDENT);
            when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(FACULTY));

            StudentDtoOut actual = studentService.create(STUDENT_DTO_IN);

            assertAll(
                    () -> assertThat(actual).isEqualTo(STUDENT_DTO_OUT),
                    () -> verify(studentRepository, new Times(1)).save(any()),
                    () -> verify(facultyRepository, new Times(1)).findById(anyLong())
            );
        }

        @Test
        @DisplayName("createNegativeTest")
        void shouldThrowFacultyNotFoundExceptionWhenInStudentDtoInIncorrectFacultyId() {
            when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());
            assertAll(
                    () -> assertThatExceptionOfType(FacultyNotFoundException.class)
                            .isThrownBy(() -> studentService.create(STUDENT_DTO_IN)),
                    () -> verify(facultyRepository, new Times(1)).findById(anyLong())
            );
        }

        @Test
        @DisplayName("getPositiveTest")
        void shouldReturnStudentDtoOutById() {
            when(studentRepository.findById(anyLong())).thenReturn(Optional.of(STUDENT));

            StudentDtoOut actual = studentService.get(STUDENT_ID);

            assertAll(
                    () -> assertThat(actual).isEqualTo(STUDENT_DTO_OUT),
                    () -> verify(studentRepository, new Times(1)).findById(anyLong())
            );
        }

        @Test
        @DisplayName("getNegativeTest")
        void shouldThrowStudentNotFoundExceptionWhenStudentIdNotFound() {
            when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());
            assertAll(
                    () -> assertThatExceptionOfType(StudentNotFoundException.class)
                            .isThrownBy(() -> studentService.get(anyLong())),
                    () -> verify(studentRepository, new Times(1)).findById(anyLong())
            );
        }

        @Test
        @DisplayName("updatePositiveTest")
        void shouldUpdateStudentAndReturnStudentDtoOut() {
            Student someStudent = new Student();
            someStudent.setId(1L);
            someStudent.setName("otherName");
            someStudent.setAge(12);

            when(studentRepository.findById(anyLong())).thenReturn(Optional.of(someStudent));
            when(studentRepository.save(any())).thenReturn(STUDENT);
            when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(FACULTY));

            StudentDtoOut actual = studentService.update(STUDENT_ID, STUDENT_DTO_IN);

            assertAll(
                    () -> assertThat(actual).isEqualTo(STUDENT_DTO_OUT),
                    () -> verify(studentRepository, new Times(1)).findById(anyLong()),
                    () -> verify(studentRepository, new Times(1)).save(any()),
                    () -> verify(facultyRepository, new Times(1)).findById(anyLong())
            );
        }

        @Test
        @DisplayName("updateNegativeTest [1]")
        void shouldThrowStudentNotFoundExceptionWhenNewStudentIdHasNoFoundInCurrentIdList() {
            when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertAll(
                    () -> assertThatExceptionOfType(StudentNotFoundException.class)
                            .isThrownBy(() -> studentService.update(10, STUDENT_DTO_IN)),
                    () -> verify(studentRepository, new Times(1)).findById(anyLong()),
                    () -> verify(studentRepository, new Times(0)).save(any())
            );
        }

        @Test
        @DisplayName("updateNegativeTest [2]")
        void shouldThrowFacultyNotFoundExceptionWhenNewStudentHasIncorrectFacultyId() {
            when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());
            when(studentRepository.findById(anyLong())).thenReturn(Optional.of(STUDENT));

            assertAll(
                    () -> assertThatExceptionOfType(FacultyNotFoundException.class)
                            .isThrownBy(() -> studentService.update(STUDENT_ID, STUDENT_DTO_IN)),
                    () -> verify(facultyRepository, new Times(1)).findById(anyLong()),
                    () -> verify(studentRepository, new Times(1)).findById(anyLong())
            );
        }

        @Test
        @DisplayName("deletePositiveTest")
        void shouldDeleteStudentAndReturnStudentDtoOut() {
            when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(STUDENT));

            assertAll(
                    () -> assertThat(studentService.delete(STUDENT_ID)).isEqualTo(STUDENT_DTO_OUT),
                    () -> verify(studentRepository, new Times(1)).findById(anyLong()),
                    () -> verify(studentRepository, new Times(1)).deleteById(any())
            );
        }

        @Test
        @DisplayName("deleteNegativeTest")
        void shouldThrowStudentNotFoundExceptionWhenTheIdIsNotFoundAtTheRemovedStudent() {
            when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());
            assertAll(
                    () -> assertThatExceptionOfType(StudentNotFoundException.class)
                            .isThrownBy(() -> studentService.delete(anyLong())),
                    () -> verify(studentRepository, new Times(1)).findById(anyLong()),
                    () -> verify(studentRepository, new Times(0)).deleteById(anyLong())
            );
        }
    }

    @Test
    @DisplayName("findStudentsByAgeBetweenPositiveTest")
    void shouldReturnListOfStudentWithAgeBetweenFromTo() {
        List<StudentDtoOut> students = List.of(STUDENT_DTO_OUT);
        when(studentRepository.findStudentsByAgeBetween(anyInt(), anyInt())).thenReturn(List.of(STUDENT));
        assertAll(
                () -> assertThat(studentService.findStudentsByAgeBetween(11, 13))
                        .containsExactlyInAnyOrderElementsOf(students),
                () -> verify(studentRepository, new Times(1))
                        .findStudentsByAgeBetween(anyInt(), anyInt())
        );
    }

    @Test
    @DisplayName("findStudentsByAgeBetweenNegativeTest")
    void shouldReturnEmptyListWhenAgeBetweenFromToNotFoundAnyStudents() {
        when(studentRepository.findStudentsByAgeBetween(anyInt(), anyInt())).thenReturn(Collections.emptyList());
        assertAll(
                () -> assertThat(studentService.findStudentsByAgeBetween(11, 13)).isEmpty(),
                () -> verify(studentRepository, new Times(1))
                        .findStudentsByAgeBetween(anyInt(), anyInt())
        );
    }

    @Test
    @DisplayName("findAllTest [1]")
    void shouldReturnAllStudentsIfAgeParamIsNull() {
        when(studentRepository.findAll()).thenReturn(List.of(STUDENT));
        assertAll(
                () -> assertThat(studentService.findAll(null))
                        .containsExactlyInAnyOrderElementsOf(List.of(STUDENT_DTO_OUT)),
                () -> verify(studentRepository, new Times(1)).findAll(),
                () -> verify(studentRepository, new Times(0)).findStudentsByAge(any())
        );
    }

    @Test
    @DisplayName("findAllTest [2]")
    void shouldReturnAllStudentsByAgeIfAgeParamIsNotNull() {
        when(studentRepository.findStudentsByAge(anyInt())).thenReturn(List.of(STUDENT));
        assertAll(
                () -> assertThat(studentService.findAll(11))
                        .containsExactlyInAnyOrderElementsOf(List.of(STUDENT_DTO_OUT)),
                () -> verify(studentRepository, new Times(0)).findAll(),
                () -> verify(studentRepository, new Times(1)).findStudentsByAge(any())
        );
    }

    @Test
    @DisplayName("findStudentsFacultyPositiveTest")
    void shouldReturnStudentsFacultyByStudentsId() {
        FacultyDtoOut facultyDtoOut = new FacultyDtoOut();
        facultyDtoOut.setId(1L);
        facultyDtoOut.setName("name");
        facultyDtoOut.setColor("color");

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(STUDENT));
        assertAll(
                () -> assertThat(studentService.findStudentsFaculty(STUDENT_ID))
                        .isEqualTo(facultyDtoOut),
                () -> verify(studentRepository, new Times(1)).findById(anyLong())
        );
    }

    @Test
    @DisplayName("findStudentsFacultyNegativeTest")
    void shouldThrowStudentNotFoundExceptionIfStudentIdNotFound() {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertAll(
                () -> assertThatExceptionOfType(StudentNotFoundException.class)
                        .isThrownBy(() -> studentService.findStudentsFaculty(STUDENT_ID)),
                () -> verify(studentRepository, new Times(1)).findById(anyLong())
        );
    }
}