package com.seguo.thymeleaf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ManifestUtil {

    private final Manifest manifest;
    private static final String PREFIX = "/build/";

    public ManifestUtil() {
        try {
            File file = ResourceUtils.getFile("classpath:static/build/manifest.json");
            String json = Files.readString(file.toPath());
            ObjectMapper objectMapper = new ObjectMapper();
            this.manifest = objectMapper.readValue(json, Manifest.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getJs() {
        return PREFIX + this.manifest.getJs().getFile();
    }

    public String getCss() {
        return PREFIX + this.manifest.getCss().getFile();
    }

    public static void main(String[] args) {
        System.out.println(new ManifestUtil().getJs());
        System.out.println(new ManifestUtil().getCss());
    }
}