package com.conv.HealthETrain.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName eq_type
 */
@TableName(value ="eq_type")
@Data
public class EqType implements Serializable {
    /**
     * 
     */
    @TableId
    private Long eqTypeId;

    /**
     * 
     */
    private String eqTypeName;

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
        EqType other = (EqType) that;
        return (this.getEqTypeId() == null ? other.getEqTypeId() == null : this.getEqTypeId().equals(other.getEqTypeId()))
            && (this.getEqTypeName() == null ? other.getEqTypeName() == null : this.getEqTypeName().equals(other.getEqTypeName()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getEqTypeId() == null) ? 0 : getEqTypeId().hashCode());
        result = prime * result + ((getEqTypeName() == null) ? 0 : getEqTypeName().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", eqTypeId=").append(eqTypeId);
        sb.append(", eqTypeName=").append(eqTypeName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}