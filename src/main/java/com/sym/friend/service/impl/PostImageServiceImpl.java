package com.sym.friend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sym.friend.model.domain.PostImage;
import com.sym.friend.service.PostImageService;
import com.sym.friend.mapper.PostImageMapper;
import org.springframework.stereotype.Service;

/**
* @author siyumeng
* @description 针对表【post_image(帖子图片表)】的数据库操作Service实现
* @createDate 2023-06-14 20:08:35
*/
@Service
public class PostImageServiceImpl extends ServiceImpl<PostImageMapper, PostImage>
    implements PostImageService{

}




