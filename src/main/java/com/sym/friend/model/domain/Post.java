package com.sym.friend.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 帖子表
 * @TableName post
 */
@TableName(value ="post")
@Data
public class Post implements Serializable {
    /**
     * 帖子 ID
     */
    @TableId(type = IdType.AUTO)
    private Long postId;

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
     * 浏览次数
     */
    private Long viewCount;

    /**
     * 回复次数
     */
    private Long replyCount;

    /**
     * 点赞次数
     */
    private Long likeCount;

    /**
     * 最近回复时间
     */
    private Date latestReplyTime;

    /**
     * 话题 ID
     */
    private Long topicId;

    /**
     * 图片
     */
    private Long postImgId;



    /**
     * 审核状态：0-待审核；1-已通过；2-未通过
     */
    private Integer postStatus;

    /**
     * 是否置顶：0-否；1-是
     */
    private Integer isTop;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}