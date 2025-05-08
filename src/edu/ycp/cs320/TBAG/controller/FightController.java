package edu.ycp.cs320.TBAG.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashMap;
import java.lang.Math;

import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;
import edu.ycp.cs320.TBAG.comparator.PlayerPreferedComparator;
import edu.ycp.cs320.TBAG.model.EnemyModel;
import edu.ycp.cs320.TBAG.model.EntityModel;
import edu.ycp.cs320.TBAG.model.Weapon;
import edu.ycp.cs320.TBAG.model.StatusEffect;

public class FightController {
    private IDatabase database;
    private ArrayList<EntityModel> fightingEntities;
    public HashMap<Integer, StatusEffect> statusEffects; // Track status effects by entity ID
    private static final double BLEED_DAMAGE = 5.0;
    private static final int BLEED_DURATION = 3;
    
    public FightController(ArrayList<EntityModel> fighters, IDatabase db) {
        this.database = db;
        this.fightingEntities = fighters;
        this.statusEffects = new HashMap<>();
        Collections.sort(fightingEntities, new PlayerPreferedComparator());
    }
    
    public EntityModel GetFighter(Integer index) { 
        return fightingEntities.get(index); 
    }
    
    private Double TakeTurn(Integer entityIndex, Integer attackIndex, String weaponSlot) {
    	EntityModel attacker = fightingEntities.get(entityIndex);
        EntityModel target = fightingEntities.get(attackIndex);
        
        // Check if attacker is stunned
        if (attacker.isStunned()) {
            attacker.setStunned(false); // Clear stun status
            System.out.println(attacker.getName() + " is stunned and skips their turn!");
            return 0.0;
        }
        
        Weapon weapon = attacker.getInventory().GetWeapon(weaponSlot);
        if (weapon == null) {
            System.out.println("No weapon found in slot: " + weaponSlot);
            return 0.0;
        }
        
        // Apply stun effect BEFORE damage for blunt weapons
        if (weapon.GetDamageType() != null && 
            weapon.GetDamageType().equalsIgnoreCase("blunt")) {
            target.setStunned(true);
            System.out.println(target.getName() + " stunned by " + weapon.GetName());
        }
        
        // Check for crit
        boolean isCrit = checkForCrit(weapon);
        double damage = calculateDamage(weapon, isCrit);
        
        // Apply damage
        new EntityController(target).AddHealthClamped(-damage);
        
        // Apply status effects if crit
        if (isCrit) {
            applyCritEffects(weapon, target);
        }
        
        // Apply ongoing status effects
        applyStatusEffects(target);
        
        System.out.println(target.getName() + " health: " + target.getHealth());
        
        database.UpdateEnemyHealthById(target.getId(), target.getHealth());
        
        return Math.min(damage, target.getHealth());
    }
    
    public boolean checkForCrit(Weapon weapon) {
        int critRoll = (int)(Math.random() * 100);
        boolean isCrit = critRoll <= weapon.GetCritChance();
        if (isCrit) {
            System.out.println("Critical hit!");
        }
        return isCrit;
    }
    
    public double calculateDamage(Weapon weapon, boolean isCrit) {
        double damage = weapon.GetDamage();
        if (isCrit) {
            damage *= 1.5; // 1.5x damage on crit
        }
        return damage;
    }
    
    public void applyTestStatusEffect(EntityModel target, StatusEffect effect) {
        statusEffects.put(target.getId(), effect);
    }

