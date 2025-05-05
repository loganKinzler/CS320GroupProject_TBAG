package edu.ycp.cs320.TBAG.model;

public abstract class EntityModel {
    protected double maxHealth, health;
    protected int lives;
    protected int currentRoomIndex;
    protected EntityInventory inventory;
    protected int id;
    protected boolean stunned;
    protected String name;

    public EntityModel(double health, int lives, int currentRoomIndex) {
        this.maxHealth = health;
        this.health = health;
        this.lives = lives;
        this.currentRoomIndex = currentRoomIndex;
        this.inventory = new EntityInventory();
        this.stunned = false;
        this.name = "";
    }

    // Health methods
    public double getHealth() { return health; }
    public void setHealth(double health) {
        if (health < 0) {
            System.out.println("health cannot be negative");
        }
        else if (health > this.maxHealth) {
            System.out.println("health cannot exceed maxHealth (" + health + ", " + this.maxHealth + ")");
        }
        else {
            this.health = health;
        }
    }

    // Lives methods
    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }

    // Max health methods
    public double getMaxHealth() { return maxHealth; }
    public void setMaxHealth(double maxHealth) {
        if (maxHealth < 0) {
            System.out.println("maxHealth cannot be negative");
        }
        else {
            this.maxHealth = maxHealth;
        }
    }

    // Room methods
    public int getCurrentRoomIndex() { return currentRoomIndex; }
    public void setCurrentRoomIndex(int currentRoomIndex) { this.currentRoomIndex = currentRoomIndex; }

    // Inventory methods
    public void setInventory(EntityInventory inventory) { this.inventory = inventory; }
    public EntityInventory getInventory() { return this.inventory; }

    // ID methods
    public void setId(int id) { this.id = id; }
    public int getId() { return this.id; }

    // Stun methods
    public boolean isStunned() { return stunned; }
    public void setStunned(boolean stunned) { this.stunned = stunned; }

    // Name methods
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}