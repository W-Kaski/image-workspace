package com.eric.imageworkspace.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * space
 * @TableName space
 */
@TableName(value ="space")
@Data
public class Space {
    /**
     * id
     */

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * spaceName
     */
    private String spaceName;

    /**
     * spaceLevel：0-normal 1-plus 2-pro
     */
    private Integer spaceLevel;


    private Integer spaceType;

    /**
     * maxSize
     */
    private Long maxSize;

    /**
     * maxCount
     */
    private Long maxCount;

    /**
     * totalSize
     */
    private Long totalSize;

    /**
     * totalCount
     */
    private Long totalCount;

    /**
     * userId
     */
    private Long userId;

    /**
     * createTime
     */
    private Date createTime;

    /**
     * editTime
     */
    private Date editTime;

    /**
     * updateTime
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