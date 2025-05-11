package edu.ycp.cs320.TBAG.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import edu.ycp.cs320.TBAG.model.*;
import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;
import edu.ycp.cs320.TBAG.comparator.PlayerPreferedComparator;

public class FightController {
    private IDatabase database;
    public ArrayList<EntityModel> fightingEntities;
    
    public FightController(ArrayList<EntityModel> fighters, IDatabase db) {
        this.database = db;
        this.fightingEntities = fighters;
        Collections.sort(fightingEntities, new PlayerPreferedComparator());
    }
    
    public EntityModel GetFighter(Integer index) { 
        return fightingEntities.get(index); 
    }
    
    private Double takeTurn(Integer attackerIndex, Integer targetIndex, String weaponSlot) {
        EntityModel attacker = fightingEntities.get(attackerIndex);
        EntityModel target = fightingEntities.get(targetIndex);
        
        Weapon weapon = attacker.getInventory().GetWeapon(weaponSlot);
        if (weapon == null) return 0.0;
        
        boolean isCrit = checkForCrit(weapon);
        double damage = calculateDamage(weapon, isCrit);
        
        target.setHealth(target.getHealth() - damage);
        
        if (isCrit || weapon.GetStatusEffect() != null) {
            applyWeaponEffects(weapon, target);
        }
        
        return damage;
    }
    
    public Double takePlayerTurn(Integer playerIndex, Integer targetIndex, String weaponSlot) {
        // Process status effects on player at start of turn
        processStatusEffects(fightingEntities.get(playerIndex));
        return takeTurn(playerIndex, targetIndex, weaponSlot);
    }
    
    public Double takeEnemyTurn(Integer enemyIndex, Integer playerIndex) {
        EntityModel enemy = fightingEntities.get(enemyIndex);
        
        // Process status effects at start of turn
        processStatusEffects(enemy);
        
        // Check stun status - both flag and effect
        if (enemy.isStunned() || enemy.hasEffect("stun")) {
            enemy.setStunned(false);
            if (enemy.hasEffect("stun")) {
                enemy.removeEffect("stun");
            }
            return 0.0; // Stunned enemies can't attack
        }
        
        // Only proceed with attack if not stunned
        Set<String> slots = enemy.getInventory().GetWeaponsAsSlots().keySet();
        if (slots.isEmpty()) {
            return 0.0;
        }
        
        String randomSlot = slots.iterator().next();
        return takeTurn(enemyIndex, playerIndex, randomSlot);
    }
    
    
    private void applyWeaponEffects(Weapon weapon, EntityModel target) {
        if (weapon.GetDamageType() != null) {
            switch (weapon.GetDamageType().toLowerCase()) {
                case "blunt":
                    target.setStunned(true); // Set the stunned flag
                    StatusEffect stun = new StatusEffect("stun", 1, 0);
                    target.addEffect(stun);
                    stun.applyEffect(target);
                    break;
                case "slashing":
                    StatusEffect bleed = new StatusEffect("bleed", 2, 2.0); // Bleed lasts 2 turns
                    target.addEffect(bleed);
                    bleed.applyEffect(target);
                    break;
            }
        }
        
        if (weapon.GetStatusEffect() != null) {
            StatusEffect effect = createStatusEffect(
                weapon.GetStatusEffect(), 
                weapon.GetDamage()
            );
            target.addEffect(effect);
            effect.applyEffect(target);
        }
    }
    
    private StatusEffect createStatusEffect(String type, double baseDamage) {
        switch (type.toLowerCase()) {
            case "poison":
                return new StatusEffect("poison", 2, baseDamage * 0.3); // Poison lasts 2 turns
            case "burn":
                return new StatusEffect("burn", 2, baseDamage * 0.25); // Burn lasts 2 turns
            case "bleed":
                return new StatusEffect("bleed", 2, baseDamage * 0.15); // Bleed lasts 2 turns
            case "stun":
                return new StatusEffect("stun", 1, 0); // Stun lasts 1 turn
            default:
                return new StatusEffect(type, 1, baseDamage * 0.1);
        }
    }
    
    public void processStatusEffects(EntityModel target) {
        List<StatusEffect> effects = new ArrayList<>(target.getActiveEffects().values());
        
        for (StatusEffect effect : effects) {
            if (effect.isActive()) {
                // Apply damage for damaging effects
                if (effect.getDamage() > 0) {
                    target.setHealth(target.getHealth() - effect.getDamage());
                }
                
                if (effect.shouldExpire()) {
                    effect.removeEffect(target);
                    target.removeEffect(effect.getType());
                    // Clear corresponding flags
                    if (effect.getType().equals("stun")) {
                        target.setStunned(false);
                    }
                } else {
                    effect.reduceDuration();
                }
            }
        }
    }
    
    private boolean checkForCrit(Weapon weapon) {
        return Math.random() * 100 <= weapon.GetCritChance();
    }
    
    private double calculateDamage(Weapon weapon, boolean isCrit) {
        return isCrit ? weapon.GetDamage() * 1.5 : weapon.GetDamage();
    }
    
    public void updatePlayerInDatabase(PlayerModel player) {
        if (database != null) {
            database.UpdatePlayerHealth(player.getHealth());
            database.UpdatePlayerMaxHealth(player.getMaxHealth());
            database.UpdatePlayerLives(player.getLives());
        }
    }
    
    public void updateEnemyInDatabase(EnemyModel enemy) {
        if (database != null) {
            database.UpdateEnemyHealthById(enemy.getId(), enemy.getHealth());
            database.UpdateEnemyMaxHealthById(enemy.getId(), enemy.getMaxHealth());
            database.UpdateEnemyLivesById(enemy.getId(), enemy.getLives());
        }
    }
    
    public void endCombatRound() {
        for (EntityModel entity : fightingEntities) {
            if (entity instanceof PlayerModel) {
                updatePlayerInDatabase((PlayerModel) entity);
            } else if (entity instanceof EnemyModel) {
                updateEnemyInDatabase((EnemyModel) entity);
            }
        }
    }
    
    public List<Integer> processDefeatedEnemies() {
        List<Integer> defeatedEnemies = new ArrayList<>();
        Iterator<EntityModel> iterator = fightingEntities.iterator();
        
        while (iterator.hasNext()) {
            EntityModel entity = iterator.next();
            if (entity instanceof EnemyModel && entity.getHealth() <= 0) {
                defeatedEnemies.add(((EnemyModel) entity).getId());
                iterator.remove();
                
                if (database != null) {
                    database.UpdateEnemyRoomById(((EnemyModel) entity).getId(), -1);
                }
            }
        }
        
        return defeatedEnemies;
    }
    private double handleStunnedEnemy(EntityModel enemy) {
        // Clear the stun immediately
        enemy.setStunned(false);
        StatusEffect stun = enemy.getEffect("stun");
        if (stun != null) {
            enemy.removeEffect("stun");
        }
        return 0.0; // Stunned enemies deal no damage
    }
}