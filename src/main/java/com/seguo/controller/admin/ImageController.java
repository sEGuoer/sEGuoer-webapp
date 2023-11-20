package com.seguo.controller.admin;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/admin/images/")
public class ImageController {
    @Value("${custom.upload.base-path}")
    String uploadBasePath;
    @Value("${custom.upload.from-vditor-dir-under-base-path}")
    String fromVditorDirUnderBasePath;

    @PostMapping("uploadFromVditor")
    public Map<String, Object> uploadFromVditor(@RequestParam("image[]") MultipartFile[] images) {
        List<String> errorFiles = new ArrayList<>();
        Map<String, String> imageMap = new HashMap<>();

        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                File dir = new File(uploadBasePath + File.separator + fromVditorDirUnderBasePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String originalFilename = image.getOriginalFilename();
                assert originalFilename != null;
                String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
                String newFilename = UUID.randomUUID() + suffix;
                try {
                    image.transferTo(new File(dir.getAbsolutePath() + File.separator + newFilename));
                } catch (IOException e) {
                    errorFiles.add(originalFilename);
                }

                imageMap.put(originalFilename, "/" + fromVditorDirUnderBasePath + File.separator + newFilename);
            }
        }

        Data data = new Data();
        data.setErrFiles(errorFiles);
        data.setSuccMap(imageMap);

        Map<String, Object> result = new HashMap<>();
        if (errorFiles.isEmpty()) {
            result.put("msg", "success");
            result.put("code", 0);
        } else {
            result.put("msg", "error");
            result.put("code", -1);
        }
        result.put("data", data);

        return result;
    }

    @lombok.Data
    private static class Data {
        private List<String> errFiles;
        private Map<String, String> succMap;
    }
}

