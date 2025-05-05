package edu.ycp.cs320.TBAG.model;

public class StatusEffect {
    private String type;
    private double damage;
    private int duration;
    private double buildup;
    private boolean active;
    private boolean persistent;
    
    public StatusEffect(String type, double damage, int duration, double buildupThreshold, boolean persistent) {
        this.type = type;
        this.damage = damage;
        this.duration = duration;
        this.buildup = 0;
        this.active = buildup >= buildupThreshold;
        this.persistent = persistent;
    }
    
    // Getters and setters
    public String getType() { return type; }
    public double getDamage() { return damage; }
    public int getDuration() { return duration; }
    public double getBuildup() { return buildup; }
    public boolean isActive() { return active; }
    public boolean isPersistent() { return persistent; }
    
    public void setActive(boolean active) { this.active = active; }
    
    public void addBuildup(double amount) {
        this.buildup += amount;
    }
    
    public void reduceDuration() {
        if (this.duration > 0) {
            this.duration--;
        }
    }
}