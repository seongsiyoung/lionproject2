package com.example.lionproject2backend.lessonfile.controller;

import com.example.lionproject2backend.lessonfile.domain.LessonFileStatus;
import com.example.lionproject2backend.lessonfile.domain.LessonFile;
import com.example.lionproject2backend.lessonfile.dto.GetLessonFileDownloadUrlResponse;
import com.example.lionproject2backend.lessonfile.dto.GetLessonFileListResponse;
import com.example.lionproject2backend.lessonfile.dto.PostLessonFileConfirmResponse;
import com.example.lionproject2backend.lessonfile.dto.PostLessonFilePresignedPostResponse;
import com.example.lionproject2backend.lessonfile.service.LessonFileService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LessonFileController.class)
@AutoConfigureMockMvc(addFilters = false)
class LessonFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LessonFileService lessonFileService;

    @Test
    void create_presigned_post_returns_upload_fields() throws Exception {
        when(lessonFileService.createPresignedPost(eq(1L), eq(10L), any()))
                .thenReturn(PostLessonFilePresignedPostResponse.of(
                        25L,
                        "https://s3.example.com",
                        Map.of("key", "pending/lessons/1/files/abc"),
                        LocalDateTime.of(2026, 8, 9, 10, 10),
                        10_485_760L,
                        List.of("application/pdf")
                ));

        mockMvc.perform(post("/api/lessons/1/files/presigned-post")
                        .with(userPrincipal(10L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type":"MATERIAL",
                                  "originalFileName":"spring.pdf",
                                  "contentType":"application/pdf",
                                  "sizeBytes":1024,
                                  "checksumSha256":"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fileId").value(25))
                .andExpect(jsonPath("$.data.uploadUrl").value("https://s3.example.com"));
    }

    @Test
    void create_presigned_post_validates_request_body() throws Exception {
        mockMvc.perform(post("/api/lessons/1/files/presigned-post")
                        .with(userPrincipal(10L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type":"MATERIAL",
                                  "originalFileName":"",
                                  "contentType":"application/pdf",
                                  "sizeBytes":1024,
                                  "checksumSha256":"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(lessonFileService);
    }

    @Test
    void confirm_upload_returns_confirm_result() throws Exception {
        LessonFile file = mock(LessonFile.class);
        when(file.getId()).thenReturn(25L);
        when(file.getStatus()).thenReturn(LessonFileStatus.VALIDATED);
        when(file.getOriginalFileName()).thenReturn("spring.pdf");
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSizeBytes()).thenReturn(1024L);
        PostLessonFileConfirmResponse response = PostLessonFileConfirmResponse.from(file);
        when(lessonFileService.confirmUpload(25L, 10L)).thenReturn(response);

        mockMvc.perform(post("/api/lesson-files/25/confirm-upload")
                        .with(userPrincipal(10L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fileId").value(25))
                .andExpect(jsonPath("$.data.status").value("VALIDATED"));
    }

    @Test
    void get_lesson_files_returns_file_list() throws Exception {
        when(lessonFileService.getLessonFiles(1L, 10L))
                .thenReturn(GetLessonFileListResponse.from(List.of()));

        mockMvc.perform(get("/api/lessons/1/files")
                        .with(userPrincipal(10L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.files").isArray());
    }

    @Test
    void create_download_url_returns_signed_url() throws Exception {
        when(lessonFileService.createDownloadUrl(25L, 10L))
                .thenReturn(GetLessonFileDownloadUrlResponse.of(
                        25L,
                        "https://cdn.example.com/file",
                        LocalDateTime.of(2026, 8, 9, 10, 10)
                ));

        mockMvc.perform(get("/api/lesson-files/25/download-url")
                        .with(userPrincipal(10L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.downloadUrl").value("https://cdn.example.com/file"));
    }

    @Test
    void delete_file_delegates_to_service() throws Exception {
        mockMvc.perform(delete("/api/lesson-files/25")
                        .with(userPrincipal(10L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(lessonFileService).deleteFile(25L, 10L);
    }

    private RequestPostProcessor userPrincipal(Long userId) {
        return request -> {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(userId, null, List.of()));
            SecurityContextHolder.setContext(context);
            return request;
        };
    }
}
