package top.shy.campusassistant.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.shy.campusassistant.config.OssProperties;
import top.shy.campusassistant.service.FileUploadService;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务实现
 *
 * @author 15331
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final OSS ossClient;
    private final OssProperties ossProperties;

    /**
     * 允许的图片格式
     */
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");

    /**
     * 最大文件大小：5MB
     */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Override
    public String uploadAvatar(MultipartFile file, Integer userId) {
        // 验证文件是否为空
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小不能超过5MB");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new RuntimeException("文件名不能为空");
        }

        // 验证文件扩展名
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new RuntimeException("只支持上传 jpg、jpeg、png、gif 格式的图片");
        }

        // 生成唯一文件名
        String fileName = generateFileName(userId, extension);

        // 构建OSS文件路径
        String objectName = ossProperties.getFolder() + "/" + fileName;

        try {
            // 上传文件到OSS
            InputStream inputStream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossProperties.getBucketName(),
                    objectName,
                    inputStream
            );
            ossClient.putObject(putObjectRequest);

            // 构建文件访问URL
            String fileUrl = "https://" + ossProperties.getBucketName() + "." +
                    ossProperties.getEndpoint() + "/" + objectName;

            log.info("文件上传成功，用户ID：{}，文件URL：{}", userId, fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("文件上传失败，用户ID：{}", userId, e);
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * 生成唯一文件名
     * 格式：avatar_userId_yyyyMMddHHmmss_uuid.extension
     */
    private String generateFileName(Integer userId, String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return String.format("avatar_%d_%s_%s.%s", userId, timestamp, uuid, extension);
    }
}
