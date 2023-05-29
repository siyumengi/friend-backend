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

    /**
     * 账号
     */
    private String userAccount;
    /**
     * 邮箱
     */
    private String userEmail;

    /**
     * 验证码
     */
    private String code;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 检验密码
     */
    private String checkPassword;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
