package com.eric.imageworkspace.manage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.eric.imageworkspace.config.S3ClientConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

@Component
public class S3Manager {

    @Resource
    private S3ClientConfig s3ClientConfig;

    @Resource
    private AmazonS3 s3Client;

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putObject(String key, File file) {
        ObjectMetadata metadata = new ObjectMetadata();
        // 自动识别 Content-Type
        String suffix = cn.hutool.core.io.FileUtil.getSuffix(file);
        if ("jpg".equalsIgnoreCase(suffix) || "jpeg".equalsIgnoreCase(suffix)) {
            metadata.setContentType("image/jpeg");
        } else if ("png".equalsIgnoreCase(suffix)) {
            metadata.setContentType("image/png");
        } else if ("webp".equalsIgnoreCase(suffix)) {
            metadata.setContentType("image/webp");
        } else if ("gif".equalsIgnoreCase(suffix)) {
            metadata.setContentType("image/gif");
        }
        
        // 明确告诉浏览器不要下载，而是直接展示 (inline)
        metadata.setContentDisposition("inline");

        PutObjectRequest putObjectRequest = new PutObjectRequest(s3ClientConfig.getBucket(), key, file);
        putObjectRequest.setMetadata(metadata);
        return s3Client.putObject(putObjectRequest);
    }

    /**
     * 下载对象
     *
     * @param key 唯一键
     */
    public S3Object getObject(String key) {
        return s3Client.getObject(s3ClientConfig.getBucket(), key);
    }

    /**
     * 上传图片对象
     * 注意：R2 不直接支持腾讯云的 PicOperations (数据万象)，
     * 如果需要生成缩略图，建议在上传前在后端进行处理，或者使用 Cloudflare Transformations。
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putPictureObject(String key, File file) {
        // 这里暂时退化为普通上传，不再支持 PicOperations
        return putObject(key, file);
    }

    /**
     * 删除对象
     *
     * @param key 唯一键
     */
    public void deleteObject(String key) {
        s3Client.deleteObject(s3ClientConfig.getBucket(), key);
    }
}
