package com.sym.friend.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 */
@Data
public class PostUpdateRequest implements Serializable {
    /**
     * 帖子 ID
     */
    @TableId(type = IdType.AUTO)
    private Long postId;
    /**
     * 当前用户 ID
     */
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
     * 审核状态：0-待审核；1-已通过；2-未通过
     */
    private Integer postStatus;

    /**
     * 是否置顶：0-否；1-是
     */
    private Integer isTop;







    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
