package com.sym.friend.service.impl;

import com.sym.friend.model.vo.UserSendMessage;
import com.sym.friend.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class UserServiceImplTest {
    @Resource
    private UserService userService;

    @Test
    void userRegisterTest() {
        String userAccount = "";
        String userEmail = "";
        String userPassword = "";
        String checkPassword = "";
        String code = "";
        Long res;
//        1. 前台传递的参数是否为空
//        Long res = userService.userRegister(userAccount, userEmail, userPassword, checkPassword, code);
//        Assertions.assertNotNull(res);
//        2. 参数校验
//          2. 账户长度不少于 4 位
//        userAccount = "sym";
//        res = userService.userRegister(userAccount, userEmail, userPassword, checkPassword, code);
//        Assertions.assertNotNull(res);
//          3. 邮箱是否填写
//        userAccount = "siyumeng";
//        res = userService.userRegister(userAccount, userEmail, userPassword, checkPassword, code);
//        Assertions.assertNotNull(res);
//          4. 验证码应为 6 位
//        userEmail = "3149433682@qq.com";
//        code = "123";
//        res = userService.userRegister(userAccount, userEmail, userPassword, checkPassword, code);
//        Assertions.assertNotNull(res);
//          5. 密码长度不少于 8 位
//        code = "12345678";
//        userPassword = "123";
//        res = userService.userRegister(userAccount, userEmail, userPassword, checkPassword, code);
//        Assertions.assertNotNull(res);
//        userPassword = "12345678";
//        checkPassword = "123";
//        res = userService.userRegister(userAccount, userEmail, userPassword, checkPassword, code);
//        Assertions.assertNotNull(res);
//        userPassword = "12345678";
//        checkPassword = "12345678";
//        res = userService.userRegister(userAccount, userEmail, userPassword, checkPassword, code);
//        Assertions.assertNotNull(res);
//          6. 用户账号不能存在特殊字符
//        userAccount = "aaaaa&……？";
//        res = userService.userRegister(userAccount, userEmail, userPassword, checkPassword, code);
//        Assertions.assertNotNull(res);
//        3. 用户密码和校验密码相同
//        userPassword = "123456789";
//        checkPassword = "12345678";
//        res = userService.userRegister(userAccount, userEmail, userPassword, checkPassword, code);
//        Assertions.assertNotNull(res);
//        4. 验证码是否相同
//        userPassword = "12345678";
//        checkPassword = "12345678";
//        code = "123456";
//        res = userService.userRegister(userAccount, userEmail, userPassword, checkPassword, code);
//        Assertions.assertNotNull(res);
//        正确注册
        userAccount = "siyumeng";
        userEmail = "3149433682@qq.com";
        userPassword = "123456789";
        checkPassword = "123456789";
        code = "378433";
        res = userService.userRegister(userAccount, userEmail, userPassword, checkPassword, code);
        Assertions.assertEquals(1, res);
    }

    @Test
    void sendMessageTest() {
        UserSendMessage userSendMessage = new UserSendMessage();
        userSendMessage.setUserEmail("3149433682@qq.com");
        Boolean res = userService.sendMessage(userSendMessage);
        Assertions.assertEquals(true, res);
    }
}