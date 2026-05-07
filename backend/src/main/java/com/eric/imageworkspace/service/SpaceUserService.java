package com.eric.imageworkspace.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eric.imageworkspace.model.dto.spaceuser.SpaceUserAddRequest;
import com.eric.imageworkspace.model.dto.spaceuser.SpaceUserQueryRequest;
import com.eric.imageworkspace.model.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eric.imageworkspace.model.vo.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author PC
* @description 针对表【space_user(Space user)】的数据库操作Service
* @createDate 2025-10-20 17:55:32
*/
public interface SpaceUserService extends IService<SpaceUser> {

    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    void validSpaceUser(SpaceUser spaceUser, boolean add);

    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);

    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);
}
