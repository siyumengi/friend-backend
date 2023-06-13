package com.sym.friend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sym.friend.model.domain.UserTeam;
import com.sym.friend.service.UserTeamService;
import com.sym.friend.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author siyumeng
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-06-13 20:58:57
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




