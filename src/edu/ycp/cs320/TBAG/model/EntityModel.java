package edu.ycp.cs320.TBAG.model;

import java.util.HashMap;
import java.util.Map;

public abstract class EntityModel {
    protected double maxHealth, health;
    protected int lives;
    protected int currentRoomIndex;
    protected EntityInventory inventory;
    protected int id;
    protected String name;
    protected Map<String, StatusEffect> activeEffects;
    protected boolean stunned;
    protected boolean onFire;
    protected boolean poisoned;
    protected boolean bleeding;
    
    public EntityModel(double health, int lives, int currentRoomIndex) {
        this.maxHealth = health;
        this.health = health;
        this.lives = lives;
        this.currentRoomIndex = currentRoomIndex;
        this.inventory = new EntityInventory();
        this.name = "";
        this.activeEffects = new HashMap<>();
        this.stunned = false;
        this.onFire = false;
        this.poisoned = false;
        this.bleeding = false;
    }

    // Health methods
    public double getHealth() { return health; }
    public void setHealth(double health) {
        this.health = Math.max(0, Math.min(health, maxHealth));
    }

    // Lives methods
    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }

    // Max health methods
    public double getMaxHealth() { return maxHealth; }
    public void setMaxHealth(double maxHealth) { 
        this.maxHealth = Math.max(0, maxHealth);
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

    // Name methods
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Status effect methods
    public boolean hasEffect(String type) {
        return activeEffects.containsKey(type.toLowerCase());
    }

    public StatusEffect getEffect(String type) {
        return activeEffects.get(type.toLowerCase());
    }

    public Map<String, StatusEffect> getActiveEffects() {
        return new HashMap<>(activeEffects);
    }

    public void addEffect(StatusEffect newEffect) {
        StatusEffect existingEffect = activeEffects.get(newEffect.getType());
        if (existingEffect != null) {
            // Keep the higher damage value
            double newDamage = Math.max(existingEffect.getDamage(), newEffect.getDamage());
            // Reset duration to the new effect's duration
            int newDuration = newEffect.getDuration();
            activeEffects.put(newEffect.getType(), 
                             new StatusEffect(newEffect.getType(), newDuration, newDamage));
        } else {
            activeEffects.put(newEffect.getType(), newEffect);
        }
    }

    public void removeEffect(String type) {
        activeEffects.remove(type.toLowerCase());
    }

    public void clearEffects() {
        activeEffects.clear();
    }

    // Stun methods
    public boolean isStunned() {
        // Only check the stunned flag, not effects
        return stunned; 
    }

    public void setStunned(boolean stunned) {
        this.stunned = stunned;
        // Don't automatically manage effects here
    }
    public boolean isOnFire() { return onFire; }
    public void setFire(boolean onFire) { this.onFire = onFire; }
    
    public boolean isPoisoned() { return poisoned; }
    public void setPoisoned(boolean poisoned) { this.poisoned = poisoned; }
    
    public boolean isBleeding() { return bleeding; }
    public void setBleeding(boolean bleeding) { this.bleeding = bleeding; }
}