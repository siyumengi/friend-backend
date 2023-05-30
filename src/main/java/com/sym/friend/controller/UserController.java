package com.sym.friend.controller;


import com.sym.friend.common.BaseResponse;
import com.sym.friend.common.ErrorCode;
import com.sym.friend.common.ResultUtils;
import com.sym.friend.exception.BusinessException;
import com.sym.friend.model.domain.User;
import com.sym.friend.model.dto.UserDto;
import com.sym.friend.model.request.UserLoginRequest;
import com.sym.friend.model.request.UserRegisterRequest;
import com.sym.friend.model.request.UserUpdateRequest;
import com.sym.friend.model.vo.UserSendMessage;
import com.sym.friend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.sym.friend.constant.UserConstant.USER_LOGIN_STATE;

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
     *
     * @param userSendMessage 邮箱和验证码
     * @return 是否成功
     */
    @PostMapping("/sendMessage")
    public BaseResponse<Boolean> sendMessage(@RequestBody UserSendMessage userSendMessage) {
        log.info("userSendMessage:" + userSendMessage.toString());
        return ResultUtils.success(userService.sendMessage(userSendMessage));
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求体
     * @param request          req
     * @return 用户
     */
    @PostMapping("/login")
    public BaseResponse<UserDto> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAllBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        UserDto userRes = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(userRes);
    }

    /**
     * 用户退出
     *
     * @param id      登出用户 id
     * @param request req
     * @return 退出结果
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(String id, HttpServletRequest request) {
        if (request == null || id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean res = userService.userLogout(id, request);
        return ResultUtils.success(res);
    }

    /**
     * 当前用户信息
     *
     * @param id      当前用户 id
     * @param request req
     * @return 用户信息
     */
    @GetMapping("/current")
    public BaseResponse<UserDto> getCurrentUser(@RequestParam String id, HttpServletRequest request) {
        log.info("id:" + id);
        if (id == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserDto currentUser = userService.getCurrentUser(id, request);
        return ResultUtils.success(currentUser);
    }

    /**
     * 用户更新基本信息
     * @param userUpdateRequest 用户更新类
     * @param request req
     * @return 是否成功
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody UserUpdateRequest userUpdateRequest , String currentId, HttpServletRequest request) {
        if (userUpdateRequest == null || currentId == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest , user);
        int res = userService.updateUser(user, currentId, request);
        return ResultUtils.success(res);
    }
}

