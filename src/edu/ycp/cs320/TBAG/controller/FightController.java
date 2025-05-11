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
        
        if (attacker.isStunned()) {
            processStun(attacker);
            return 0.0;
        }
        
        Weapon weapon = attacker.getInventory().GetWeapon(weaponSlot);
        if (weapon == null) return 0.0;
        
        boolean isCrit = checkForCrit(weapon);
        double damage = calculateDamage(weapon, isCrit);
        
        // Apply damage
        target.setHealth(target.getHealth() - damage);
        
        // Apply effects on crit or based on weapon properties
        if (isCrit || weapon.GetStatusEffect() != null) {
            applyWeaponEffects(weapon, target);
        }
        
        // Process all effects at end of turn
        processStatusEffects(target);
        
        return damage;
    }
    
    public Double takePlayerTurn(Integer playerIndex, Integer targetIndex, String weaponSlot) {
        return takeTurn(playerIndex, targetIndex, weaponSlot);
    }
    
    public Double takeEnemyTurn(Integer enemyIndex, Integer playerIndex) {
        EntityModel enemy = fightingEntities.get(enemyIndex);
        
        if (enemy.isStunned()) {
            processStun(enemy);
            return 0.0;
        }
        
        
        // Get random weapon
        Set<String> slots = enemy.getInventory().GetWeaponsAsSlots().keySet();
        if (slots.isEmpty()) return 0.0;
        
        String randomSlot = slots.iterator().next();
        return takeTurn(enemyIndex, playerIndex, randomSlot);
    }
    
    /**
     * Updates player stats in the database after combat
     */
    public void updatePlayerInDatabase(PlayerModel player) {
        if (database != null) {
            database.UpdatePlayerHealth(player.getHealth());
            database.UpdatePlayerMaxHealth(player.getMaxHealth());
            database.UpdatePlayerLives(player.getLives());
            database.UpdatePlayerInventory(player.getInventory());
        }
    }

    /**
     * Updates enemy stats in the database after combat
     */
    public void updateEnemyInDatabase(EnemyModel enemy) {
        if (database != null) {
            database.UpdateEnemyHealthById(enemy.getId(), enemy.getHealth());
            database.UpdateEnemyMaxHealthById(enemy.getId(), enemy.getMaxHealth());
            database.UpdateEnemyLivesById(enemy.getId(), enemy.getLives());
            database.UpdateEnemyInventory(enemy.getId(), enemy.getInventory());
        }
    }

    /**
     * Processes end of combat and updates all entities in database
     */
    public void endCombatRound() {
        for (EntityModel entity : fightingEntities) {
            if (entity instanceof PlayerModel) {
                updatePlayerInDatabase((PlayerModel) entity);
            } else if (entity instanceof EnemyModel) {
                updateEnemyInDatabase((EnemyModel) entity);
            }
        }
    }

    /**
     * Checks if any enemies are defeated and removes them from combat
     * @return list of defeated enemy IDs
     */
    public List<Integer> processDefeatedEnemies() {
        List<Integer> defeatedEnemies = new ArrayList<>();
        Iterator<EntityModel> iterator = fightingEntities.iterator();
        
        while (iterator.hasNext()) {
            EntityModel entity = iterator.next();
            if (entity instanceof EnemyModel && entity.getHealth() <= 0) {
                defeatedEnemies.add(((EnemyModel) entity).getId());
                iterator.remove();
                
                if (database != null) {
                    database.UpdateEnemyRoomById(((EnemyModel) entity).getId(), -1); // Move to "defeated" room
                }
            }
        }
        
        return defeatedEnemies;
    }
    
    private void processStun(EntityModel entity) {
        StatusEffect stun = entity.getEffect("stun");
        if (stun != null) {
            stun.reduceDuration();
            if (stun.shouldExpire()) {
                stun.removeEffect(entity);
                entity.removeEffect("stun");
            }
        }
        System.out.println(entity.getName() + " is stunned and skips their turn!");
    }
    
    private void applyWeaponEffects(Weapon weapon, EntityModel target) {
        // Apply effect based on weapon type (on crit)
        if (weapon.GetDamageType() != null) {
            switch (weapon.GetDamageType().toLowerCase()) {
                case "blunt":
                    StatusEffect stun = new StatusEffect("stun", 1, 0);
                    target.addEffect(stun);
                    stun.applyEffect(target);
                    System.out.println(target.getName() + " was stunned!");
                    break;
                case "slashing":
                    StatusEffect bleed = new StatusEffect("bleed", 3, 2.0);
                    target.addEffect(bleed);
                    bleed.applyEffect(target);
                    System.out.println(target.getName() + " is bleeding!");
                    break;
            }
        }
        
        // Apply weapon-specific effect (always applies if weapon has effect)
        if (weapon.GetStatusEffect() != null) {
            StatusEffect effect = createStatusEffect(
                weapon.GetStatusEffect(), 
                weapon.GetDamage()
            );
            target.addEffect(effect);
            effect.applyEffect(target);
            System.out.println(target.getName() + " is affected by " + effect.getType());
        }
    }
    
    private StatusEffect createStatusEffect(String type, double baseDamage) {
        switch (type.toLowerCase()) {
            case "poison":
                return new StatusEffect("poison", 3, baseDamage * 0.3);
            case "burn":
                return new StatusEffect("burn", 2, baseDamage * 0.25);
            case "bleed":
                return new StatusEffect("bleed", 3, baseDamage * 0.15);
            case "stun":
                return new StatusEffect("stun", 1, 0);
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
                    System.out.println(target.getName() + " takes " + 
                                     effect.getDamage() + " " + effect.getType() + " damage");
                }
                
                // Reduce duration
                effect.reduceDuration();
                if (effect.shouldExpire()) {
                    effect.removeEffect(target);
                    target.removeEffect(effect.getType());
                    System.out.println(effect.getType() + " effect expired on " + target.getName());
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
}