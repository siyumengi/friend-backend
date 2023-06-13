package com.sym.friend.controller;

import com.sym.friend.common.BaseResponse;
import com.sym.friend.common.ErrorCode;
import com.sym.friend.exception.BusinessException;
import com.sym.friend.mapper.TeamMapper;
import com.sym.friend.model.domain.Team;
import com.sym.friend.model.dto.UserDto;
import com.sym.friend.model.request.TeamAddRequest;
import com.sym.friend.service.TeamService;
import com.sym.friend.service.UserService;
import com.sym.friend.service.UserTeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.sym.friend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 队伍接口
 * @author siyumeng 
 */
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
    
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest , HttpServletRequest request){
        if (teamAddRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String currentId = teamAddRequest.getCurrentId();
        String redisKey = String.format(USER_LOGIN_STATE+currentId);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        UserDto loginUser = (UserDto) valueOperations.get(redisKey);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest , team);
        teamService.addTeam(team , loginUser);
    }
    
    
}
