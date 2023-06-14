package com.sym.friend.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PostAddRequest implements Serializable {

    private String currentId;
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
     * 图片
     */
    private String imageUrl;

    /**
     * 创建时间
     */
    private Date createTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
