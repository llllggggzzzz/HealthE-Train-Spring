package com.conv.HealthETrain.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @author liusg
 * @TableName exam_question_bank
 */
@TableName(value ="question_bank")
@Data
public class QuestionBank implements Serializable {
    /**
     * 
     */
    @TableId
    private Long qbId;

    /**
     * 
     */
    private String qbTitle;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Long lessonId;

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
        QuestionBank other = (QuestionBank) that;
        return (this.getQbId() == null ? other.getQbId() == null : this.getQbId().equals(other.getQbId()))
            && (this.getQbTitle() == null ? other.getQbTitle() == null : this.getQbTitle().equals(other.getQbTitle()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getLessonId() == null ? other.getLessonId() == null : this.getLessonId().equals(other.getLessonId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getQbId() == null) ? 0 : getQbId().hashCode());
        result = prime * result + ((getQbTitle() == null) ? 0 : getQbTitle().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getLessonId() == null) ? 0 : getLessonId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", qbId=").append(qbId);
        sb.append(", qbTitle=").append(qbTitle);
        sb.append(", createTime=").append(createTime);
        sb.append(", lessonId=").append(lessonId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}