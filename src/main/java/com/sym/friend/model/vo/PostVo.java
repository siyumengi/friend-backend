package com.sym.friend.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.Date;

@Data
public class PostVo implements Serializable {
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
}
