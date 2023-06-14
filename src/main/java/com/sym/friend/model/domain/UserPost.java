package com.sym.friend.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 帖子-用户中间表
 * @TableName user_post
 */
@TableName(value ="user_post")
@Data
public class UserPost implements Serializable {
    /**
     * 
     */
    @TableId
    private Long postId;

    /**
     * 
     */
    @TableId
    private Long userId;

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
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}