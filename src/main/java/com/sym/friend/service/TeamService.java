package com.sym.friend.service;

import com.sym.friend.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sym.friend.model.domain.User;
import com.sym.friend.model.dto.TeamQuery;
import com.sym.friend.model.dto.UserDto;
import com.sym.friend.model.request.TeamJoinRequest;
import com.sym.friend.model.request.TeamQuitRequest;
import com.sym.friend.model.request.TeamUpdateRequest;
import com.sym.friend.model.vo.TeamUserVO;

import java.util.List;

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

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, UserDto loginUser);

    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    boolean joinTeam(TeamJoinRequest teamJoinRequest, UserDto loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, UserDto    loginUser);



    /**
     * 删除（解散）队伍
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id, UserDto loginUser);

    List<TeamUserVO> getMyJoinTeam(UserDto loginUser,TeamQuery teamQuery);
}
