package com.sym.friend.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 帖子附件表
 * @TableName post_attachment
 */
@TableName(value ="post_attachment")
@Data
public class PostAttachment implements Serializable {
    /**
     * 附件 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 帖子 ID
     */
    private Long postId;

    /**
     * 附件 URL
     */
    private String url;

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