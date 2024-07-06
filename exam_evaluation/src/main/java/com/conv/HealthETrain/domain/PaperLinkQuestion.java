package com.conv.HealthETrain.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName paper_link_question
 */
@TableName(value ="paper_link_question")
@Data
public class PaperLinkQuestion implements Serializable {
    /**
     * 
     */
    @TableId
    private Long plqId;

    /**
     * 
     */
    private Long paperId;

    /**
     * 
     */
    private Long examQuestionId;

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
        PaperLinkQuestion other = (PaperLinkQuestion) that;
        return (this.getPlqId() == null ? other.getPlqId() == null : this.getPlqId().equals(other.getPlqId()))
            && (this.getPaperId() == null ? other.getPaperId() == null : this.getPaperId().equals(other.getPaperId()))
            && (this.getExamQuestionId() == null ? other.getExamQuestionId() == null : this.getExamQuestionId().equals(other.getExamQuestionId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPlqId() == null) ? 0 : getPlqId().hashCode());
        result = prime * result + ((getPaperId() == null) ? 0 : getPaperId().hashCode());
        result = prime * result + ((getExamQuestionId() == null) ? 0 : getExamQuestionId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", plqId=").append(plqId);
        sb.append(", paperId=").append(paperId);
        sb.append(", examQuestionId=").append(examQuestionId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}