package com.conv.HealthETrain.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName chapter
 */
@TableName(value ="chapter")
@Data
public class Chapter implements Serializable {
    /**
     * 
     */
    @TableId
    private Long chapterId;

    /**
     * 
     */
    private Long lessonId;

    /**
     * 
     */
    private Integer chapterOrder;

    /**
     * 
     */
    private String chapterTitle;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Chapter other = (Chapter) that;
        return (this.getChapterId() == null ? other.getChapterId() == null : this.getChapterId().equals(other.getChapterId()))
            && (this.getLessonId() == null ? other.getLessonId() == null : this.getLessonId().equals(other.getLessonId()))
            && (this.getChapterOrder() == null ? other.getChapterOrder() == null : this.getChapterOrder().equals(other.getChapterOrder()))
            && (this.getChapterTitle() == null ? other.getChapterTitle() == null : this.getChapterTitle().equals(other.getChapterTitle()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getChapterId() == null) ? 0 : getChapterId().hashCode());
        result = prime * result + ((getLessonId() == null) ? 0 : getLessonId().hashCode());
        result = prime * result + ((getChapterOrder() == null) ? 0 : getChapterOrder().hashCode());
        result = prime * result + ((getChapterTitle() == null) ? 0 : getChapterTitle().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", chapterId=").append(chapterId);
        sb.append(", lessonId=").append(lessonId);
        sb.append(", chapterOrder=").append(chapterOrder);
        sb.append(", chapterTitle=").append(chapterTitle);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}