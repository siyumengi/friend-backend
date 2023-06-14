package com.sym.friend.mapper;

import com.sym.friend.model.domain.Team;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sym.friend.model.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author siyumeng
* @description 针对表【team(队伍)】的数据库操作Mapper
* @createDate 2023-06-13 20:58:57
* @Entity com.sym.friend.model.domain.Team
*/
public interface TeamMapper extends BaseMapper<Team> {

    List<User> SelectUsers(@Param("teamId") long teamId);
}




