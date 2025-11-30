package com.example.InventoryMangement.Sales.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class S3Service {

    @Value("${aws.accessKeyId}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public String uploadFile(File file) throws IOException {
        System.out.println("Using region: " + region + " for bucket: " + bucketName);

        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);

        S3Client s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .build();

        String keyName = "invoices/" + file.getName();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .contentType(Files.probeContentType(file.toPath()))
                .build();

        s3.putObject(request, RequestBody.fromFile(file));
        s3.close();

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, keyName);
    }

    public String generatePresignedUrl(String fileName) {
        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);

        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .build();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key("invoices/" + fileName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presigned = presigner.presignGetObject(presignRequest);
        return presigned.url().toString();
    }
}
