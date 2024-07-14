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
    private Long qualificationId;

    /**
     * 
     */
    private String qualificationName;

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
            && (this.getQualificationName() == null ? other.getQualificationName() == null : this.getQualificationName().equals(other.getQualificationName()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getQualificationId() == null) ? 0 : getQualificationId().hashCode());
        result = prime * result + ((getQualificationName() == null) ? 0 : getQualificationName().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", qualificationId=").append(qualificationId);
        sb.append(", qualificationName=").append(qualificationName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}