package com.conv.HealthETrain.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName courseware
 */
@TableName(value ="courseware")
@Data
public class Courseware implements Serializable {
    /**
     * 
     */
    @TableId
    private Long coursewareId;

    /**
     * 
     */
    private Date uploadTime;

    /**
     * 
     */
    private Long size;

    /**
     * 
     */
    private String path;

    /**
     * 
     */
    private String coursewareName;

    /**
     * 
     */
    private String suffix;

    /**
     * 
     */
    private Integer sectionId;

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
        Courseware other = (Courseware) that;
        return (this.getCoursewareId() == null ? other.getCoursewareId() == null : this.getCoursewareId().equals(other.getCoursewareId()))
            && (this.getUploadTime() == null ? other.getUploadTime() == null : this.getUploadTime().equals(other.getUploadTime()))
            && (this.getSize() == null ? other.getSize() == null : this.getSize().equals(other.getSize()))
            && (this.getPath() == null ? other.getPath() == null : this.getPath().equals(other.getPath()))
            && (this.getCoursewareName() == null ? other.getCoursewareName() == null : this.getCoursewareName().equals(other.getCoursewareName()))
            && (this.getSuffix() == null ? other.getSuffix() == null : this.getSuffix().equals(other.getSuffix()))
            && (this.getSectionId() == null ? other.getSectionId() == null : this.getSectionId().equals(other.getSectionId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCoursewareId() == null) ? 0 : getCoursewareId().hashCode());
        result = prime * result + ((getUploadTime() == null) ? 0 : getUploadTime().hashCode());
        result = prime * result + ((getSize() == null) ? 0 : getSize().hashCode());
        result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
        result = prime * result + ((getCoursewareName() == null) ? 0 : getCoursewareName().hashCode());
        result = prime * result + ((getSuffix() == null) ? 0 : getSuffix().hashCode());
        result = prime * result + ((getSectionId() == null) ? 0 : getSectionId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", coursewareId=").append(coursewareId);
        sb.append(", uploadTime=").append(uploadTime);
        sb.append(", size=").append(size);
        sb.append(", path=").append(path);
        sb.append(", coursewareName=").append(coursewareName);
        sb.append(", suffix=").append(suffix);
        sb.append(", sectionId=").append(sectionId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}