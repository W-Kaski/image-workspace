package com.eric.imageworkspace.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * picture
 * @TableName picture
 */
@TableName(value ="picture")
@Data
public class Picture implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * picture url
     */
    private String url;

    private String thumbnailUrl;

    /**
     * name
     */
    private String name;

    /**
     * introduction
     */
    private String introduction;

    /**
     * category
     */
    private String category;

    /**
     * tags（JSON）
     */
    private String tags;


    private Long spaceId;
    /**
     * picSize
     */
    private Long picSize;

    /**
     * picWidth
     */
    private Integer picWidth;

    /**
     * picHeight
     */
    private Integer picHeight;

    /**
     * picScale
     */
    private Double picScale;

    /**
     * picFormat
     */
    private String picFormat;

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

    private String picColor;

    /**
     * updateTime
     */
    private Date updateTime;

    /**
     * Review：0-Not reviewed; 1-Pass; 2-Reject
     */
    private Integer reviewStatus;

    /**
     * Review message
     */
    private String reviewMessage;

    /**
     * Reviewer Id
     */
    private Long reviewerId;

    /**
     * Review time
     */
    private Date reviewTime;

    /**
     * isDelete
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}