package com.sym.friend.service;

import com.sym.friend.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sym.friend.model.dto.UserDto;

/**
* @author siyumeng
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-06-13 20:58:57
*/
public interface TeamService extends IService<Team> {


    /**
     * 创建队伍
     *
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, UserDto loginUser);
}
