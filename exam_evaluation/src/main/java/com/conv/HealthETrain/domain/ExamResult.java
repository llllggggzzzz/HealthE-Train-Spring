package com.conv.HealthETrain.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import org.aspectj.weaver.ast.Not;

/**
 * 
 * @TableName exam_result
 */
@TableName(value ="exam_result")
@Data
public class ExamResult implements Serializable {
    /**
     * 
     */
    @TableId
    private Long erId;

    /**
     * 
     */
    private Long examId;

    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Long eqId;

    /**
     * 
     */
    private Long eqTypeId;

    /**
     * 
     */
    private String userAnswer;

    /**
     * 
     */
    private String realAnswer;

    /**
     * 
     */
    private Double getScore;

    /**
     * 
     */
    private Integer sumScore;

    @TableField(exist = false)
    private Note note;

    @TableField(exist = false)
    private EqOption eqOption;

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
        ExamResult other = (ExamResult) that;
        return (this.getErId() == null ? other.getErId() == null : this.getErId().equals(other.getErId()))
            && (this.getExamId() == null ? other.getExamId() == null : this.getExamId().equals(other.getExamId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getEqId() == null ? other.getEqId() == null : this.getEqId().equals(other.getEqId()))
            && (this.getEqTypeId() == null ? other.getEqTypeId() == null : this.getEqTypeId().equals(other.getEqTypeId()))
            && (this.getUserAnswer() == null ? other.getUserAnswer() == null : this.getUserAnswer().equals(other.getUserAnswer()))
            && (this.getRealAnswer() == null ? other.getRealAnswer() == null : this.getRealAnswer().equals(other.getRealAnswer()))
            && (this.getGetScore() == null ? other.getGetScore() == null : this.getGetScore().equals(other.getGetScore()))
            && (this.getSumScore() == null ? other.getSumScore() == null : this.getSumScore().equals(other.getSumScore()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getErId() == null) ? 0 : getErId().hashCode());
        result = prime * result + ((getExamId() == null) ? 0 : getExamId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getEqId() == null) ? 0 : getEqId().hashCode());
        result = prime * result + ((getEqTypeId() == null) ? 0 : getEqTypeId().hashCode());
        result = prime * result + ((getUserAnswer() == null) ? 0 : getUserAnswer().hashCode());
        result = prime * result + ((getRealAnswer() == null) ? 0 : getRealAnswer().hashCode());
        result = prime * result + ((getGetScore() == null) ? 0 : getGetScore().hashCode());
        result = prime * result + ((getSumScore() == null) ? 0 : getSumScore().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", erId=").append(erId);
        sb.append(", examId=").append(examId);
        sb.append(", userId=").append(userId);
        sb.append(", eqId=").append(eqId);
        sb.append(", eqTypeId=").append(eqTypeId);
        sb.append(", userAnswer=").append(userAnswer);
        sb.append(", realAnswer=").append(realAnswer);
        sb.append(", getScore=").append(getScore);
        sb.append(", sumScore=").append(sumScore);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}