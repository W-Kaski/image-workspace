package com.eric.imageworkspace.manage.upload;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.eric.imageworkspace.config.S3ClientConfig;
import com.eric.imageworkspace.exception.BusinessException;
import com.eric.imageworkspace.exception.ErrorCode;
import com.eric.imageworkspace.manage.S3Manager;
import com.eric.imageworkspace.model.dto.file.UploadPictureResult;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    private S3ClientConfig s3ClientConfig;

    @Resource
    private S3Manager s3Manager;

    /**
     * 上传图片
     */
    public UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        // 1. 校验图片
        validPicture(inputSource);

        // 2. 图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFilename = getOriginFilename(inputSource);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("%s/%s", uploadPathPrefix, uploadFilename);

        File file = null;
        try {
            // 3. 创建临时文件
            file = File.createTempFile("upload_", "." + FileUtil.getSuffix(originalFilename));
            processFile(inputSource, file);

            // 4. 获取图片基本信息 (手动读取，不再依赖云端返回)
            BufferedImage bufferedImage = ImageIO.read(file);
            int picWidth = bufferedImage.getWidth();
            int picHeight = bufferedImage.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();

            // 5. 上传图片到 R2
            s3Manager.putObject(uploadPath, file);

            // 6. 封装返回结果
            UploadPictureResult result = new UploadPictureResult();
            result.setUrl(s3ClientConfig.getPublicUrl() + "/" + uploadPath);
            result.setPicName(FileUtil.mainName(originalFilename));
            result.setPicSize(FileUtil.size(file));
            result.setPicWidth(picWidth);
            result.setPicHeight(picHeight);
            result.setPicScale(picScale);
            result.setPicFormat(FileUtil.getSuffix(originalFilename));
            // R2 暂时不提供平均色调，设为空或默认值
            result.setPicColor("");
            // 缩略图暂时使用原图 (R2 可通过 URL 参数进行动态缩放)
            result.setThumbnailUrl(s3ClientConfig.getPublicUrl() + "/" + uploadPath);

            return result;
        } catch (Exception e) {
            log.error("Fail to upload picture to R2", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to upload picture");
        } finally {
            this.deleteTempFile(file);
        }
    }

    protected abstract void validPicture(Object inputSource);
    protected abstract String getOriginFilename(Object inputSource);
    protected abstract void processFile(Object inputSource, File file) throws Exception;

    public void deleteTempFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }
}