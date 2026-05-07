package com.eric.imageworkspace.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eric.imageworkspace.model.dto.user.UserQueryRequest;
import com.eric.imageworkspace.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eric.imageworkspace.model.vo.LoginUserVO;
import com.eric.imageworkspace.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Eric
* @description service
* @createDate 2025-09-18 23:20:00
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);

    boolean userLogout(HttpServletRequest request);

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    boolean isAdmin(User user);

    String getEncryptPassword(String userPassword);

    LoginUserVO getLoginUserVO(User user);

    UserVO getUserVO(User user);

    List<UserVO> getUserVOList(List<User> userList);
}
