package com.gianteyes.gaarigar.utils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.apache.http.entity.ContentType.*;


@Service
public class FileUpload {
    @org.springframework.beans.factory.annotation.Value("${gaarigar.integrations.enabled:false}")
    private boolean integrationsEnabled;

    private final AmazonS3 amazonS3;
    public FileUpload(AmazonS3 amazonS3) { this.amazonS3 = amazonS3; }
    private final String BUCKET_NAME = "gaarigar";

    public void upload(String fileName,
                       Optional<Map<String, String>> optionalMetaData,
                       InputStream inputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        optionalMetaData.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(objectMetadata::addUserMetadata);
            }
        });
        try {
            amazonS3.putObject(BUCKET_NAME, fileName, inputStream, objectMetadata);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to upload the file", e);
        }
    }

    public byte[] download(String path, String key) {
        try {
            S3Object object = amazonS3.getObject(path, key);
            S3ObjectInputStream objectContent = object.getObjectContent();
            return IOUtils.toByteArray(objectContent);
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to download the file", e);
        }
    }

    public String generateUrl(String key) {
        if (!integrationsEnabled) return "/images/logo.png";
        return amazonS3.generatePresignedUrl(BUCKET_NAME, key,
                new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)).toString();
    }

    private Boolean checkIfValidImage(String contentType) {
        return Arrays.asList(IMAGE_PNG.getMimeType(),
                IMAGE_BMP.getMimeType(),
                IMAGE_GIF.getMimeType(),
                IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType()).contains(contentType);
    }

    private Map<String, String> getImageMetadata(MultipartFile image) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", image.getContentType());
        metadata.put("Content-Length", String.valueOf(image.getSize()));
        return metadata;
    }

    private String generateImagePath(MultipartFile image, String path) {
        return String.format("%s/%s%s", path, UUID.randomUUID(), image.getOriginalFilename());
    }

    public String uploadImage(MultipartFile image, String folder) throws IOException {
        if (!integrationsEnabled) throw new IllegalStateException("File storage is not configured");
        if (!checkIfValidImage(image.getContentType())) {
            throw new IllegalStateException("File uploaded is not an image");
        }
        String imagePath = generateImagePath(image, folder);
        upload(imagePath, Optional.of(getImageMetadata(image)), image.getInputStream());
        return imagePath;
    }
}
