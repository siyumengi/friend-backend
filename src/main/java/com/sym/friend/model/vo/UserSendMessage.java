package com.sym.friend.model.vo;

import lombok.Data;

import java.io.Serializable;


/**
 * @author siyumeng
 */
@Data
public class UserSendMessage implements Serializable {

    private static final long serialVersionUID = 46412442243484364L;

    /**
     * 用户邮箱
     */
    private String userEmail;
    /**
     * 验证码
     */
    private String code;

}