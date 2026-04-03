package com.demo.service.impl;

import com.demo.config.FileUploadConfig;
import com.demo.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * 文件存储服务实现类
 */
@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final FileUploadConfig fileUploadConfig;

    public FileStorageServiceImpl(FileUploadConfig fileUploadConfig) {
        this.fileUploadConfig = fileUploadConfig;
    }

    @Override
    public String saveFile(MultipartFile file, String folder) throws IOException {
        // 创建日期子目录，如：2024/04/02
        String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path uploadPath = Paths.get(fileUploadConfig.getBasePath(), folder, dateFolder);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 生成唯一文件名（UUID + 原始扩展名）
        String originalFilename = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFilename);
        if (extension == null) {
            extension = "bin"; // 默认扩展名
        }
        
        String fileName = UUID.randomUUID() + "." + extension.toLowerCase();
        Path filePath = uploadPath.resolve(fileName);
        
        // 保存文件到磁盘
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // 返回前端可访问的相对路径
        String accessUrl = "/uploads/" + folder + "/" + dateFolder + "/" + fileName;
        log.info("文件上传成功: {}", accessUrl);
        return accessUrl;
    }

    @Override
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(fileUploadConfig.getBasePath() + filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("文件删除成功: {}", filePath);
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error("文件删除失败: {}", filePath, e);
            return false;
        }
    }

    @Override
    public boolean validateFileType(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }
        String extension = Objects.requireNonNull(StringUtils.getFilenameExtension(originalFilename)).toLowerCase();
        // 校验文件后缀是否在允许列表中
        return Arrays.asList(fileUploadConfig.getAllowedTypes()).contains(extension);
    }

    @Override
    public boolean validateFileSize(MultipartFile file) {
        // 校验文件大小是否超出配置限制（单位：MB）
        return file.getSize() <= fileUploadConfig.getMaxFileSize() * 1024 * 1024;
    }
}
