package edu.ycp.cs320.TBAG.model;

public class Weapon extends Item {
    // Weapon-specific properties
    private double damage;
    private String damageType;
    private int critChance;
    private String statusEffect;
    
    // Constructors
    public Weapon(Integer id, String name, String description, 
                 double damage, String damageType, int critChance, String statusEffect) {
        super(id, name, description);
        this.damage = damage;
        this.damageType = damageType != null ? damageType : "";
        this.critChance = critChance;
        this.statusEffect = statusEffect != null ? statusEffect : "";
    }
    
    // Simplified constructor with default values for combat properties
    public Weapon(Integer id, String name, String description, double damage) {
        this(id, name, description, damage, "", 0, "");
    }
    
    // Getters
    public double GetDamage() {
        return damage;
    }
    
    public String GetDamageType() {
        return damageType;
    }
    
    public int GetCritChance() {
        return critChance;
    }
    
    public String GetStatusEffect() {
        return statusEffect;
    }
    
    // Setters
    public void SetDamage(double damage) {
        this.damage = damage;
    }
    
    public void SetDamageType(String damageType) {
        this.damageType = damageType != null ? damageType : "";
    }
    
    public void SetCritChance(int critChance) {
        this.critChance = critChance;
    }
    
    public void SetStatusEffect(String statusEffect) {
        this.statusEffect = statusEffect != null ? statusEffect : "";
    }
}