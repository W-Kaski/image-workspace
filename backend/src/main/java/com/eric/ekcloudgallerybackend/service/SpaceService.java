package com.eric.ekcloudgallerybackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eric.ekcloudgallerybackend.model.dto.space.SpaceAddRequest;
import com.eric.ekcloudgallerybackend.model.dto.space.SpaceQueryRequest;
import com.eric.ekcloudgallerybackend.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eric.ekcloudgallerybackend.model.entity.User;
import com.eric.ekcloudgallerybackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author PC
* @description 针对表【space(space)】的数据库操作Service
* @createDate 2025-10-15 22:44:57
*/
public interface SpaceService extends IService<Space> {

    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    void validSpace(Space space, boolean add);

    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    void fillSpaceBySpaceLevel(Space space);

    void checkSpaceAuth(User loginUser, Space space);
}
