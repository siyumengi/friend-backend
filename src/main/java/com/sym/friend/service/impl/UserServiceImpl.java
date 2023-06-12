package com.sym.friend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sym.friend.common.ErrorCode;
import com.sym.friend.exception.BusinessException;
import com.sym.friend.mapper.UserMapper;
import com.sym.friend.model.domain.User;
import com.sym.friend.model.dto.UserDto;
import com.sym.friend.model.request.UserForgetRequest;
import com.sym.friend.model.vo.UserSendMessage;
import com.sym.friend.service.UserService;
import com.sym.friend.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sym.friend.constant.UserConstant.ADMIN_ROLE;
import static com.sym.friend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author siyumeng
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2023-05-29 11:38:40
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    private static final String SALT = "sym";
    @Resource
    private UserMapper userMapper;

    //把yml配置的邮箱号赋值到from
    @Value("${spring.mail.username}")
    private String from;

    //发送邮件需要的对象
    @Resource
    private JavaMailSender javaMailSender;


    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 注册
     *
     * @param userAccount   用户账户
     * @param userEmail     邮箱
     * @param userPassword  用户密码
     * @param checkPassword 检验密码
     * @param code          验证码
     * @return 用户id
     */
    @Override
    public Long userRegister(String userAccount, String userEmail, String userPassword, String checkPassword, String code) {
        // 1. 前台传递的参数是否为空
        if (StringUtils.isAllBlank(userAccount, userEmail, userPassword, userPassword, checkPassword, code)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 2. 参数校验
        //   1. 账户长度不少于 4 位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度小于 4 位，请重新填写");
        }
        //   2. 验证码应为 6 位
        if (code.length() != 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入 6 位验证码，请重新填写");
        }
        //   3. 密码长度不少于 8 位
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码或校验密码短于 8 位，请重新填写");
        }
        //   4. 用户账号不能存在特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户存在特殊字符，请重新填写");
        }
        //3. 用户密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不相同，请重新填写");
        }
        //4. 验证码是否相同
        //  1. 获取验证码
        String redisKey = String.format("my:user:sendMessage:%s", userEmail);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        log.info(redisKey);
        UserSendMessage sendMessage = (UserSendMessage) valueOperations.get(redisKey);
        if (!Optional.ofNullable(sendMessage).isPresent()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "获取验证码失败!");
        }
        //  2. 比较验证码
        String sendMessageCode = sendMessage.getCode();
        log.info(sendMessageCode);
        if (sendMessageCode == null || !sendMessageCode.equals(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误!");
        }
        //5. 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复，请重新填写");
        }
        //6. 密码加验
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //5. 保存用户
        User user = new User();
        user.setUserPassword(encryptPassword);
        user.setUserAccount(userAccount);
        user.setEmail(userEmail);
        String defaultUrl = "https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E5%8F%AF%E8%8E%89%E5%A4%B4%E5%83%8F&step_word=&hs=0&pn=25&spn=0&di=7214885350303334401&pi=0&rn=1&tn=baiduimagedetail";
        user.setAvatar(defaultUrl);
        boolean saveRes = save(user);
        if (!saveRes) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建用户失败，请重新填写");
        }
        return user.getId();
    }

    /**
     * 验证码
     *
     * @param toEmail 邮箱和验证码
     * @return 是否成功
     */
    @Override
    public Boolean sendMessage(UserSendMessage toEmail) {
        String userEmail = toEmail.getUserEmail();
//        校验邮箱
        if (StringUtils.isEmpty(userEmail)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "email为空");
        }
        String subject = "friend 系统";
        String code = "";
        if (StringUtils.isNoneEmpty(userEmail)) {
            //发送一个六位数的验证码,把验证码变成String类型
            code = ValidateCodeUtils.generateValidateCode(6).toString();
            String text = "【friend 系统】您好，您的验证码为：" + code + "，请在5分钟内使用";
            log.info("验证码为：" + code);
            //发送短信
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(userEmail);
            message.setSubject(subject);
            message.setText(text);
            //发送邮件
            javaMailSender.send(message);
            UserSendMessage userSendMessage = new UserSendMessage();
            userSendMessage.setUserEmail(userEmail);
            userSendMessage.setCode(code);
            // 作为唯一标识
            String redisKey = String.format("my:user:sendMessage:%s", userEmail);
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            // 写缓存
            try {
                valueOperations.set(redisKey, userSendMessage, 300000, TimeUnit.MILLISECONDS);
                UserSendMessage sendMessage = (UserSendMessage) valueOperations.get(redisKey);
                log.info(sendMessage.toString());
                return true;
            } catch (Exception e) {
                log.error("redis set key error", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "缓存失败!");
            }
        }
        return true;
    }

    /**
     * 用户登录
     *
     * @param userAccount  账户
     * @param userPassword 密码
     * @param request      req
     * @return 脱敏后的用户
     */
    @Override
    public UserDto userLogin(String userAccount, String userPassword, HttpServletRequest request) {
//        2. 参数校验
//          1. 是否为空
        if (StringUtils.isAllBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        //   2. 账户长度不少于 4 位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度小于 4 位，请重新填写");
        }
        //   3. 密码长度不少于 8 位
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码短于 8 位，请重新填写");
        }
        //   4. 用户账号不能存在特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户存在特殊字符，请重新填写");
        }
