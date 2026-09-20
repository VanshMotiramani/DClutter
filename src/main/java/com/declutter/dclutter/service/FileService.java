package com.declutter.dclutter.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    String uploadImage(String path, MultipartFile file) throws IOException;

    void deleteImage(String path, String filename) throws IOException;
}