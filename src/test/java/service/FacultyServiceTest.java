package service;

import org.example.dto.FacultyDtoIn;
import org.example.dto.FacultyDtoOut;
import org.example.dto.StudentDtoOut;
import org.example.exception.FacultyNameNotFoundException;
import org.example.exception.FacultyNotFoundException;
import org.example.mapper.FacultyMapper;
import org.example.mapper.StudentMapper;
import org.example.model.Faculty;
import org.example.model.Student;
import org.example.repository.FacultyRepository;
import org.example.repository.StudentRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("facultyService")
class FacultyServiceTest {

    private static final FacultyDtoIn FACULTY_DTO_IN = new FacultyDtoIn();
    private static final FacultyDtoOut FACULTY_DTO_OUT = new FacultyDtoOut();
    private static final Faculty FACULTY = new Faculty();
    private static final long FACULTY_ID = 1L;
    private FacultyService facultyService;
    private final FacultyMapper facultyMapper = new FacultyMapper();
    private final FacultyRepository facultyRepository = mock(FacultyRepository.class);
    private final StudentRepository studentRepository = mock(StudentRepository.class);

    @BeforeEach
    void prepare() {
        facultyService = new FacultyService(
                facultyRepository,
                facultyMapper,
                studentRepository,
                new StudentMapper(facultyMapper, facultyRepository)
        );

        FACULTY_DTO_IN.setName("name");
        FACULTY_DTO_IN.setColor("color");

        FACULTY.setId(1L);
        FACULTY.setName("name");
        FACULTY.setColor("color");

        FACULTY_DTO_OUT.setId(1L);
        FACULTY_DTO_OUT.setName("name");
        FACULTY_DTO_OUT.setColor("color");
    }

    public static Stream<Arguments> paramByFindByColorOrNameParamTest() {
        return Stream.of(
                Arguments.of("color", List.of(FACULTY_DTO_OUT)),
                Arguments.of("name", List.of(FACULTY_DTO_OUT))
        );
    }

    @ParameterizedTest(name = "test [{index}]")
    @DisplayName("findByColorOrNamePositiveTest")
    @MethodSource("paramByFindByColorOrNameParamTest")
    void shouldReturnListOfFacultyByColor(String colorOrName, List<FacultyDtoOut> faculties) {
        when(facultyRepository.findAllByColorContainingIgnoreCaseOrNameContainingIgnoreCase("color", "color"))
                .thenReturn(List.of(FACULTY));
        when(facultyRepository.findAllByColorContainingIgnoreCaseOrNameContainingIgnoreCase("name", "name"))
                .thenReturn(List.of(FACULTY));
        assertAll(
                () -> assertThat(facultyService.findByColorOrName(colorOrName))
                        .containsExactlyInAnyOrderElementsOf(faculties)
                        .hasSize(1),
                () -> verify(facultyRepository, new Times(1))
                        .findAllByColorContainingIgnoreCaseOrNameContainingIgnoreCase(anyString(), anyString())
        );
    }

