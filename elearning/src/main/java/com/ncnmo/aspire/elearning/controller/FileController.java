package com.ncnmo.aspire.elearning.controller;

import com.ncnmo.aspire.elearning.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    // Upload a file (course or lesson image)
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("folder") String folder) {
        try {
            String fileName = fileStorageService.storeFile(file, folder);
            String fileDownloadUri = "/api/files/" + folder + "/" + fileName;
            return ResponseEntity.ok(fileDownloadUri);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Could not upload the file.");
        }
    }

    // Retrieve a file (course or lesson image)
    @GetMapping("/{folder}/{fileName:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String folder, @PathVariable String fileName) {
        try {
            Path filePath = fileStorageService.loadFile(fileName, folder);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}