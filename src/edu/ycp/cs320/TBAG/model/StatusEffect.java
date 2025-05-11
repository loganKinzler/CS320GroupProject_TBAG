package edu.ycp.cs320.TBAG.model;

public class StatusEffect {
    private String type;
    private int duration;
    private double damage;
    private boolean active;
    
    public StatusEffect(String type, int duration, double damage) {
        this.type = type.toLowerCase();
        this.duration = duration;
        this.damage = damage;
        this.active = true;
    }
    
    // Getters
    public String getType() { return type; }
    public int getDuration() { return duration; }
    public double getDamage() { return damage; }
    public boolean isActive() { return active; }
    
    // Setters
    public void setDuration(int duration) { this.duration = duration; }
    public void setDamage(double damage) { this.damage = damage; }
    public void setActive(boolean active) { this.active = active; }
    
    // Behavior methods
    public void applyEffect(EntityModel target) {
        switch (type) {
            case "stun":
                target.setStunned(true);
                break;
            case "burn":
                target.setFire(true);
                break;
            case "poison":
                target.setPoisoned(true);
                break;
            case "bleed":
                target.setBleeding(true);
                break;
        }
    }
    
    public void removeEffect(EntityModel target) {
        switch (type) {
            case "stun":
                target.setStunned(false);
                break;
            case "burn":
                target.setFire(false);
                break;
            case "poison":
                target.setPoisoned(false);
                break;
            case "bleed":
                target.setBleeding(false);
                break;
        }
    }
    
    public void reduceDuration() {
        if (duration > 0) duration--;
    }
    
    public boolean shouldExpire() {
        return duration <= 0;
    }
}