package com.sym.friend.model.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * @author:siyumeng
 * @Date:2023/3/20 15:43
 */
@Data
public class UserForgetRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userEmail;

    private String code;

    private String userPassword;


}