package com.employeeportal.serviceImpl.onboarding;

import com.employeeportal.dto.onboarding.UploadedFiles;
import com.employeeportal.exception.BadRequestException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@Slf4j
public class UploadFilesService {

    public UploadedFiles handleFileUpload(MultipartFile file) {
        try {
            // Clean the file name
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            String extension = "";
            String originalFilename = "";

            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = fileName.substring(dotIndex);
                originalFilename = fileName.substring(0, dotIndex);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");
            String currentTimestamp = LocalDateTime.now().format(formatter);

            Random random = new Random();
            int randomSevenDigitNumber = 1000000 + random.nextInt(9000000);

            String uniqueFileName = originalFilename + "_" + currentTimestamp + "_" + randomSevenDigitNumber + ""
                    + extension;

            // Set upload path using injected property
            String projectPath = System.getProperty("user.dir"); // gets root path of your project
            Path uploadPath = Paths.get(projectPath, "src", "main", "resources", "uploads");

            // Create directories if they do not exist
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            final String duplicateFileName = originalFilename;
            boolean isDuplicate = Files.list(uploadPath)
                    .anyMatch(path -> path.getFileName().toString().startsWith(duplicateFileName + "_"));

            if (isDuplicate) {
                throw new BadRequestException("File already exists.");
            }

            // Resolve full path
            Path filePath = uploadPath.resolve(uniqueFileName);

            // Save file to disk
            file.transferTo(filePath.toFile());

            // Generate download URL (optional)
            String pathName = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/resources/uploads/") // Adjust this if you map file access
                    .path(uniqueFileName)
                    .toUriString();

            log.info("File uploaded at path: {}", pathName);

            // Save to DB
            UploadedFiles uploadedFile = new UploadedFiles(pathName);

            return uploadedFile;

        } catch (IOException e) {
            log.error("Error saving file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }
}
