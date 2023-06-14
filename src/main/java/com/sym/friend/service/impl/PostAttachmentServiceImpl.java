package com.sym.friend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sym.friend.model.domain.PostAttachment;
import com.sym.friend.service.PostAttachmentService;
import com.sym.friend.mapper.PostAttachmentMapper;
import org.springframework.stereotype.Service;

/**
* @author siyumeng
* @description 针对表【post_attachment(帖子附件表)】的数据库操作Service实现
* @createDate 2023-06-14 20:08:35
*/
@Service
public class PostAttachmentServiceImpl extends ServiceImpl<PostAttachmentMapper, PostAttachment>
    implements PostAttachmentService{

}




