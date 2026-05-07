package com.eric.imageworkspace.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * user register request
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 8735650154179439661L;

    /**
     * account
     */
    private String userAccount;

    /**
     * password
     */
    private String userPassword;

    /**
     * confirm password
     */
    private String checkPassword;

}
