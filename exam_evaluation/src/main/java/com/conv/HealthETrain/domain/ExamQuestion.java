package com.conv.HealthETrain.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName exam_question
 */
@TableName(value ="exam_question")
@Data
public class ExamQuestion implements Serializable {
    /**
     * 
     */
    @TableId
    private Long eqId;
    /**
     * 
     */
    private Long eqTypeId;
    /**
     * 
     */
    private Long noteId;

    /**
     * 
     */
    private String answer;

    /**
     * 
     */
    private Long qbId;

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
        ExamQuestion other = (ExamQuestion) that;
        return (this.getEqId() == null ? other.getEqId() == null : this.getEqId().equals(other.getEqId()))
            && (this.getEqTypeId() == null ? other.getEqTypeId() == null : this.getEqTypeId().equals(other.getEqTypeId()))
            && (this.getNoteId() == null ? other.getNoteId() == null : this.getNoteId().equals(other.getNoteId()))
            && (this.getAnswer() == null ? other.getAnswer() == null : this.getAnswer().equals(other.getAnswer()))
            && (this.getQbId() == null ? other.getQbId() == null : this.getQbId().equals(other.getQbId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getEqId() == null) ? 0 : getEqId().hashCode());
        result = prime * result + ((getEqTypeId() == null) ? 0 : getEqTypeId().hashCode());
        result = prime * result + ((getNoteId() == null) ? 0 : getNoteId().hashCode());
        result = prime * result + ((getAnswer() == null) ? 0 : getAnswer().hashCode());
        result = prime * result + ((getQbId() == null) ? 0 : getQbId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", eqId=").append(eqId);
        sb.append(", eqTypeId=").append(eqTypeId);
        sb.append(", noteId=").append(noteId);
        sb.append(", answer=").append(answer);
        sb.append(", qbId=").append(qbId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}