package com.conv.HealthETrain.domain.POJP;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName lesson
 */
@TableName(value ="lesson")
@Data
public class Lesson implements Serializable {
    /**
     * 
     */
    @TableId
    private Long lessonId;

    /**
     * 
     */
    private String lessonName;

    /**
     * 
     */
    private Integer lessonType;

    /**
     * 
     */
    private Date startTime;

    /**
     * 
     */
    private Date endTime;

    /**
     * 
     */
    private String lessonCover;

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
        Lesson other = (Lesson) that;
        return (this.getLessonId() == null ? other.getLessonId() == null : this.getLessonId().equals(other.getLessonId()))
            && (this.getLessonName() == null ? other.getLessonName() == null : this.getLessonName().equals(other.getLessonName()))
            && (this.getLessonType() == null ? other.getLessonType() == null : this.getLessonType().equals(other.getLessonType()))
            && (this.getStartTime() == null ? other.getStartTime() == null : this.getStartTime().equals(other.getStartTime()))
            && (this.getEndTime() == null ? other.getEndTime() == null : this.getEndTime().equals(other.getEndTime()))
            && (this.getLessonCover() == null ? other.getLessonCover() == null : this.getLessonCover().equals(other.getLessonCover()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getLessonId() == null) ? 0 : getLessonId().hashCode());
        result = prime * result + ((getLessonName() == null) ? 0 : getLessonName().hashCode());
        result = prime * result + ((getLessonType() == null) ? 0 : getLessonType().hashCode());
        result = prime * result + ((getStartTime() == null) ? 0 : getStartTime().hashCode());
        result = prime * result + ((getEndTime() == null) ? 0 : getEndTime().hashCode());
        result = prime * result + ((getLessonCover() == null) ? 0 : getLessonCover().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", lessonId=").append(lessonId);
        sb.append(", lessonName=").append(lessonName);
        sb.append(", lessonType=").append(lessonType);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", lessonCover=").append(lessonCover);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}