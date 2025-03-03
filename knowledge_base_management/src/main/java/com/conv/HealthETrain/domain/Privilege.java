package com.conv.HealthETrain.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName privilege
 */
@TableName(value ="privilege")
@Data
public class Privilege implements Serializable {
    /**
     * 
     */
    @TableId
    private Long privilegeId;

    /**
     * 
     */
    private String privilegeLevel;

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
        Privilege other = (Privilege) that;
        return (this.getPrivilegeId() == null ? other.getPrivilegeId() == null : this.getPrivilegeId().equals(other.getPrivilegeId()))
            && (this.getPrivilegeLevel() == null ? other.getPrivilegeLevel() == null : this.getPrivilegeLevel().equals(other.getPrivilegeLevel()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPrivilegeId() == null) ? 0 : getPrivilegeId().hashCode());
        result = prime * result + ((getPrivilegeLevel() == null) ? 0 : getPrivilegeLevel().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", privilegeId=").append(privilegeId);
        sb.append(", privilegeLevel=").append(privilegeLevel);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}