    @Test
    @DisplayName("findByColorOrNameNegativeTest")
    void shouldThrowFacultyNotFoundExceptionWhenIncorrectParametersArrived() {
        when(facultyRepository.findAllByColorContainingIgnoreCaseOrNameContainingIgnoreCase(anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        assertAll(
                () -> assertThat(facultyService.findByColorOrName("")).isEmpty(),
                () -> verify(facultyRepository, new Times(1))
                        .findAllByColorContainingIgnoreCaseOrNameContainingIgnoreCase(anyString(), anyString())
        );
    }

    @Test
    @DisplayName("findStudentsByFacultyPositiveTest")
    void shouldReturnAllStudentsByFacultyName() {
        List<Student> students = new ArrayList<>();
        students.add(new Student());
        students.add(new Student());
        students.add(new Student());

        List<StudentDtoOut> studentsDto = new ArrayList<>();
        studentsDto.add(new StudentDtoOut());
        studentsDto.add(new StudentDtoOut());
        studentsDto.add(new StudentDtoOut());

        when(facultyRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(FACULTY));
        when(studentRepository.findAllByFaculty_id(FACULTY_ID)).thenReturn(students);
        assertAll(
                () -> assertThat(facultyService.findStudentsByFaculty("name"))
                        .containsExactlyInAnyOrderElementsOf(studentsDto),
                () -> verify(facultyRepository, new Times(1))
                        .findByNameIgnoreCase(anyString()),
                () -> verify(studentRepository, new Times(1))
                        .findAllByFaculty_id(anyLong())
        );
    }

    @Test
    @DisplayName("findStudentsByFacultyNegativeTest")
    void shouldThrowFacultyNameNotFoundExceptionWhenFacultyNameNotFound() {
        when(facultyRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        assertAll(
                () -> assertThatExceptionOfType(FacultyNameNotFoundException.class)
                        .isThrownBy(() -> facultyService.findStudentsByFaculty("name")),
                () -> verify(facultyRepository, new Times(1)).findByNameIgnoreCase(anyString())
        );
    }

    @Nested
    @Tag("facultyServiceCrud")
    class CrudTests {
        @Test
        @DisplayName("createTest")
        void shouldConvertDtoInToEntityThenConvertToDtoOutAndReturnDtoOut() {
            when(facultyRepository.save(any())).thenReturn(FACULTY);

            FacultyDtoOut actual = facultyService.create(FACULTY_DTO_IN);

            assertAll(
                    () -> assertThat(actual).isEqualTo(FACULTY_DTO_OUT),
                    () -> verify(facultyRepository, new Times(1)).save(any())
            );
        }

        @Test
        @DisplayName("getPositiveTest")
        void shouldReturnStudentDtoOutById() {
            when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(FACULTY));

            FacultyDtoOut actual = facultyService.get(FACULTY_ID);

            assertAll(
                    () -> assertThat(actual).isEqualTo(FACULTY_DTO_OUT),
                    () -> verify(facultyRepository, new Times(1)).findById(anyLong())
            );
        }

        @Test
        @DisplayName("getNegativeTest")
        void shouldThrowFacultyNotFoundExceptionWhenFacultyIdNotFound() {
            when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());
            assertAll(
                    () -> assertThatExceptionOfType(FacultyNotFoundException.class)
                            .isThrownBy(() -> facultyService.get(anyLong())),
                    () -> verify(facultyRepository, new Times(1)).findById(anyLong())
            );
        }

        @Test
        @DisplayName("updatePositiveTest")
        void shouldUpdateFacultyAndReturnFacultyDtoOut() {
            Faculty someFaculty = new Faculty();
            someFaculty.setId(1L);
            someFaculty.setName("otherName");
            someFaculty.setColor("otherColor");

            when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(someFaculty));
            when(facultyRepository.save(any())).thenReturn(FACULTY);

            FacultyDtoOut actual = facultyService.update(FACULTY_ID, FACULTY_DTO_IN);

            assertAll(
                    () -> assertThat(actual).isEqualTo(FACULTY_DTO_OUT),
                    () -> verify(facultyRepository, new Times(1)).findById(anyLong()),
                    () -> verify(facultyRepository, new Times(1)).save(any())
            );
        }

        @Test
        @DisplayName("updateNegativeTest")
        void shouldThrowFacultyNotFoundExceptionWhenNewFacultyIdHasNoFoundInCurrentIdList() {
            when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertAll(
                    () -> assertThatExceptionOfType(FacultyNotFoundException.class)
                            .isThrownBy(() -> facultyService.update(10, FACULTY_DTO_IN)),
                    () -> verify(facultyRepository, new Times(1)).findById(anyLong()),
                    () -> verify(facultyRepository, new Times(0)).save(any())
            );
        }

        @Test
        @DisplayName("deletePositiveTest")
        void shouldDeleteFacultyAndReturnFacultyDtoOut() {
            when(facultyRepository.findById(FACULTY_ID)).thenReturn(Optional.of(FACULTY));

            assertAll(
                    () -> assertThat(facultyService.delete(FACULTY_ID)).isEqualTo(FACULTY_DTO_OUT),
                    () -> verify(facultyRepository, new Times(1)).findById(anyLong()),
                    () -> verify(facultyRepository, new Times(1)).deleteById(any())
            );
        }

        @Test
        @DisplayName("deleteNegativeTest")
        void shouldThrowFacultyNotFoundExceptionWhenTheIdIsNotFoundAtTheRemovedFaculty() {
            when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());
            assertAll(
                    () -> assertThatExceptionOfType(FacultyNotFoundException.class)
                            .isThrownBy(() -> facultyService.delete(anyLong())),
                    () -> verify(facultyRepository, new Times(1)).findById(anyLong()),
                    () -> verify(facultyRepository, new Times(0)).deleteById(anyLong())
            );
        }
    }
}