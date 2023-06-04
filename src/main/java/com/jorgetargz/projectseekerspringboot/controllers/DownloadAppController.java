package com.jorgetargz.projectseekerspringboot.controllers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.io.InputStream;

@Controller
public class DownloadAppController {

    @GetMapping("/downloadApp")
    public ResponseEntity<byte[]> descargarAplicacion() throws IOException {
        Resource resource = new ClassPathResource("project-seeker.apk"); // Nombre del archivo APK

        InputStream inputStream = resource.getInputStream();
        byte[] contenidoArchivo = inputStream.readAllBytes();
        inputStream.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "project-seeker.apk"); // Nombre del archivo de descarga

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(contenidoArchivo.length)
                .body(contenidoArchivo);
    }
}