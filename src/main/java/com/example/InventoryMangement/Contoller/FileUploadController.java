package com.example.InventoryMangement.Contoller;
import com.example.InventoryMangement.Service.S3Service;
import com.example.InventoryMangement.WhatsappService.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private S3Service s3Service;



    @GetMapping("/presigned/{filename}")
    public ResponseEntity<String> getPresignedUrl(@PathVariable String filename) {
        String presignedUrl = s3Service.generatePresignedUrl(filename);
        return ResponseEntity.ok(presignedUrl);
    }
    @Autowired
    private TwilioService twilioService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        try {
            File tempFile = File.createTempFile("upload-", multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempFile);

            String fileUrl = s3Service.uploadFile(tempFile);


            twilioService.sendPdf(fileUrl);

            return ResponseEntity.ok("✅ File uploaded & sent on WhatsApp!\n" + fileUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(" Failed: " + e.getMessage());
        }
    }

}

