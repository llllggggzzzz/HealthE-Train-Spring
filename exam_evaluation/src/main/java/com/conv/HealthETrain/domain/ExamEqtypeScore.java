package com.conv.HealthETrain.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName exam_eqtype_score
 */
@TableName(value ="exam_eqtype_score")
@Data
public class ExamEqtypeScore implements Serializable {
    /**
     * 
     */
    @TableId
    private Long eesId;

    /**
     * 
     */
    private Long examId;

    /**
     * 
     */
    private Long eqTypeId;

    /**
     * 
     */
    private Integer score;

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
        ExamEqtypeScore other = (ExamEqtypeScore) that;
        return (this.getEesId() == null ? other.getEesId() == null : this.getEesId().equals(other.getEesId()))
            && (this.getExamId() == null ? other.getExamId() == null : this.getExamId().equals(other.getExamId()))
            && (this.getEqTypeId() == null ? other.getEqTypeId() == null : this.getEqTypeId().equals(other.getEqTypeId()))
            && (this.getScore() == null ? other.getScore() == null : this.getScore().equals(other.getScore()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getEesId() == null) ? 0 : getEesId().hashCode());
        result = prime * result + ((getExamId() == null) ? 0 : getExamId().hashCode());
        result = prime * result + ((getEqTypeId() == null) ? 0 : getEqTypeId().hashCode());
        result = prime * result + ((getScore() == null) ? 0 : getScore().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", eesId=").append(eesId);
        sb.append(", examId=").append(examId);
        sb.append(", eqTypeId=").append(eqTypeId);
        sb.append(", score=").append(score);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}