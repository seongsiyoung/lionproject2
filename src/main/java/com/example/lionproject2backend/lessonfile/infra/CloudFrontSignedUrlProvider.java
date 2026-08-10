package com.example.lionproject2backend.lessonfile.infra;

import com.example.lionproject2backend.lessonfile.domain.LessonFile;
import com.example.lionproject2backend.lessonfile.dto.GetLessonFileDownloadUrlResponse;

public interface CloudFrontSignedUrlProvider {

    GetLessonFileDownloadUrlResponse createSignedUrl(LessonFile file);
}
