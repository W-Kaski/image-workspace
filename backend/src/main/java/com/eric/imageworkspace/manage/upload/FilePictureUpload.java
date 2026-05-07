package com.eric.imageworkspace.manage.upload;

import cn.hutool.core.io.FileUtil;
import com.eric.imageworkspace.exception.ErrorCode;
import com.eric.imageworkspace.exception.ThrowUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Service
public class FilePictureUpload extends PictureUploadTemplate {

    @Override
    protected void validPicture(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        // Throw error if file is null
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "File cannot be null");

        // 1. Validate file size
        long fileSize = multipartFile.getSize();
        final long ONE_M = 1024 * 1024;
        // Throw error if file size exceeds 10MB
        ThrowUtils.throwIf(fileSize > 10 * ONE_M, ErrorCode.PARAMS_ERROR, "File size cannot exceed 10MB");

        // 2. Validate file extension
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        // Allowed file extensions
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "png", "jpg", "webp", "gif");
        // Throw error if file type is not allowed
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix.toLowerCase()), ErrorCode.PARAMS_ERROR, "Invalid file type");
    }

    @Override
    protected String getOriginFilename(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        return multipartFile.getOriginalFilename();
    }

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        // Save the uploaded file to the specified File object
        multipartFile.transferTo(file);
    }
}
