package com.conv.HealthETrain.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName eq_option
 */
@TableName(value ="eq_option")
@Data
public class EqOption implements Serializable {
    /**
     * 
     */
    @TableId
    private Long eqOptionId;

    /**
     * 
     */
    private Long eqId;

    /**
     * 
     */
    private String eqA;

    /**
     * 
     */
    private String eqB;

    /**
     * 
     */
    private String eqC;

    /**
     * 
     */
    private String eqD;

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
        EqOption other = (EqOption) that;
        return (this.getEqOptionId() == null ? other.getEqOptionId() == null : this.getEqOptionId().equals(other.getEqOptionId()))
            && (this.getEqId() == null ? other.getEqId() == null : this.getEqId().equals(other.getEqId()))
            && (this.getEqA() == null ? other.getEqA() == null : this.getEqA().equals(other.getEqA()))
            && (this.getEqB() == null ? other.getEqB() == null : this.getEqB().equals(other.getEqB()))
            && (this.getEqC() == null ? other.getEqC() == null : this.getEqC().equals(other.getEqC()))
            && (this.getEqD() == null ? other.getEqD() == null : this.getEqD().equals(other.getEqD()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getEqOptionId() == null) ? 0 : getEqOptionId().hashCode());
        result = prime * result + ((getEqId() == null) ? 0 : getEqId().hashCode());
        result = prime * result + ((getEqA() == null) ? 0 : getEqA().hashCode());
        result = prime * result + ((getEqB() == null) ? 0 : getEqB().hashCode());
        result = prime * result + ((getEqC() == null) ? 0 : getEqC().hashCode());
        result = prime * result + ((getEqD() == null) ? 0 : getEqD().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", eqOptionId=").append(eqOptionId);
        sb.append(", eqId=").append(eqId);
        sb.append(", eqA=").append(eqA);
        sb.append(", eqB=").append(eqB);
        sb.append(", eqC=").append(eqC);
        sb.append(", eqD=").append(eqD);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}