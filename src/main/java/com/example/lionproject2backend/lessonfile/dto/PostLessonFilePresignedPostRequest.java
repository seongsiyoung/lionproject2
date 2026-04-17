package com.example.lionproject2backend.lessonfile.dto;

import com.example.lionproject2backend.lessonfile.domain.LessonFileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostLessonFilePresignedPostRequest {

    @NotNull
    private LessonFileType type;

    @NotBlank
    @Size(max = 255)
    private String originalFileName;

    @NotBlank
    @Size(max = 100)
    private String contentType;

    @Positive
    private long sizeBytes;

    @NotBlank
    @Pattern(regexp = "^[a-fA-F0-9]{64}$")
    private String checksumSha256;
}
