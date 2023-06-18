package com.sym.friend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sym.friend.common.ErrorCode;
import com.sym.friend.exception.BusinessException;
import com.sym.friend.model.domain.Post;
import com.sym.friend.model.domain.PostImage;
import com.sym.friend.model.domain.UserPost;
import com.sym.friend.model.dto.PostQuery;
import com.sym.friend.model.dto.UserDto;
import com.sym.friend.model.request.PostUpdateRequest;
import com.sym.friend.model.vo.PostVo;
import com.sym.friend.service.PostImageService;
import com.sym.friend.service.PostService;
import com.sym.friend.mapper.PostMapper;
import com.sym.friend.service.UserPostService;
import com.sym.friend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author siyumeng
* @description 针对表【post(帖子表)】的数据库操作Service实现
* @createDate 2023-06-14 20:08:35
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

    @Resource
    private UserPostService userPostService;
    @Resource
    private UserService userService;
    @Resource
    private PostImageService postImageService;
    @Override
    public long addPost(Post post, UserDto loginUser, String imageUrl) {
        //        1. 请求参数是否为空
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        2. 是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final long userId = loginUser.getId();
//        3. 校验信息
//         1. 标题 不能为空
        String title = post.getTitle();
        if (StringUtils.isBlank(title)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入标题");
        }
//         2. 内容 不能为空
        String content = post.getContent();
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入内容");
        }
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();


//        4. 插入信息到帖子表
        post.setPostId(null);
        post.setAuthorId(userId);
        post.setIsTop(0);
        boolean result = this.save(post);
        Long teamId = post.getPostId();
        if (!result || teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "发表帖子失败");
        }
//        5. 插入图片  => 帖子关系到关系表
        PostImage postImage = new PostImage();
        if (StringUtils.isNotBlank(imageUrl)) {
            postImage.setPostId(post.getPostId());
            postImage.setUrl(imageUrl);
            result = postImageService.save(postImage);
        }
        post.setPostImgId(postImage.getId());
        result = this.updateById(post);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "发表帖子失败");
        }
        return teamId;
    }

    @Override
    public boolean updateTeam(PostUpdateRequest postUpdateRequest, UserDto loginUser) {
//        1. 请求参数校验
        if (postUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        2. 是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final long userId = loginUser.getId();
//        3. 校验信息
//         1. 标题 不能为空
        String title = postUpdateRequest.getTitle();
        if (StringUtils.isBlank(title)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入标题");
        }
//         2. 内容 不能为空
        String content = postUpdateRequest.getContent();
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入内容");
        }
//        4. 校验帖子是否存在
        Long postId = postUpdateRequest.getPostId();
        Post post = this.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子不存在");
        }
//        5. 校验帖子是否属于当前用户
        if (!post.getAuthorId().equals(userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子不属于当前用户");
        }
//        6. 更新帖子信息
        post.setTitle(title);
        post.setContent(content);
        post.setUpdateTime(new Date());
//        7. 执行更新
        boolean res = this.updateById(post);
//        8.更新图片
        PostImage postImage = new PostImage();
        postImage.setPostId(postId);
        postImage.setUrl(postUpdateRequest.getImageUrl());
        postImage.setId(post.getPostImgId());
        res = postImageService.updateById(postImage);
//        9. 是否更新成功
        if (!res) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新帖子失败");
        }
        return res;
    }

    @Override
    public List<PostVo> listPost(PostQuery postQuery, boolean isAdmin) {
//        1. 请求参数校验
        if (postQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        2. 查询符合要求的帖子
//         1.获取所有参数
        String title = postQuery.getTitle();
        String content = postQuery.getContent();
        Long authorId = postQuery.getAuthorId();
//        3.判断参数是否为空，不为空就条件拼接
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(title)) {
            queryWrapper.like("title", title);
        }
        if (StringUtils.isNotBlank(content)) {
            queryWrapper.like("content", content);
        }
        if (authorId != null) {
            queryWrapper.eq("author_id", authorId);
        }
//        4. 是否是管理员，如果不是管理员，只能查询已经发布的帖子
        return getPostVos(queryWrapper);

    }

    @Override
    public boolean deleteByPostId(long id, UserDto loginUser) {
//        1. 请求参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        2. 是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final long userId = loginUser.getId();
//        3. 校验帖子是否存在
        Post post = this.getById(id);
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子不存在");
        }
//        4. 校验帖子是否属于当前用户和是否为管理员
        if (!post.getAuthorId().equals(userId) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子不属于当前用户");
        }
//        5. 删除帖子
        boolean res = this.removeById(id);
//        6. 是否删除成功
        if (!res) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除帖子失败");
        }
//        7. 删除帖子关系
//        QueryWrapper<UserPost> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("postId", id);
//        queryWrapper.eq("userId", userId);
//        res = userPostService.remove(queryWrapper);
        return res;
    }

    @Override
    public List<PostVo> listMyCreateTeams(PostQuery postQuery, UserDto loginUser) {
//        1. 请求参数校验
        if (postQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        2. 是否登录，未登录不允许查询
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final long userId = loginUser.getId();
//        3. 查询自己的所有帖子
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("authorId", userId);
        return getPostVos(queryWrapper);
    }

    private List<PostVo> getPostVos(QueryWrapper<Post> queryWrapper) {
        List<Post> list = this.list(queryWrapper);
        List<PostVo> postVoList = new ArrayList<>();
        for (Post post : list) {
            Long postImgId = post.getPostImgId();
            PostImage postImage = postImageService.getById(postImgId);
            PostVo postVo = new PostVo();
            BeanUtils.copyProperties(post, postVo);
            if (postImage != null) {
                postVo.setImageUrl(postImage.getUrl());
            }else {
                postVo.setImageUrl("");
            }
            postVoList.add(postVo);
        }
        return postVoList;
    }
}




