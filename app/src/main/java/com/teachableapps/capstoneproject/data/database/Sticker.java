package com.teachableapps.capstoneproject.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "sticker", indices = {@Index(value = {"id"}, unique = true)})
public class Sticker implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int profileId;
    private int projectId;
    private int slotId;
    private String imageSrc;
    private Date completedOn;
    private boolean confirmed;

    @Ignore
    public Sticker(int profileId, int projectId, int slotId, String imageSrc, Date completedOn, boolean confirmed){
        this.profileId = profileId;
        this.projectId = projectId;
        this.slotId = slotId;
        this.imageSrc = imageSrc;
        this.completedOn = completedOn;
        this.confirmed = confirmed;
    }

    // Constructor used by Room
    public Sticker(int id, int profileId, int projectId, int slotId, String imageSrc, Date completedOn, boolean confirmed){
        this.id = id;
        this.profileId = profileId;
        this.projectId = projectId;
        this.slotId = slotId;
        this.imageSrc = imageSrc;
        this.completedOn = completedOn;
        this.confirmed = confirmed;
    }

    // getters
    public int getId() { return id; }
    public int getProfileId() { return profileId; }
    public int getProjectId() { return projectId; }
    public int getSlotId() { return slotId; }
    public String getImageSrc() { return imageSrc; }
    public Date getCompletedOn() { return completedOn; }
    public boolean isConfirmed() { return confirmed; }

    // setters
    public void setId(int id) { this.id = id; }
    public void setProfileId(int pfid) { this.profileId = pfid; }
    public void setProjectId(int pid) { this.projectId = pid; }
    public void setSlotId(int slotId) { this.slotId = slotId; }
    public void setImageSrc(String imageSrc) { this.imageSrc = imageSrc; }
    public void setCompletedOn(Date date) { this.completedOn = date; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
}
