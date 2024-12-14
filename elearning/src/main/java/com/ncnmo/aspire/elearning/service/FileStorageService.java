package com.ncnmo.aspire.elearning.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path courseImageStorageLocation = Paths.get("course-images").normalize();
    private final Path lessonImageStorageLocation = Paths.get("lesson-images").normalize();


    public FileStorageService() throws IOException {
        Files.createDirectories(this.courseImageStorageLocation);
        Files.createDirectories(this.lessonImageStorageLocation);
    }

    public String storeFile(MultipartFile file, String folder) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path targetLocation;

        if ("lesson".equals(folder)) {
            targetLocation = this.lessonImageStorageLocation.resolve(fileName);
        } else {
            targetLocation = this.courseImageStorageLocation.resolve(fileName);
        }

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    public Path loadFile(String fileName, String folder) {
        if ("lesson".equals(folder)) {
            return this.lessonImageStorageLocation.resolve(fileName).normalize();
        } else {
            return this.courseImageStorageLocation.resolve(fileName).normalize();
        }
    }

    public void deleteFile(String fileName, String folder) throws IOException {
        Path filePath = loadFile(fileName, folder);
        Files.deleteIfExists(filePath);
    }
}