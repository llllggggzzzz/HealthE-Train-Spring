package com.conv.HealthETrain.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @TableName eq_bank_link_eq
 */
@TableName(value ="eq_bank_link_eq")
@Data
public class EQBankLinkEQ implements Serializable {
    /**
     *
     */
    private Long eqbleqId;

    /**
     *
     */
    private Long eqbId;

    /**
     *
     */
    private Long eqId;

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
        EQBankLinkEQ other = (EQBankLinkEQ) that;
        return (this.getEqbleqId() == null ? other.getEqbleqId() == null : this.getEqbleqId().equals(other.getEqbleqId()))
            && (this.getEqbId() == null ? other.getEqbId() == null : this.getEqbId().equals(other.getEqbId()))
            && (this.getEqId() == null ? other.getEqId() == null : this.getEqId().equals(other.getEqId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getEqbleqId() == null) ? 0 : getEqbleqId().hashCode());
        result = prime * result + ((getEqbId() == null) ? 0 : getEqbId().hashCode());
        result = prime * result + ((getEqId() == null) ? 0 : getEqId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", eqbleqId=").append(eqbleqId);
        sb.append(", eqbId=").append(eqbId);
        sb.append(", eqId=").append(eqId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
