package com.conv.HealthETrain.domain.POJP;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author liusg
 */
@Data
@TableName(value ="lesson_detail")
@AllArgsConstructor
@NoArgsConstructor
public class LessonDetail implements Serializable {
    @TableId
    private Long ldId;
    private Long lessonId;
    private String lessonOverview;
    private String lessonObject;
    private String preliminaryKnowledge;
    private String lessonMaterial;

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
        LessonDetail other = (LessonDetail) that;

        return (this.getLdId() == null ? other.getLdId() == null : this.getLdId().equals(other.getLdId()))
            && (this.getLessonId() == null ? other.getLessonId() == null : this.getLessonId().equals(other.getLessonId()))
            && (this.getLessonOverview() == null ? other.getLessonOverview() == null : this.getLessonOverview().equals(other.getLessonOverview()))
            && (this.getLessonObject() == null ? other.getLessonObject() == null : this.getLessonObject().equals(other.getLessonObject()))
            && (this.getPreliminaryKnowledge() == null ? other.getPreliminaryKnowledge() == null : this.getPreliminaryKnowledge().equals(other.getPreliminaryKnowledge()))
            && (this.getLessonMaterial() == null ? other.getLessonMaterial() == null : this.getLessonMaterial().equals(other.getLessonMaterial()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getLdId() == null) ? 0 : getLdId().hashCode());
        result = prime * result + ((getLessonId() == null) ? 0 : getLessonId().hashCode());
        result = prime * result + ((getLessonOverview() == null) ? 0 : getLessonOverview().hashCode());
        result = prime * result + ((getLessonObject() == null) ? 0 : getLessonObject().hashCode());
        result = prime * result + ((getPreliminaryKnowledge() == null) ? 0 : getPreliminaryKnowledge().hashCode());
        result = prime * result + ((getLessonMaterial() == null) ? 0 : getLessonMaterial().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("ldId=").append(ldId);
        sb.append(", lessonId=").append(lessonId);
        sb.append(", lessonOverview=").append(lessonOverview);
        sb.append(", lessonObject=").append(lessonObject);
        sb.append(", preliminaryKnowledge=").append(preliminaryKnowledge);
        sb.append(", lessonMaterial=").append(lessonMaterial);
        sb.append("]");

        return sb.toString();
    }
}
