package com.sym.friend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新密码
 * @author siyumeng
 */
@Data
public class UserForgetRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userEmail;

    private String code;

    private String userPassword;
}
