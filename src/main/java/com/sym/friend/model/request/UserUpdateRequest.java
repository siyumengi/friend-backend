package com.sym.friend.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * 用户更新
 * @author siyumeng
 */
@Data
public class UserUpdateRequest {
    /**
     * 用户 ID
     */
     @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    private String username;


    /**
     * 账号
     */
    private String userAccount;

    /**
     * 性别：0-未知；1-男；2-女
     */
    private Integer gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0 - 正常
     */
    private Integer userStatus;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 标签 json 列表
     */
    private String tags;


    /**
     * 个人介绍
     */
    private String profile;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
