package com.declutter.dclutter.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

        // Get original filename
        String originalFilename = file.getOriginalFilename();

        // Generate unique filename to avoid conflicts
        String randomId = UUID.randomUUID().toString();
        String filename = randomId.concat(originalFilename.substring(originalFilename.lastIndexOf(".")));

        // Create full path
        String filePath = path + File.separator + filename;

        // Create directory if it doesn't exist
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Copy file to destination
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    @Override
    public void deleteImage(String path, String filename) throws IOException {
        String filePath = path + File.separator + filename;
        File file = new File(filePath);

        if (file.exists()) {
            file.delete();
        }
    }
}