    // Updated applyCritEffects to ensure effects get added
    private void applyCritEffects(Weapon weapon, EntityModel target) {
        // 1. Get weapon properties
        String damageType = weapon.GetDamageType();
        String statusEffect = weapon.GetStatusEffect();
        
        // 2. Handle damage-type effects (always apply on crit)
        if (damageType != null && !damageType.isEmpty()) {
            switch (damageType.toLowerCase()) {
                case "blunt":
                    // Apply stun effect
                    target.setStunned(true);
                    statusEffects.put(target.getId(), 
                        new StatusEffect("stun", 0, 1, 0, false));
                    System.out.println(target.getName() + " was stunned!");
                    break;
                    
                case "slashing":
                    // Apply bleed if not already present
                    if (!statusEffects.containsKey(target.getId()) || 
                        !statusEffects.get(target.getId()).getType().equals("bleed")) {
                        StatusEffect bleed = new StatusEffect("bleed", 5.0, 3, 0, true);
                        bleed.setActive(true);
                        statusEffects.put(target.getId(), bleed);
                        System.out.println(target.getName() + " is bleeding!");
                    }
                    break;
            }
        }
        
        // 3. Handle weapon-specific status effects
        if (statusEffect != null && !statusEffect.isEmpty()) {
            double buildupAmount = weapon.GetDamage() * 0.1; // 10% of damage as buildup
            StatusEffect existingEffect = statusEffects.get(target.getId());
            
            if (existingEffect == null || !existingEffect.getType().equals(statusEffect)) {
                // Create new effect
                StatusEffect newEffect = createStatusEffect(
                    statusEffect,
                    buildupAmount,
                    weapon.GetDamage()
                );
                statusEffects.put(target.getId(), newEffect);
                System.out.println(target.getName() + " is building up " + statusEffect);
            } else {
                // Add to existing effect
                existingEffect.addBuildup(buildupAmount);
                if (!existingEffect.isActive() && 
                    existingEffect.getBuildup() >= weapon.GetDamage() * 0.5) {
                    existingEffect.setActive(true);
                    System.out.println(target.getName() + " is now affected by " + statusEffect);
                }
            }
        }
    }
    
    private StatusEffect createStatusEffect(String type, double buildup, double weaponDamage) {
        switch (type.toLowerCase()) {
            case "poison":
                return new StatusEffect("poison", weaponDamage * 0.5, 3, weaponDamage * 0.5, false);
            case "burn":
                return new StatusEffect("burn", weaponDamage * 0.3, 2, weaponDamage * 0.5, true);
            case "paralysis":
                return new StatusEffect("paralysis", 0, 2, weaponDamage * 0.4, false);
            default:
                return new StatusEffect(type, 0, 0, 0, false);
        }
    }
    
    public void applyStatusEffects(EntityModel target) {
        StatusEffect effect = statusEffects.get(target.getId());
        if (effect != null && effect.isActive()) {
            switch (effect.getType().toLowerCase()) {
                case "bleed":
                case "poison":
                case "burn":
                    new EntityController(target).AddHealthClamped(-effect.getDamage());
                    effect.reduceDuration();
                    break;
                case "paralysis":
                case "stun":
                    target.setStunned(true);
                    effect.reduceDuration();
                    break;
            }
            
            if (effect.getDuration() <= 0 && !effect.isPersistent()) {
                statusEffects.remove(target.getId());
                if (effect.getType().equalsIgnoreCase("paralysis") || 
                    effect.getType().equalsIgnoreCase("stun")) {
                    target.setStunned(false);
                }
            }
        }
    }
    
    public void handleBurnEffects(EntityModel target) {
        StatusEffect effect = statusEffects.get(target.getId());
        if (effect != null && effect.isActive() && effect.getType().equalsIgnoreCase("burn")) {
            new EntityController(target).AddHealthClamped(-effect.getDamage());
            System.out.println(target.getName() + " takes " + effect.getDamage() + " burn damage!");
        }
    }
    
    public Double TakePlayerTurn(Integer entityIndex, Integer attackIndex, String weaponSlot) {
        return TakeTurn(entityIndex, attackIndex, weaponSlot);
    }
    
    public Double TakeEnemyTurn(Integer enemyIndex) {
        EntityModel enemy = fightingEntities.get(enemyIndex);
        
        // Check stun status
        if (enemy.isStunned()) {
            System.out.println(enemy.getName() + " is stunned and skips turn!");
            enemy.setStunned(false); // Clear stun after skipping turn
            return 0.0; // No damage dealt
        }
        
        Set<String> enemyWeaponSlots = enemy.getInventory().GetWeaponsAsSlots().keySet();
        if (enemyWeaponSlots.size() == 0) {
            System.out.println(enemy.getName() + " has no weapons to attack with!");
            return 0.0;
        }
        
        Integer slotIndex = (int) (enemyWeaponSlots.size() * Math.random());
        String randomWeaponSlot = (String) enemyWeaponSlots.toArray()[slotIndex];
        
        Double damage = TakeTurn(enemyIndex, 0, randomWeaponSlot);
        
        // Handle burn effects when enemy finishes turn
        handleBurnEffects(fightingEntities.get(0)); // Assuming player is at index 0
        
        return damage;
    }
    
    public StatusEffect getStatusEffect(int entityId) {
        return statusEffects.get(entityId);
    }
}