//        3. 密码加盐
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
//        4. 查表判断是否相同
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
//          1.用户是否存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        5. 脱敏
        UserDto userSafe = new UserDto();
        BeanUtils.copyProperties(user, userSafe);
//        6. 记录用户态
        String redisKey = String.format(USER_LOGIN_STATE + userSafe.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(redisKey, userSafe, 30, TimeUnit.MINUTES);
        request.getSession().setAttribute(USER_LOGIN_STATE, userSafe);
//        7. 返回脱敏用户
        return userSafe;
    }

    /**
     * 用户退出
     *
     * @param id      登出用户 id
     * @param request req
     * @return 退出结果
     */
    @Override
    public Boolean userLogout(String id, HttpServletRequest request) {
        if (StringUtils.isEmpty(id) || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//      1. 删除 req
        request.getSession().removeAttribute(USER_LOGIN_STATE);
//      2. 删除 redis 缓存
        String redisKey = String.format(USER_LOGIN_STATE + id);
        log.info(redisKey);
        return redisTemplate.delete(redisKey);
    }

    /**
     * 当前用户
     *
     * @param id      登录用户 id
     * @param request req
     * @return 脱敏后的用户
     */
    @Override
    public UserDto getCurrentUser(String id, HttpServletRequest request) {
        if (StringUtils.isEmpty(id) || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        1. 从缓存中取
        String redisKey = String.format(USER_LOGIN_STATE + id);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        UserDto currentUser = (UserDto) valueOperations.get(redisKey);
//        2. 是否存在
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
//        3. 脱敏
//        UserDto userSafe = new UserDto();
//        BeanUtils.copyProperties(currentUser, userSafe);
//        4. 返回脱敏后数据
        return currentUser;
    }

    /**
     * 更新用户信息
     *
     * @param user      用户信息
     * @param currentId 更新用户信息的用户
     * @param request   req
     * @return 是否成功
     */
    @Override
    public int updateUser(User user, String currentId, HttpServletRequest request) {
        Long userId = user.getId();
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//   1. 校验参数
//      1. userAccount 长度不小于 4 位
        String userAccount = user.getUserAccount();
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求长度小于 4 位");
        }
//      2. gender 不能设置其他值
        Integer gender = user.getGender();
        if (gender == null || (gender != 0 && gender != 1 && gender != 2)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择正确的性别");
        }
//      3. age 不能小于 0 不能超过 200
        Integer age = user.getAge();
        if (age == null || (age <= 0 || age >= 200)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入正常年龄");
        }
//      4. 手机长度是否为 11 位
        String phone = user.getPhone();
        if (StringUtils.isNoneEmpty(phone) && phone.length() != 11) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入正确的手机号码");
        }
//   2. 与旧数据相同就不更新
        String redisKey = String.format(USER_LOGIN_STATE + currentId);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        UserDto currentUser = (UserDto) valueOperations.get(redisKey);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
//   3. 判断是谁更新用户
        if (!isAdmin(currentUser) && !userId.equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
//      1. 如果是管理员可以随意更改
//      2. 如果是用户本身只能更改自己
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        int res = userMapper.updateById(user);
        //        写入缓存
        UserDto userSafe = new UserDto();
        BeanUtils.copyProperties(user, userSafe);
         redisKey = String.format(USER_LOGIN_STATE + userId);
        valueOperations.set(redisKey, userSafe, 30, TimeUnit.MINUTES);
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return res;
    }

    /**
     * 是否为管理员
     *
     * @param request req
     * @return 是否为管理员
     */
    public boolean isAdmin(HttpServletRequest request) {
        UserDto loginUser = (UserDto) request.getSession().getAttribute(USER_LOGIN_STATE);
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 是否为管理员
     *
     * @param loginUser 用户
     * @return 是否为管理员
     */
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }
    public boolean isAdmin(UserDto loginUser) {
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 用户密码更新
     *
     * @param userForgetRequest 用户密码更新
     * @return 是否更新成功
     */
    @Override
    public Boolean forget(UserForgetRequest userForgetRequest) {
        String userAccount = userForgetRequest.getUserAccount();
        String userPassword = userForgetRequest.getUserPassword();
        String userEmail = userForgetRequest.getUserEmail();
        String code = userForgetRequest.getCode();
        if ((!Optional.ofNullable(userEmail).isPresent()) || (!Optional.ofNullable(userAccount).isPresent())
                || (!Optional.ofNullable(userPassword).isPresent()) || !Optional.ofNullable(code).isPresent()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于 8 位，请重新填写");
        }
        String redKey = String.format("my:user:sendMessage:%s", userEmail);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        UserSendMessage sendMessage = (UserSendMessage) valueOperations.get(redKey);
        if (!Optional.ofNullable(sendMessage).isPresent()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码获取失败");
        }
        String sendMessageCode = sendMessage.getCode();
        if (!sendMessageCode.equals(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("email", userEmail);
        User user = userMapper.selectOne(queryWrapper);
        if (!Optional.ofNullable(user).isPresent()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求用户不存在");
        }
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        user.setUserPassword(encryptPassword);
        int res = userMapper.updateById(user);
        return res > 0;

    }


    /**
     * 根据用户标签查询用户
     *
     * @param tags 标签
     * @return 脱敏后的用户集合
     */

    @Override
    public List<UserDto> searchUserByTags(List<String> tags) {

        return null;
    }

}



