package com.sym.friend.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.sym.friend.model.request.PageRequest;
import lombok.Data;

import java.util.Date;

@Data
public class PostQuery extends PageRequest {

    private String currentId;


    /**
     * 帖子标题
     */
    private String title;

    /**
     * 帖子内容
     */

    private String content;

    /**
     * 帖子发布时间
     */
    private Date postTime;

    /**
     * 作者 ID
     */
    private Long authorId;
    /**
     * 搜索关键词（同时对队伍名称和描述搜索）
     */
    private String searchText;

}
