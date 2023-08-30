package controller;

import org.example.model.Avatar;
import org.example.model.Student;
import org.example.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AvatarController.class)
class AvatarControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StudentService studentService;
    private static final long STUDENT_ID = 1L;

    @Test
    @DisplayName("downloadAvatar [1]")
    void shouldReturnAvatarFromDataBaseByStudentId() throws Exception {
        Avatar avatar = new Avatar();
        avatar.setId(1L);
        avatar.setFilePath("/avatarDir/1.jpg");
        avatar.setFileSize(12580);
        avatar.setMediaType(MediaType.IMAGE_JPEG_VALUE);
        avatar.setData("some image".getBytes());
        avatar.setStudent(new Student());

        when(studentService.findAvatarByStudentId(anyLong())).thenReturn(avatar);
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/avatar/" + STUDENT_ID + "/preview")
                                .accept(MediaType.IMAGE_JPEG_VALUE)
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("downloadAvatar [2]")
    void shouldReturnAvatarFromServerDirectoryByStudentId() throws Exception {
        Avatar avatar = new Avatar();
        avatar.setId(1L);
        avatar.setFilePath("avatarsDir/1.jpg");
        avatar.setFileSize(12580);
        avatar.setMediaType(MediaType.IMAGE_JPEG_VALUE);
        avatar.setData("some image".getBytes());
        avatar.setStudent(new Student());

        when(studentService.findAvatarByStudentId(anyLong())).thenReturn(avatar);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/avatar/" + STUDENT_ID)
                                .accept(MediaType.IMAGE_JPEG_VALUE)
                )
                .andExpect(status().isOk());
    }
}