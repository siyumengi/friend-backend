package com.sym.friend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sym.friend.common.BaseResponse;
import com.sym.friend.common.ErrorCode;
import com.sym.friend.common.ResultUtils;
import com.sym.friend.exception.BusinessException;
import com.sym.friend.mapper.TeamMapper;
import com.sym.friend.model.domain.Team;
import com.sym.friend.model.domain.User;
import com.sym.friend.model.domain.UserTeam;
import com.sym.friend.model.dto.TeamQuery;
import com.sym.friend.model.dto.UserDto;
import com.sym.friend.model.request.*;
import com.sym.friend.model.vo.TeamUserVO;
import com.sym.friend.model.vo.UserVO;
import com.sym.friend.service.TeamService;
import com.sym.friend.service.UserService;
import com.sym.friend.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sym.friend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 队伍接口
 *
 * @author siyumeng
 */
@Slf4j
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:3000/", "http://localhost:3001/"})
public class TeamController {
    @Resource
    private UserService userService;

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    @Qualifier("TeamServiceImpl")
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;

    /**
     * 添加队伍
     *
     * @param teamAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String currentId = teamAddRequest.getCurrentId();
        String redisKey = String.format(USER_LOGIN_STATE + currentId);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        UserDto loginUser = (UserDto) valueOperations.get(redisKey);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    /**
     * 更新队伍
     *
     * @param teamUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        log.info("team:" + teamUpdateRequest);
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String currentId = teamUpdateRequest.getCurrentId();
        log.info("id:" + currentId);
        String redisKey = String.format(USER_LOGIN_STATE + currentId);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        UserDto loginUser = (UserDto) valueOperations.get(redisKey);
        log.info("loginUser:" + loginUser);
//        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
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
    public BaseResponse<Team> getTeamById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    /**
     * 条件查询
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        // 1、查询队伍列表
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, !isAdmin);
        final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        // 2、判断当前用户是否已加入队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try {
            UserDto loginUser = userService.getLoginUser(request);
            userTeamQueryWrapper.eq("userId", loginUser.getId());
            userTeamQueryWrapper.in("teamId", teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            // 已加入的队伍 id 集合
            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            teamList.forEach(team -> {
                boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        } catch (Exception e) {
        }
        // 3、查询已加入队伍的人数
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("teamId", teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamJoinQueryWrapper);
        // 队伍 id => 加入这个队伍的用户列表
        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(team -> team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size()));
        return ResultUtils.success(teamList);
    }

    /**
     * 分页查询
     *
     * @param teamQuery
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        return ResultUtils.success(resultPage);
    }

    /**
     * 加入的队伍
     *
     * @param teamJoinRequest
     * @param request
     * @return
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String currentId = teamJoinRequest.getCurrentId();
        log.info("id:" + currentId);
        String redisKey = String.format(USER_LOGIN_STATE + currentId);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        UserDto loginUser = (UserDto) valueOperations.get(redisKey);
//        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 退出队伍
     *
     * @param teamQuitRequest
     * @param request
     * @return
     */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String currentId = teamQuitRequest.getCurrentId();
        log.info("id:" + currentId);
        String redisKey = String.format(USER_LOGIN_STATE + currentId);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        UserDto loginUser = (UserDto) valueOperations.get(redisKey);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 解散队伍
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        log.info("deleteRequest:" + deleteRequest);
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        UserDto loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(id, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }


    /**
     * 获取我创建的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String currentId = teamQuery.getCurrentId();
        log.info("id:" + teamQuery.getCurrentId());
        String redisKey = String.format(USER_LOGIN_STATE + currentId);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        UserDto loginUser = (UserDto) valueOperations.get(redisKey);
//        User loginUser = userService.getLoginUser(request);
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("userId", loginUser.getId());
        String searchText = teamQuery.getSearchText();
        log.info("searchText" + searchText);
//            如果描述或者标题中包含相关内容，模糊查询
        if (StringUtils.isNotBlank(searchText)) {
            teamQueryWrapper.and(qw -> qw.like("description", searchText).or().like("name", searchText));
        }
        List<Team> list = teamService.list(teamQueryWrapper);
        if (list == null) {
            return ResultUtils.success(null);
        }
        List<TeamUserVO> teamList = new ArrayList<>();
//        1、通过队伍查询结果遍历获取每个队伍id
        for (Team team : list) {
            Long teamId = team.getId();
//          2、通过队伍查询创建人信息（塞入UserTeamVo）中
            Long CreateUserId = team.getUserId();
            UserVO CreateUserVO = new UserVO();
            User CreateUserById = userService.getById(CreateUserId);
            TeamUserVO teamUserVO = new TeamUserVO();
//            将队伍信息拷贝到teamUserVO中
            BeanUtils.copyProperties(team, teamUserVO);
//            将创建人用户信息脱敏
            BeanUtils.copyProperties(CreateUserById, CreateUserVO);
            teamUserVO.setCreateUser(CreateUserVO);
//          3、通过队伍id在队伍用户表中查询出用户关系集合
            List<User> users = teamMapper.SelectUsers(teamId);
            List<UserVO> Users = new ArrayList<>();
            for (User user : users) {
//          4、遍历集合获取用户id得到用户
//                如果用户存在
                if (user != null) {
                    UserVO userVO = new UserVO();
//          5、用户脱敏
                    BeanUtils.copyProperties(user, userVO);
                    Users.add(userVO);
                }
            }
            teamUserVO.setUserVOS(Users);
            teamList.add(teamUserVO);
        }
        return ResultUtils.success(teamList);
    }

    /**
     * 获取我加入的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String currentId = teamQuery.getCurrentId();
        log.info("id:" + currentId);
        String redisKey = String.format(USER_LOGIN_STATE + currentId);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        UserDto loginUser = (UserDto) valueOperations.get(redisKey);
//        User loginUser = userService.getLoginUser(request);
        log.info("loginUser:" + loginUser.toString());
        log.info(teamQuery.getSearchText());
        List<TeamUserVO> myJoinTeam = teamService.getMyJoinTeam(loginUser, teamQuery);
        return ResultUtils.success(myJoinTeam);
    }
}
