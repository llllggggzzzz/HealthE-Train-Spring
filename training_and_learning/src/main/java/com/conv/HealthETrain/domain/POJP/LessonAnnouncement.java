package com.conv.HealthETrain.domain.POJP;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liusg
 */
@Data
@TableName("lesson_announcement")
public class LessonAnnouncement implements Serializable {
    @TableId
    private Long laId;
    private Long lessonId;
    private String announcementTitle;
    private String announcementContent;
    private Date publishTime;

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
        LessonAnnouncement other = (LessonAnnouncement) that;
        return (this.getLaId() == null ? other.getLaId() == null : this.getLaId().equals(other.getLaId()))
            && (this.getLessonId() == null ? other.getLessonId() == null : this.getLessonId().equals(other.getLessonId()))
            && (this.getAnnouncementTitle() == null ? other.getAnnouncementTitle() == null : this.getAnnouncementTitle().equals(other.getAnnouncementTitle()))
            && (this.getAnnouncementContent() == null ? other.getAnnouncementContent() == null : this.getAnnouncementContent().equals(other.getAnnouncementContent()))
            && (this.getPublishTime() == null ? other.getPublishTime() == null : this.getPublishTime().equals(other.getPublishTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getLaId() == null) ? 0 : getLaId().hashCode());
        result = prime * result + ((getLessonId() == null) ? 0 : getLessonId().hashCode());
        result = prime * result + ((getAnnouncementTitle() == null) ? 0 : getAnnouncementTitle().hashCode());
        result = prime * result + ((getAnnouncementContent() == null) ? 0 : getAnnouncementContent().hashCode());
        result = prime * result + ((getPublishTime() == null) ? 0 : getPublishTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", laId=").append(laId);
        sb.append(", lessonId=").append(lessonId);
        sb.append(", announcementTitle=").append(announcementTitle);
        sb.append(", announcementContent=").append(announcementContent);
        sb.append(", publishTime=").append(publishTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

}
