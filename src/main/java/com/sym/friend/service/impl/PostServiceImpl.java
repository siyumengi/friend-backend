package com.sym.friend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sym.friend.model.domain.Post;
import com.sym.friend.service.PostService;
import com.sym.friend.mapper.PostMapper;
import org.springframework.stereotype.Service;

/**
* @author siyumeng
* @description 针对表【post(帖子表)】的数据库操作Service实现
* @createDate 2023-06-14 19:17:43
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

}




