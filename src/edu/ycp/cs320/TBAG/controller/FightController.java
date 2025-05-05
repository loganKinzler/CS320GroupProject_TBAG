package edu.ycp.cs320.TBAG.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashMap;
import java.lang.Math;

import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;
import edu.ycp.cs320.TBAG.comparator.PlayerPreferedComparator;

import edu.ycp.cs320.TBAG.controller.EntityController;
import edu.ycp.cs320.TBAG.model.EntityModel;
import edu.ycp.cs320.TBAG.model.Weapon;
import edu.ycp.cs320.TBAG.model.StatusEffect;

public class FightController {
    private IDatabase database;
    private ArrayList<EntityModel> fightingEntities;
    private HashMap<Integer, StatusEffect> statusEffects; // Track status effects by entity ID
    
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
        
        // Check if attacker is stunned
        if (attacker.isStunned()) {
            attacker.setStunned(false); // Clear stun status
            System.out.println(attacker.getName() + " is stunned and skips their turn!");
            return 0.0;
        }
        
        Weapon weapon = attacker.getInventory().GetWeapon(weaponSlot);
        EntityModel target = fightingEntities.get(attackIndex);
        
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
    
    private boolean checkForCrit(Weapon weapon) {
        int critRoll = (int)(Math.random() * 100);
        boolean isCrit = critRoll <= weapon.GetCritChance();
        if (isCrit) {
            System.out.println("Critical hit!");
        }
        return isCrit;
    }
    
    private double calculateDamage(Weapon weapon, boolean isCrit) {
        double damage = weapon.GetDamage();
        if (isCrit) {
            damage *= 1.5; // 1.5x damage on crit
        }
        return damage;
    }
    
    private void applyCritEffects(Weapon weapon, EntityModel target) {
        String damageType = weapon.GetDamageType();
        String statusEffect = weapon.GetStatusEffect();
        
        // Handle damage type effects
        if (damageType != null && !damageType.isEmpty()) {
            switch (damageType.toLowerCase()) {
                case "blunt":
                    target.setStunned(true);
                    System.out.println(target.getName() + " was stunned by the blunt attack!");
                    break;
                case "slashing":
                    StatusEffect bleed = new StatusEffect("bleed", 5, 0, 0, true);
                    statusEffects.put(target.getId(), bleed);
                    System.out.println(target.getName() + " is bleeding!");
                    break;
            }
        }
        
        // Handle status effect buildup
        if (statusEffect != null && !statusEffect.isEmpty()) {
            double buildupAmount = weapon.GetDamage() / 10; // 1/10 of base damage
            StatusEffect existingEffect = statusEffects.get(target.getId());
            
            if (existingEffect == null || !existingEffect.getType().equals(statusEffect)) {
                // New effect
                StatusEffect newEffect = createStatusEffect(statusEffect, buildupAmount, weapon.GetDamage());
                statusEffects.put(target.getId(), newEffect);
                System.out.println(target.getName() + " is building up " + statusEffect + "!");
            } else {
                // Add to existing effect
                existingEffect.addBuildup(buildupAmount);
                if (existingEffect.getBuildup() >= weapon.GetDamage() / 2 && !existingEffect.isActive()) {
                    // Effect procs
                    existingEffect.setActive(true);
                    System.out.println(target.getName() + " is now affected by " + statusEffect + "!");
                }
            }
        }
    }
    
    private StatusEffect createStatusEffect(String type, double buildup, double weaponDamage) {
        switch (type.toLowerCase()) {
            case "paralysis":
                return new StatusEffect("paralysis", 0, 2, weaponDamage / 2, false);
            case "poison":
                return new StatusEffect("poison", 10, 0, weaponDamage / 2, false);
            case "burn":
                return new StatusEffect("burn", 2.5, 0, weaponDamage / 2, true);
            default:
                return new StatusEffect(type, 0, 0, weaponDamage / 2, false);
        }
    }
    
    private void applyStatusEffects(EntityModel target) {
        StatusEffect effect = statusEffects.get(target.getId());
        if (effect != null && effect.isActive()) {
            switch (effect.getType().toLowerCase()) {
                case "bleed":
                    new EntityController(target).AddHealthClamped(-effect.getDamage());
                    System.out.println(target.getName() + " takes " + effect.getDamage() + " bleed damage!");
                    break;
                case "poison":
                    new EntityController(target).AddHealthClamped(-effect.getDamage());
                    System.out.println(target.getName() + " takes " + effect.getDamage() + " poison damage!");
                    break;
                case "paralysis":
                    if (effect.getDuration() > 0) {
                        effect.reduceDuration();
                        target.setStunned(true);
                    } else {
                        statusEffects.remove(target.getId());
                        System.out.println(target.getName() + " is no longer paralyzed!");
                    }
                    break;
            }
            
            // Remove effect if duration expired
            if (effect.getDuration() == 0 && !effect.isPersistent()) {
                statusEffects.remove(target.getId());
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
        
        // Check if enemy is stunned
        if (enemy.isStunned()) {
            enemy.setStunned(false);
            System.out.println(enemy.getName() + " is stunned and skips their turn!");
            return 0.0;
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
}