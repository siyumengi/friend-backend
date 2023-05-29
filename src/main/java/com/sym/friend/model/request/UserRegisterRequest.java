package com.sym.friend.model.request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author siyumeng
 */
@Data
public class UserRegisterRequest implements Serializable {


    private String userAccount;

    private String userEmail;

    private String code;

    private String userPassword;

    private String checkPassword;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
