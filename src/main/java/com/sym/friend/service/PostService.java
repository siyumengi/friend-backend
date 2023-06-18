package com.sym.friend.service;

import com.sym.friend.model.domain.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sym.friend.model.dto.PostQuery;
import com.sym.friend.model.dto.UserDto;
import com.sym.friend.model.request.PostUpdateRequest;
import com.sym.friend.model.vo.PostVo;

import java.util.List;

/**
* @author siyumeng
* @description 针对表【post(帖子表)】的数据库操作Service
* @createDate 2023-06-14 20:08:35
*/
public interface PostService extends IService<Post> {

    /**
     * 添加帖子
     *
     * @param post      帖子信息
     * @param loginUser 登录用户
     * @param imageUrl
     * @return 是否添加成功
     */
    long addPost(Post post, UserDto loginUser, String imageUrl);

    /**
     * 更新帖子信息
     * @param postUpdateRequest 帖子更新请求
     * @param loginUser        登录用户
     * @return      是否更新成功
     */
    boolean updateTeam(PostUpdateRequest postUpdateRequest, UserDto loginUser);

    List<PostVo> listPost(PostQuery postQuery, boolean isAdmin);

    boolean deleteByPostId(long id, UserDto loginUser);

    List<PostVo> listMyCreateTeams(PostQuery postQuery, UserDto loginUser);
}
