package com.conv.HealthETrain.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName qualification
 */
@TableName(value ="qualification")
@Data
public class Qualification implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Integer qualificationId;

    /**
     * 
     */
    private String qualicationName;

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
        Qualification other = (Qualification) that;
        return (this.getQualificationId() == null ? other.getQualificationId() == null : this.getQualificationId().equals(other.getQualificationId()))
            && (this.getQualicationName() == null ? other.getQualicationName() == null : this.getQualicationName().equals(other.getQualicationName()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getQualificationId() == null) ? 0 : getQualificationId().hashCode());
        result = prime * result + ((getQualicationName() == null) ? 0 : getQualicationName().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", qualificationId=").append(qualificationId);
        sb.append(", qualicationName=").append(qualicationName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}