package com.eric.imageworkspace.manage;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.eric.imageworkspace.config.S3ClientConfig;
import com.eric.imageworkspace.exception.BusinessException;
import com.eric.imageworkspace.exception.ErrorCode;
import com.eric.imageworkspace.exception.ThrowUtils;
import com.eric.imageworkspace.model.dto.file.UploadPictureResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Deprecated
public class FileManager {

    @Resource
    private S3ClientConfig s3ClientConfig;

    @Resource
    private S3Manager s3Manager;

    /**
     * 上传图片
     */
    public UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
        validPicture(multipartFile);
        String uuid = RandomUtil.randomString(16);
        String originalFilename = multipartFile.getOriginalFilename();
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("%s/%s", uploadPathPrefix, uploadFilename);
        File file = null;
        try {
            file = File.createTempFile("upload_", null);
            multipartFile.transferTo(file);

            // 手动读取图片信息
            BufferedImage bufferedImage = ImageIO.read(file);
            int picWidth = bufferedImage.getWidth();
            int picHeight = bufferedImage.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();

            s3Manager.putObject(uploadPath, file);

            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            uploadPictureResult.setUrl(s3ClientConfig.getPublicUrl() + "/" + uploadPath);
            uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(FileUtil.getSuffix(originalFilename));
            return uploadPictureResult;
        } catch (Exception e) {
            log.error("picture upload fail", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "upload fail");
        } finally {
            this.deleteTempFile(file);
        }
    }

    private void validPicture(MultipartFile multipartFile) {
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "file cannot be empty");
        long fileSize = multipartFile.getSize();
        final long ONE_M = 1024 * 1024;
        ThrowUtils.throwIf(fileSize > 2 * ONE_M, ErrorCode.PARAMS_ERROR, "file size cannot be over 2MB");
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "png", "jpg", "webp");
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "file type error");
    }

    public void deleteTempFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }
}