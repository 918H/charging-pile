package com.charging.common.service;

public interface FileUploadService {
    String uploadLocal(byte[] data, String filename, String contentType);
    String uploadAliyunOSS(byte[] data, String filename, String contentType);
    String uploadTencentCOS(byte[] data, String filename, String contentType);
    void deleteFile(String fileUrl);
}
