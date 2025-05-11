package edu.ycp.cs320.TBAG.model;

public class EnemyModel extends EntityModel {
    protected String description;
    
    public EnemyModel(double health, int lives, int currentRoomIndex, String name, String description) {
        super(health, lives, currentRoomIndex);
        this.name = name;
        this.description = description;
    }
    
    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }
}