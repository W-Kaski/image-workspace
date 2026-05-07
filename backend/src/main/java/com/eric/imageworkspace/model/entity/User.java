package com.eric.imageworkspace.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * user
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * userAccount
     */
    private String userAccount;

    /**
     * userPassword
     */
    private String userPassword;

    /**
     * userName
     */
    private String userName;

    /**
     * userAvatar
     */
    private String userAvatar;

    /**
     * userProfile
     */
    private String userProfile;

    /**
     * userRole：user/admin
     */
    private String userRole;

    /**
     * editTime
     */
    private Date editTime;

    /**
     * createTime
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * isDelete
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}