package com.sym.friend.service;

import com.sym.friend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sym.friend.model.dto.UserDto;
import com.sym.friend.model.request.UserForgetRequest;
import com.sym.friend.model.vo.TagVo;
import com.sym.friend.model.vo.UserSendMessage;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * @author siyumeng
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2023-05-29 11:38:40
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userEmail     邮箱
     * @param userPassword  用户密码
     * @param checkPassword 检验密码
     * @param code          验证码
     * @return 用户id
     */
    Long userRegister(String userAccount, String userEmail, String userPassword, String checkPassword, String code);

    /**
     * 验证码
     *
     * @param toEmail 邮箱和验证码
     * @return 是否成功
     */
    Boolean sendMessage(UserSendMessage toEmail);

    /**
     * 用户登录
     *
     * @param userAccount  账户
     * @param userPassword 密码
     * @param request      req
     * @return 脱敏后的用户
     */
    UserDto userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户退出
     *
     * @param id      登出用户 id
     * @param request req
     * @return 退出结果
     */
    Boolean userLogout(String id, HttpServletRequest request);

    /**
     * 当前用户
     *
     * @param id      登录用户 id
     * @param request req
     * @return 脱敏后的用户
     */
    UserDto getCurrentUser(String id, HttpServletRequest request);

    /**
     * 更新用户信息
     *
     * @param user      用户信息
     * @param currentId 更新用户信息的用户
     * @param request   req
     * @return 是否成功
     */
    int updateUser(User user, String currentId, HttpServletRequest request);


    /**
     * 是否为管理员
     *
     * @param request req
     * @return 是否为管理员
     */
    public boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param loginUser 用户
     * @return 是否为管理员
     */
    public boolean isAdmin(User loginUser);

    public boolean isAdmin(UserDto loginUser);

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    UserDto getLoginUser(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList
     * @return
     */
    List<UserDto> searchUsersByTags(List<String> tagNameList);

    Boolean forget(UserForgetRequest userForgetRequest);

    /**
     * 匹配用户
     *
     * @param num
     * @param loginUser
     * @return
     */
    List<UserDto> matchUsers(long num, UserDto loginUser) throws IOException;

    TagVo getTags(String id, HttpServletRequest request);
}
