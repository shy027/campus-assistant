package top.shy.campusassistant.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 *
 * @author 15331
 */
public interface FileUploadService {

    /**
     * 上传头像到OSS
     *
     * @param file 上传的文件
     * @param userId 用户ID
     * @return 文件访问URL
     */
    String uploadAvatar(MultipartFile file, Integer userId);
}
