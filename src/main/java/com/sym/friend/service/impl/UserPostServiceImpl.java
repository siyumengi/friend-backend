package com.sym.friend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sym.friend.model.domain.UserPost;
import com.sym.friend.service.UserPostService;
import com.sym.friend.mapper.UserPostMapper;
import org.springframework.stereotype.Service;

/**
* @author siyumeng
* @description 针对表【user_post(帖子-用户中间表)】的数据库操作Service实现
* @createDate 2023-06-14 19:17:43
*/
@Service
public class UserPostServiceImpl extends ServiceImpl<UserPostMapper, UserPost>
    implements UserPostService{

}




