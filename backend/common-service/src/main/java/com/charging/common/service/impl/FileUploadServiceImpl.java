package com.charging.common.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class FileUploadServiceImpl implements com.charging.common.service.FileUploadService {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.max-size:10485760}")
    private long maxFileSize;

    @Value("${file.upload.allowed-types:jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx}")
    private String allowedTypes;

    @PostConstruct
    public void init() {
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public String uploadLocal(byte[] data, String filename, String contentType) {
        try {
            validateFile(filename, data.length);
            
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String uniqueFilename = IdUtil.fastSimpleUUID() + "_" + filename;
            String relativePath = datePath + "/" + uniqueFilename;
            String fullPath = uploadPath + "/" + relativePath;

            File targetFile = new File(fullPath);
            FileUtil.mkParentDirs(targetFile);
            FileUtil.writeBytes(data, fullPath);

            return "/uploads/" + relativePath;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public String uploadAliyunOSS(byte[] data, String filename, String contentType) {
        // TODO: 实现阿里云 OSS 上传
        throw new UnsupportedOperationException("阿里云 OSS 上传暂未实现");
    }

    @Override
    public String uploadTencentCOS(byte[] data, String filename, String contentType) {
        // TODO: 实现腾讯云 COS 上传
        throw new UnsupportedOperationException("腾讯云 COS 上传暂未实现");
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl.startsWith("/uploads/")) {
            String relativePath = fileUrl.substring(9);
            String fullPath = uploadPath + "/" + relativePath;
            File file = new File(fullPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void validateFile(String filename, long fileSize) {
        if (fileSize > maxFileSize) {
            throw new RuntimeException("文件大小超过限制：" + (maxFileSize / 1024 / 1024) + "MB");
        }

        String ext = FileUtil.getSuffix(filename).toLowerCase();
        String[] types = allowedTypes.split(",");
        boolean allowed = false;
        for (String type : types) {
            if (type.trim().equalsIgnoreCase(ext)) {
                allowed = true;
                break;
            }
        }

        if (!allowed) {
            throw new RuntimeException("不支持的文件类型：" + ext);
        }
    }

    public String uploadMultipartFile(MultipartFile file) {
        try {
            byte[] data = file.getBytes();
            return uploadLocal(data, file.getOriginalFilename(), file.getContentType());
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }
}
