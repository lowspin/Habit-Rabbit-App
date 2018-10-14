package com.teachableapps.capstoneproject.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "project", indices = {@Index(value = {"id"}, unique = true)})
public class ProjectEntry implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int profileId;
    private String title;
    private String description;
    private int repTarget;
    private int repCompleted;
    private double score;
    private int remindMins;
    private int practiceDays;
    private boolean needApproval;

    @Ignore
    public ProjectEntry(int profileId, String title, String description, int repTarget, int repCompleted, double score, int remindMins, int practiceDays, boolean needApproval) {
        this.profileId = profileId;
        this.title = title;
        this.description = description;
        this.repTarget = repTarget;
        this.repCompleted = repCompleted;
        this.score = score;
        this.remindMins = remindMins;
        this.practiceDays = practiceDays;
        this.needApproval = needApproval;
    }

    // Constructor used by Room to create ProjectEntries
    public ProjectEntry(int id, int profileId, String title, String description, int repTarget, int repCompleted, double score, int remindMins, int practiceDays, boolean needApproval) {
        this.id = id;
        this.profileId = profileId;
        this.title = title;
        this.description = description;
        this.repTarget = repTarget;
        this.repCompleted = repCompleted;
        this.score = score;
        this.remindMins = remindMins;
        this.practiceDays = practiceDays;
        this.needApproval = needApproval;
    }

    // getters
    public int getId() { return id; }
    public int getProfileId() { return profileId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getRepTarget() { return repTarget; }
    public int getRepCompleted() { return repCompleted; }
    public double getScore() { return score; }
    public int getRemindMins() { return remindMins; }
    public int getPracticeDays() { return practiceDays; }
    public boolean isNeedApproval() { return needApproval; }

    // setters
    public void setId(int id) { this.id = id; }
    public void setProfileId(int pid) { this.profileId = pid; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String d) { this.description = d; }
    public void setRepTarget(int rep) { this.repTarget = rep; }
    public void setRepCompleted(int rep) { this.repCompleted = rep; }
    public void setScore(double score) { this.score = score; }
    public void setRemindMins(int mins) { this.remindMins = mins; }
    public void setPracticeDays(int days) { this.practiceDays = days; }
    public void setNeedApproval(boolean needApproval) { this.needApproval = needApproval; }

}
