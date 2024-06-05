package com.bancolombia.smartsupervision.controller;

import com.bancolombia.smartsupervision.model.PresignedUrlsResponse;
import com.bancolombia.smartsupervision.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @GetMapping("/presigned-urls/{id}")
    public PresignedUrlsResponse getPresignedUrls(@PathVariable String id) {
        return s3Service.getPresignedUrls(id);
    }
}
