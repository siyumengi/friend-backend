package com.sym.friend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sym.friend.model.domain.User;
import com.sym.friend.service.UserService;
import com.sym.friend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author siyumeng
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2023-05-29 11:38:40
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




