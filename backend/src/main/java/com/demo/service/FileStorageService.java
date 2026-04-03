package com.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件存储服务接口
 * 提供文件的上传、删除以及合法性校验功能
 */
public interface FileStorageService {

    /**
     * 保存上传的文件
     * @param file 原始文件对象
     * @param folder 存储的子文件夹名称
     * @return 文件的相对访问路径
     * @throws IOException 文件读写异常
     */
    String saveFile(MultipartFile file, String folder) throws IOException;

    /**
     * 根据路径删除文件
     * @param filePath 文件相对路径
     * @return 是否删除成功
     */
    boolean deleteFile(String filePath);

    /**
     * 验证文件类型是否合法
     * @param file 待验证文件
     * @return 是否合法
     */
    boolean validateFileType(MultipartFile file);

    /**
     * 验证文件大小是否在允许范围内
     * @param file 待验证文件
     * @return 是否合法
     */
    boolean validateFileSize(MultipartFile file);
}
