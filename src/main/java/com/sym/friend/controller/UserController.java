package com.sym.friend.controller;


import com.sym.friend.common.BaseResponse;

import com.sym.friend.common.ErrorCode;
import com.sym.friend.common.ResultUtils;
import com.sym.friend.exception.BusinessException;
import com.sym.friend.model.request.UserRegisterRequest;

import com.sym.friend.model.vo.UserSendMessage;
import com.sym.friend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户表(User)表控制层
 *
 * @author siyumeng
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:3000/"})
@Slf4j
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册类
     * @return 用户id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
//        1.校验参数是否为空
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
//        2.提取参数
        String userAccount = userRegisterRequest.getUserAccount();
        String userEmail = userRegisterRequest.getUserEmail();
        String userPassword = userRegisterRequest.getUserPassword();
        String code = userRegisterRequest.getCode();
        String checkPassword = userRegisterRequest.getCheckPassword();
//        3.注册
        Long res = userService.userRegister(userAccount, userEmail, userPassword, checkPassword, code);
        return ResultUtils.success(res);
    }

    /**
     * 验证码登录和注册
     * @param userSendMessage 邮箱和验证码
     * @return 是否成功
     */
    @PostMapping("/sendMessage")
    public BaseResponse<Boolean> sendMessage(@RequestBody UserSendMessage userSendMessage) {
        log.info("userSendMessage:" + userSendMessage.toString());
        return ResultUtils.success(userService.sendMessage(userSendMessage));
    }
}

