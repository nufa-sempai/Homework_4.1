package controller;

import org.example.dto.FacultyDtoIn;
import org.example.dto.FacultyDtoOut;
import org.example.dto.StudentDtoOut;
import org.example.exception.FacultyNotFoundException;
import org.example.service.FacultyService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FacultyController.class)
@Tag("facultyController")
class FacultyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FacultyService facultyService;
    @InjectMocks
    private FacultyController facultyController;

    private static final long FACULTY_ID = 1L;
    private static final String FACULTY_NAME = "name";
    private static final String FACULTY_COLOR = "color";
    private static final JSONObject FACULTY_OBJECT = new JSONObject();
    private static final FacultyDtoOut FACULTY_DTO_OUT = new FacultyDtoOut();

    @BeforeEach
    void prepare() throws JSONException {
        FACULTY_OBJECT.put("name", FACULTY_NAME);
        FACULTY_OBJECT.put("color", FACULTY_COLOR);

        FACULTY_DTO_OUT.setId(FACULTY_ID);
        FACULTY_DTO_OUT.setName(FACULTY_NAME);
        FACULTY_DTO_OUT.setColor(FACULTY_COLOR);
    }

    @Nested
    @Tag("facultyControllerCrud")
    class CrudTests {
        @Test
        @DisplayName("create")
        void shouldSaveFacultyAndReturnHim() throws Exception {
            when(facultyService.create(any(FacultyDtoIn.class)))
                    .thenReturn(FACULTY_DTO_OUT);
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .post("/faculty")
                                    .content(FACULTY_OBJECT.toString())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(FACULTY_ID))
                    .andExpect(jsonPath("$.name").value(FACULTY_NAME))
                    .andExpect(jsonPath("$.color").value(FACULTY_COLOR));
        }

        @Test
        @DisplayName("get [positive]")
        void shouldReturnFacultyByFacultyId() throws Exception {
            when(facultyService.get(anyLong())).thenReturn(FACULTY_DTO_OUT);
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .get("/faculty/" + FACULTY_ID)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(FACULTY_ID))
                    .andExpect(jsonPath("$.name").value(FACULTY_NAME))
                    .andExpect(jsonPath("$.color").value(FACULTY_COLOR));
        }

        @Test
        @DisplayName("get [negative]")
        void getShouldReturn404IfFacultyIdNotFound() throws Exception {
            when(facultyService.get(anyLong())).thenThrow(FacultyNotFoundException.class);
            mockMvc.perform(MockMvcRequestBuilders.get("/faculty/" + FACULTY_ID))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("update [positive]")
        void shouldReturnUpdatedFaculty() throws Exception {
            when(facultyService.update(anyLong(), any(FacultyDtoIn.class)))
                    .thenReturn(FACULTY_DTO_OUT);
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .put("/faculty/" + FACULTY_ID)
                                    .content(FACULTY_OBJECT.toString())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(FACULTY_ID))
                    .andExpect(jsonPath("$.name").value(FACULTY_NAME))
                    .andExpect(jsonPath("$.color").value(FACULTY_COLOR));
        }

        @Test
        @DisplayName("update [negative]")
        void updateShouldReturn404IfFacultyIdNotFound() throws Exception {
            when(facultyService.update(anyLong(), any(FacultyDtoIn.class)))
                    .thenThrow(FacultyNotFoundException.class);
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .put("/faculty/" + FACULTY_ID)
                                    .content(FACULTY_OBJECT.toString())
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("delete [positive]")
        void shouldReturnDeletedFaculty() throws Exception {
            when(facultyService.delete(anyLong())).thenReturn(FACULTY_DTO_OUT);
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .delete("/faculty/" + FACULTY_ID)
                                    .content(FACULTY_OBJECT.toString())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(FACULTY_ID))
                    .andExpect(jsonPath("$.name").value(FACULTY_NAME))
                    .andExpect(jsonPath("$.color").value(FACULTY_COLOR));
        }

        @Test
        @DisplayName("delete [negative]")
        void deleteShouldReturn404IfFacultyIdNotFound() throws Exception {
            when(facultyService.delete(anyLong())).thenThrow(FacultyNotFoundException.class);
            mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/" + FACULTY_ID))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    @DisplayName("filter [positive]")
    void shouldReturnFacultyByColorOrName() throws Exception {
        when(facultyService.findByColorOrName(anyString())).thenReturn(List.of(FACULTY_DTO_OUT));
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/faculty/filter")
                                .param("colorOrName", anyString())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(FACULTY_ID))
                .andExpect(jsonPath("$[0].name").value(FACULTY_NAME))
                .andExpect(jsonPath("$[0].color").value(FACULTY_COLOR));
    }

    @Test
    @DisplayName("filter [negative]")
    void filterShouldReturnEmptyListIfNoMatchesByColorOrName() throws Exception {
        when(facultyService.findByColorOrName(anyString())).thenReturn(Collections.emptyList());
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/faculty/filter")
                                .param("colorOrName", anyString())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("findAllStudentsOnFaculty [positive]")
    void shouldReturnListOfStudentsByFaculty() throws Exception {
        StudentDtoOut studentDtoOut = new StudentDtoOut();
        studentDtoOut.setId(1);
        studentDtoOut.setName("name");
        studentDtoOut.setAge(11);
        studentDtoOut.setFaculty(FACULTY_DTO_OUT);

        when(facultyService.findStudentsByFaculty(anyString())).thenReturn(List.of(studentDtoOut));
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/faculty/" + FACULTY_NAME + "/students")
                                .param("facultyName", anyString())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(studentDtoOut.getId()))
                .andExpect(jsonPath("$[0].name").value(studentDtoOut.getName()))
                .andExpect(jsonPath("$[0].age").value(studentDtoOut.getAge()))
                .andExpect(jsonPath("$[0].faculty").value(studentDtoOut.getFaculty()));
    }

    @Test
    @DisplayName("findAllStudentsOnFaculty [negative]")
    void shouldReturnEmptyListIfNoMatchesByFacultyName() throws Exception {
        when(facultyService.findStudentsByFaculty(anyString())).thenReturn(Collections.emptyList());
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/faculty/" + FACULTY_NAME + "/students")
                                .param("facultyName", anyString())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}