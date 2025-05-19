package com.employeeportal.controller.onboarding;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.employeeportal.dto.onboarding.UploadedFiles;
import com.employeeportal.model.GeneralResponse;
import com.employeeportal.serviceImpl.onboarding.UploadFilesService;

import java.io.IOException;

@Api(value = "Files", tags = "Files")
@RestController
@RequestMapping("/api/files")
@Slf4j
@Validated
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UploadFilesController {

    @Autowired
    private UploadFilesService uploadFilesService;

    @PostMapping("/uploadFile")
    @PreAuthorize("isAuthenticated()")
    public GeneralResponse<UploadedFiles> uploadFile(@RequestParam("file") MultipartFile upload) throws IOException {
        // Save the file and get the URL
        UploadedFiles uploadedFile = uploadFilesService.handleFileUpload(upload);
        // Return response with file URL
        return new GeneralResponse<>(true, "Upload file Successfully", uploadedFile);
    }
}
