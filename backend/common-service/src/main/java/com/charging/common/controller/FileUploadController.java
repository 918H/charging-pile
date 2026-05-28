package com.charging.common.controller;

import com.charging.common.core.response.R;
import com.charging.common.service.FileUploadService;
import com.charging.common.service.NotificationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Resource
    private FileUploadService fileUploadService;

    @PostMapping
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = ((com.charging.common.service.impl.FileUploadServiceImpl) fileUploadService)
                .uploadMultipartFile(file);
            
            Map<String, String> data = new HashMap<>();
            data.put("url", fileUrl);
            data.put("name", file.getOriginalFilename());
            data.put("size", String.valueOf(file.getSize()));
            
            return R.ok(data);
        } catch (Exception e) {
            return R.fail("上传失败：" + e.getMessage());
        }
    }
}
