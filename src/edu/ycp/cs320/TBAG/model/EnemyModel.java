package edu.ycp.cs320.TBAG.model;

public class EnemyModel extends EntityModel {
    protected String description;
    private boolean isStunned; // Simple stun flag
    
    public EnemyModel(double health, int lives, int currentRoomIndex, String name, String description) {
        super(health, lives, currentRoomIndex);
        this.name = name;
        this.description = description;
        this.isStunned = false;
    }
    
    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }
    
    // Stun methods
    public boolean isStunned() {
        return isStunned;
    }
    
    public void setStunned(boolean stunned) {
        this.isStunned = stunned;
    }
}