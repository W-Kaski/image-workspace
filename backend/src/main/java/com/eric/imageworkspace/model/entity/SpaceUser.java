package com.eric.imageworkspace.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * Space user
 * @TableName space_user
 */
@TableName(value ="space_user")
@Data
public class SpaceUser {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * spaceId
     */
    private Long spaceId;

    /**
     * userId
     */
    private Long userId;

    /**
     * spaceRole：viewer/editor/admin
     */
    private String spaceRole;

    /**
     * createTime
     */
    private Date createTime;

    /**
     * updateTime
     */
    private Date updateTime;
}