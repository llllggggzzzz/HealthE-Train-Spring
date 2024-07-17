package com.conv.HealthETrain.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName exception_info
 */
@TableName(value ="exception_info")
@Data
public class ExceptionInfo implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer exceptionInfoId;

    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Long examId;

    /**
     * 
     */
    private String addTime;

    /**
     * 
     */
    private String info;

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
        ExceptionInfo other = (ExceptionInfo) that;
        return (this.getExceptionInfoId() == null ? other.getExceptionInfoId() == null : this.getExceptionInfoId().equals(other.getExceptionInfoId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getExamId() == null ? other.getExamId() == null : this.getExamId().equals(other.getExamId()))
            && (this.getAddTime() == null ? other.getAddTime() == null : this.getAddTime().equals(other.getAddTime()))
            && (this.getInfo() == null ? other.getInfo() == null : this.getInfo().equals(other.getInfo()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getExceptionInfoId() == null) ? 0 : getExceptionInfoId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getExamId() == null) ? 0 : getExamId().hashCode());
        result = prime * result + ((getAddTime() == null) ? 0 : getAddTime().hashCode());
        result = prime * result + ((getInfo() == null) ? 0 : getInfo().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", exceptionInfoId=").append(exceptionInfoId);
        sb.append(", userId=").append(userId);
        sb.append(", examId=").append(examId);
        sb.append(", addTime=").append(addTime);
        sb.append(", info=").append(info);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}