package com.sym.friend.service;

import com.sym.friend.common.BaseResponse;
import com.sym.friend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sym.friend.model.vo.UserSendMessage;

/**
* @author siyumeng
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2023-05-29 11:38:40
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userEmail 邮箱
     * @param userPassword 用户密码
     * @param checkPassword 检验密码
     * @param code 验证码
     * @return 用户id
     */
    Long userRegister(String userAccount, String userEmail, String userPassword, String checkPassword, String code);

    /**
     * 验证码
     * @param toEmail 邮箱和验证码
     * @return 是否成功
     */
    Boolean sendMessage(UserSendMessage toEmail);
}
