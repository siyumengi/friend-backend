package com.sym.friend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sym.friend.common.BaseResponse;
import com.sym.friend.common.ErrorCode;
import com.sym.friend.common.ResultUtils;
import com.sym.friend.exception.BusinessException;
import com.sym.friend.model.domain.Post;
import com.sym.friend.model.domain.Team;
import com.sym.friend.model.domain.User;
import com.sym.friend.model.domain.UserTeam;
import com.sym.friend.model.dto.PostQuery;
import com.sym.friend.model.dto.TeamQuery;
import com.sym.friend.model.dto.UserDto;
import com.sym.friend.model.request.PostAddRequest;
import com.sym.friend.model.request.PostUpdateRequest;
import com.sym.friend.model.vo.PostVo;
import com.sym.friend.model.vo.TeamUserVO;
import com.sym.friend.model.vo.UserVO;
import com.sym.friend.service.PostService;
import com.sym.friend.service.UserPostService;
import com.sym.friend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sym.friend.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/post")
@CrossOrigin(origins = {"http://localhost:3000/", "http://localhost:3001/"})
@Slf4j
public class PostController {
    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private PostService postService;
    @Resource
    private UserPostService userPostService;

    /**
     * 添加
     *
     * @param postAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest, HttpServletRequest request) {
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String currentId = postAddRequest.getCurrentId();
        String redisKey = String.format(USER_LOGIN_STATE + currentId);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        UserDto loginUser = (UserDto) valueOperations.get(redisKey);
        Post post = new Post();
        BeanUtils.copyProperties(postAddRequest, post);
        long teamId = postService.addPost(post, loginUser);
        return ResultUtils.success(teamId);
    }


    /**
     * 更新队伍
     *
     * @param postUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updatePost(@RequestBody PostUpdateRequest postUpdateRequest, HttpServletRequest request) {
        log.info("team:" + postUpdateRequest);
        if (postUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String currentId = postUpdateRequest.getCurrentId();
        log.info("id:" + currentId);
        String redisKey = String.format(USER_LOGIN_STATE + currentId);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        UserDto loginUser = (UserDto) valueOperations.get(redisKey);
        log.info("loginUser:" + loginUser);
        boolean result = postService.updateTeam(postUpdateRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 根据用户 id 查询
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Post> getPostById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post team = postService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    /**
     * 条件查询
     *
     * @param postQuery
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<Post>> listPost(PostQuery postQuery, HttpServletRequest request) {
        if (postQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        List<Post> postList = postService.listPost(postQuery, isAdmin);
        return ResultUtils.success(postList);
    }

    /**
     * 分页查询
     *
     * @param postQuery
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Post>> listPostPage(PostQuery postQuery) {
        if (postQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postQuery, post);
        Page<Post> page = new Page<>(postQuery.getPageNum(), postQuery.getPageSize());
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>(post);
        Page<Post> postPage = postService.page(page, queryWrapper);
        return ResultUtils.success(postPage);
    }

    /**
     * 删除
     * @param id     帖子id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePost(@RequestBody long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//    获取当前用户
        UserDto loginUser = userService.getLoginUser(request);
        boolean res = postService.deleteByPostId(id, loginUser);
        if (!res) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 获取我发布的帖子
     *
     * @param postQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<PostVo>> listMyCreatePosts(PostQuery postQuery, HttpServletRequest request) {
        if (postQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserDto loginUser = userService.getLoginUser(request);
        List<Post> postList = postService.listMyCreateTeams(postQuery, loginUser);
        List<PostVo> postVoList = new ArrayList<>();
//      用 stream 流脱敏处理
        postList.forEach(post -> {
            PostVo postVo = new PostVo();
            BeanUtils.copyProperties(post, postVo);
            postVoList.add(postVo);
        });
        return ResultUtils.success(postVoList);
    }

}
