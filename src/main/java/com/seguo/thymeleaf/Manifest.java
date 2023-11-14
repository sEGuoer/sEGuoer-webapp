package com.seguo.thymeleaf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Manifest {
    @JsonProperty("src/main/resources/static/js/app.js")
    private ManifestItem js;

    @JsonProperty("src/main/resources/static/js/app.css")
    private ManifestItem css;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ManifestItem {
        private String file;
        private String src;
        private Boolean isEntry;
        private String[] css;
    }
}
