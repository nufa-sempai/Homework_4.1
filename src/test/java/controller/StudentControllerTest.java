package controller;

import org.example.dto.FacultyDtoOut;
import org.example.dto.StudentDtoIn;
import org.example.dto.StudentDtoOut;
import org.example.exception.StudentNotFoundException;
import org.example.service.AvatarService;
import org.example.service.StudentService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StudentController.class)
class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StudentService studentService;
    @MockBean
    private AvatarService avatarService;
    @InjectMocks
    private StudentController studentController;

    private static final long FACULTY_ID = 1L;
    private static final String FACULTY_NAME = "name";
    private static final String FACULTY_COLOR = "color";
    private static final FacultyDtoOut FACULTY_DTO_OUT = new FacultyDtoOut();
    private static final long STUDENT_ID = 1L;
    private static final String STUDENT_NAME = "name";
    private static final int STUDENT_AGE = 11;
    private static final JSONObject STUDENT_OBJECT = new JSONObject();
    private static final StudentDtoOut STUDENT_DTO_OUT = new StudentDtoOut();

    @BeforeEach
    void prepare() throws JSONException {
        STUDENT_OBJECT.put("name", STUDENT_NAME);
        STUDENT_OBJECT.put("age", STUDENT_AGE);
        STUDENT_OBJECT.put("facultyId", FACULTY_ID);

        STUDENT_DTO_OUT.setId(STUDENT_ID);
        STUDENT_DTO_OUT.setName(STUDENT_NAME);
        STUDENT_DTO_OUT.setAge(STUDENT_AGE);
        STUDENT_DTO_OUT.setFaculty(FACULTY_DTO_OUT);

        FACULTY_DTO_OUT.setId(FACULTY_ID);
        FACULTY_DTO_OUT.setName(FACULTY_NAME);
        FACULTY_DTO_OUT.setColor(FACULTY_COLOR);
    }

    @Nested
    @Tag("studentControllerCrud")
    class CrudTests {
        @Test
        @DisplayName("create")
        void shouldSaveStudentAndReturnHim() throws Exception {
            when(studentService.create(any(StudentDtoIn.class)))
                    .thenReturn(STUDENT_DTO_OUT);
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .post("/student")
                                    .content(STUDENT_OBJECT.toString())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(STUDENT_ID))
                    .andExpect(jsonPath("$.name").value(STUDENT_NAME))
                    .andExpect(jsonPath("$.age").value(STUDENT_AGE))
                    .andExpect(jsonPath("$.faculty").value(FACULTY_DTO_OUT));
        }

        @Test
        @DisplayName("get [positive]")
        void shouldReturnStudentByStudentId() throws Exception {
            when(studentService.get(anyLong())).thenReturn(STUDENT_DTO_OUT);
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .get("/student/" + STUDENT_ID)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(STUDENT_ID))
                    .andExpect(jsonPath("$.name").value(STUDENT_NAME))
                    .andExpect(jsonPath("$.age").value(STUDENT_AGE))
                    .andExpect(jsonPath("$.faculty").value(FACULTY_DTO_OUT));
        }

        @Test
        @DisplayName("get [negative]")
        void getShouldReturn404IfStudentIdNotFound() throws Exception {
            when(studentService.get(anyLong())).thenThrow(StudentNotFoundException.class);
            mockMvc.perform(MockMvcRequestBuilders.get("/student/" + STUDENT_ID))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("update [positive]")
        void shouldReturnUpdatedStudent() throws Exception {
            when(studentService.update(anyLong(), any(StudentDtoIn.class)))
                    .thenReturn(STUDENT_DTO_OUT);
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .put("/student/" + STUDENT_ID)
                                    .content(STUDENT_OBJECT.toString())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(STUDENT_ID))
                    .andExpect(jsonPath("$.name").value(STUDENT_NAME))
                    .andExpect(jsonPath("$.age").value(STUDENT_AGE))
                    .andExpect(jsonPath("$.faculty").value(FACULTY_DTO_OUT));
        }

        @Test
        @DisplayName("update [negative]")
        void updateShouldReturn404IfStudentIdNotFound() throws Exception {
            when(studentService.update(anyLong(), any(StudentDtoIn.class)))
                    .thenThrow(StudentNotFoundException.class);
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .put("/student/" + STUDENT_ID)
                                    .content(STUDENT_OBJECT.toString())
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("delete [positive]")
        void shouldReturnDeletedStudent() throws Exception {
            when(studentService.delete(anyLong())).thenReturn(STUDENT_DTO_OUT);
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .delete("/student/" + STUDENT_ID)
                                    .content(STUDENT_OBJECT.toString())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(STUDENT_ID))
                    .andExpect(jsonPath("$.name").value(STUDENT_NAME))
                    .andExpect(jsonPath("$.age").value(STUDENT_AGE))
                    .andExpect(jsonPath("$.faculty").value(FACULTY_DTO_OUT));
        }

        @Test
        @DisplayName("delete [negative]")
        void deleteShouldReturn404IfStudentIdNotFound() throws Exception {
            when(studentService.delete(anyLong())).thenThrow(StudentNotFoundException.class);
            mockMvc.perform(MockMvcRequestBuilders.delete("/student/" + STUDENT_ID))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    @DisplayName("findAll")
    void shouldReturnCollectionOfStudentsByAge() throws Exception {
        when(studentService.findAll(anyInt())).thenReturn(List.of(STUDENT_DTO_OUT));
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/student/" + STUDENT_AGE + "/students")
                                .content(STUDENT_OBJECT.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(STUDENT_ID))
                .andExpect(jsonPath("$[0].name").value(STUDENT_NAME))
                .andExpect(jsonPath("$[0].age").value(STUDENT_AGE))
                .andExpect(jsonPath("$[0].faculty").value(FACULTY_DTO_OUT));
    }

    @Test
    @DisplayName("filter")
    void shouldReturnCollectionOfStudentsByAgeBetween() throws Exception {
        when(studentService.findStudentsByAgeBetween(anyInt(), anyInt()))
                .thenReturn(List.of(STUDENT_DTO_OUT));
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/student/filter")
                                .param("from", String.valueOf(anyInt()))
                                .param("to", String.valueOf(anyInt()))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(STUDENT_ID))
                .andExpect(jsonPath("$[0].name").value(STUDENT_NAME))
                .andExpect(jsonPath("$[0].age").value(STUDENT_AGE))
                .andExpect(jsonPath("$[0].faculty").value(FACULTY_DTO_OUT));
    }

    @Test
    @DisplayName("findStudentsFaculty [positive]")
    void shouldReturnFacultyByStudentId() throws Exception {
        when(studentService.findStudentsFaculty(anyLong())).thenReturn(FACULTY_DTO_OUT);
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/student/" + STUDENT_ID + "/faculty")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(FACULTY_ID))
                .andExpect(jsonPath("$.name").value(FACULTY_NAME))
                .andExpect(jsonPath("$.color").value(FACULTY_COLOR));
    }

    @Test
    @DisplayName("findStudentsFaculty [negative]")
    void shouldReturn404WhenStudentIdNotFound() throws Exception {
        when(studentService.findStudentsFaculty(anyLong())).thenThrow(StudentNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/student/" + STUDENT_ID + "/faculty"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("uploadAvatar")
    void shouldReturn200WhenAvatarSuccessfulUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "avatarImage",
                "1.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "some image".getBytes()
        );
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .multipart("/student/" + STUDENT_ID + "/avatar")
                                .file(file)
                )
                .andExpect(status().isOk());
    }
}