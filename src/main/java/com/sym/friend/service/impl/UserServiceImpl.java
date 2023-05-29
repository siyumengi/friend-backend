package com.sym.friend.service.impl;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sym.friend.common.ErrorCode;
import com.sym.friend.exception.BusinessException;
import com.sym.friend.model.domain.User;
import com.sym.friend.model.vo.UserSendMessage;
import com.sym.friend.service.UserService;
import com.sym.friend.mapper.UserMapper;
import com.sym.friend.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;


import javax.annotation.Resource;
import java.util.Dictionary;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        //6. 密码加密
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
        String code =  "";
        if (StringUtils.isNoneEmpty(userEmail)){
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

}



