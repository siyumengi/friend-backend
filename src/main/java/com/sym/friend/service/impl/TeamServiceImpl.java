package com.sym.friend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sym.friend.common.ErrorCode;
import com.sym.friend.exception.BusinessException;
import com.sym.friend.model.domain.Team;
import com.sym.friend.model.dto.UserDto;
import com.sym.friend.model.enums.TeamStatusEnum;
import com.sym.friend.service.TeamService;
import com.sym.friend.mapper.TeamMapper;
import com.sym.friend.service.UserService;
import com.sym.friend.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

/**
 * @author siyumeng
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2023-06-13 20:58:57
 */
@Slf4j
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {


    @Resource
    private TeamMapper teamMapper;


    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public long addTeam(Team team, UserDto loginUser) {
//        1. 请求参数是否为空
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        2. 是否登录，未登录不允许创建
        if (loginUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final long userId = loginUser.getId();
//        3. 校验信息
//         1. 队伍人数 > 1 且 <= 20
        int maxNum = Optional.ofNullable(team.getMaxnum()).orElse(0);
        if (maxNum < 1 || maxNum > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
//         2. 队伍标题 <= 20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
//         3. 描述 <= 512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
//         4. status 是否公开（int）不传默认为 0（公开）
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
//         5. 如果 status 是加密状态，一定要有密码，且密码 <= 32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
            }
        }
//         6. 超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间 > 当前时间");
        }
//         7. 校验用户最多创建 5 个队伍
//        4. 插入队伍信息到队伍表
//        5. 插入用户  => 队伍关系到关系表
        return 0;
    }
}




