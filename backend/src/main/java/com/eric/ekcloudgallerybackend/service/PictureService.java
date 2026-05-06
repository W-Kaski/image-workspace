package com.eric.ekcloudgallerybackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.ekcloudgallerybackend.model.dto.picture.*;
import com.eric.ekcloudgallerybackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eric.ekcloudgallerybackend.model.entity.User;
import com.eric.ekcloudgallerybackend.model.vo.PictureVO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author EK
* @description 针对表【picture(picture)】的数据库操作Service
* @createDate 2025-09-29 21:42:46
*/
public interface PictureService extends IService<Picture> {

    void validPicture(Picture picture);

    PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser);

    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    void editPicture(PictureEditRequest pictureEditRequest, User loginUser);

    void deletePicture(long pictureId, User loginUser);

    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    void fillReviewParams(Picture picture, User loginUser);

    Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser);

    @Async
    void clearPictureFile(Picture oldPicture);

    void checkPictureAuth(User loginUser, Picture picture);

    List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser);

    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);
}
