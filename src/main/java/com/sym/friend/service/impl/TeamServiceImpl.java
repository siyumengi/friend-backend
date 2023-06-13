package com.sym.friend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sym.friend.model.domain.Team;
import com.sym.friend.service.TeamService;
import com.sym.friend.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author siyumeng
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2023-06-13 20:58:57
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




