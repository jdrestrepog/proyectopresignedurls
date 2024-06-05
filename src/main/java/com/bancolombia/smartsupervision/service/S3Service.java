package com.bancolombia.smartsupervision.service;

import com.bancolombia.smartsupervision.model.PresignedUrlsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public PresignedUrlsResponse getPresignedUrls(String id) {
        List<String> presignedUrls = new ArrayList<>();

        S3Client s3 = null;
        S3Presigner presigner = null;
        try {
            // Inicializar el cliente S3
            s3 = S3Client.builder()
                    .region(Region.US_EAST_1)
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .build();

            // Inicializar el presigner
            presigner = S3Presigner.builder()
                    .region(Region.US_EAST_1)
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .build();

            // Listar los objetos en el bucket con el prefijo dado
            ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                    .bucket(bucketName)
                    .prefix(id + "/")
                    .build();

            ListObjectsResponse listObjectsResponse = s3.listObjects(listObjectsRequest);
            List<S3Object> objects = listObjectsResponse.contents();

            // Log de los objetos encontrados
            System.out.println("Objetos encontrados en el bucket:");
            for (S3Object object : objects) {
                System.out.println(" - " + object.key());
            }

            // Generar URL pre-firmada para cada objeto
            for (S3Object object : objects) {
                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(object.key())
                        .build();

                GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                        .getObjectRequest(getObjectRequest)
                        .signatureDuration(Duration.ofMinutes(10))
                        .build();

                PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(getObjectPresignRequest);
                URL presignedUrl = presignedRequest.url();
                presignedUrls.add(presignedUrl.toString());

                // Log de la URL pre-firmada generada
                System.out.println("URL pre-firmada generada: " + presignedUrl.toString());
            }
        } catch (S3Exception e) {
            System.err.println("Error al interactuar con S3: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        } finally {
            if (s3 != null) {
                s3.close();
            }
            if (presigner != null) {
                presigner.close();
            }
        }

        return new PresignedUrlsResponse(presignedUrls);
    }
}
