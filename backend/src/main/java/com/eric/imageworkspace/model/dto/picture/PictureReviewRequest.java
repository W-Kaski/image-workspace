package com.eric.imageworkspace.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class PictureReviewRequest implements Serializable {

    /**
     * id
     */
    private Long id;


    /**
     * Review State：0-Not reviewed; 1-Pass; 2-Reject
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    private static final long serialVersionUID = 1L;
}