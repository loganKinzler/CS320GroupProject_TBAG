package edu.ycp.cs320.TBAG.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.TBAG.controller.FightController;
import java.util.ArrayList;
import java.util.List;

public class FightControllerTest {
    private FightController fightController;
    private PlayerModel player;
    private EnemyModel enemy;
    private final int PLAYER_INDEX = 0;
    private final int ENEMY_INDEX = 1;
    
    @Before
    public void setUp() {
        player = new PlayerModel(100.0, 3, 0);
        enemy = new EnemyModel(50.0, 1, 1, "Goblin", "A nasty creature");
        
        Weapon hammer = new Weapon(1, "Hammer", "Blunt weapon", 15.0);
        hammer.SetDamageType("blunt");
        hammer.SetCritChance(100);
        
        Weapon dagger = new Weapon(2, "Poison Dagger", "Toxic blade", 10.0);
        dagger.SetStatusEffect("poison");
        dagger.SetCritChance(100);
        
        Weapon axe = new Weapon(3, "Axe", "Sharp axe", 12.0);
        axe.SetDamageType("slashing");
        axe.SetCritChance(100);
        
        player.getInventory().EquipWeapon("Right Hand", hammer);
        player.getInventory().EquipWeapon("Left Hand", dagger);
        enemy.getInventory().EquipWeapon("Right Hand", axe);
        
        ArrayList<EntityModel> fighters = new ArrayList<>();
        fighters.add(player);
        fighters.add(enemy);
        fightController = new FightController(fighters, null);
    }

    @Test
    public void testStunEffectApplication() {
        fightController.takePlayerTurn(PLAYER_INDEX, ENEMY_INDEX, "Right Hand");
        assertTrue("Enemy should be stunned", enemy.isStunned());
        assertTrue("Enemy should have stun effect", enemy.hasEffect("stun"));
        assertEquals("Stun duration should be 1", 1, enemy.getEffect("stun").getDuration());
    }
    
    @Test
    public void testStunnedSkipTurn() {
        enemy.setStunned(true);
        double damage = fightController.takeEnemyTurn(ENEMY_INDEX, PLAYER_INDEX);
        assertEquals(0.0, damage, 0.001);
        assertFalse(enemy.isStunned());
    }
    
    @Test
    public void testPoisonEffect() {
        fightController.takePlayerTurn(PLAYER_INDEX, ENEMY_INDEX, "Left Hand");
        assertTrue("Enemy should be poisoned", enemy.isPoisoned());
        assertEquals("Poison duration should be 2", 2, enemy.getEffect("poison").getDuration());
        
        double initialHealth = enemy.getHealth();
        fightController.processStatusEffects(enemy);
        assertEquals("Health should decrease by poison damage", 
                    initialHealth - 3.0, enemy.getHealth(), 0.001);
    }
    
    @Test
    public void testBleedEffect() {
        fightController.takeEnemyTurn(ENEMY_INDEX, PLAYER_INDEX);
        assertTrue("Player should be bleeding", player.isBleeding());
        assertEquals("Bleed duration should be 2", 2, player.getEffect("bleed").getDuration());
    }
    
    @Test
    public void testBurnEffect() {
        Weapon fireSword = new Weapon(4, "Fire Sword", "Burning", 20.0);
        fireSword.SetStatusEffect("burn");
        player.getInventory().EquipWeapon("Right Hand", fireSword);
        
        fightController.takePlayerTurn(PLAYER_INDEX, ENEMY_INDEX, "Right Hand");
        assertTrue("Enemy should be burning", enemy.isOnFire());
        assertEquals("Burn duration should be 2", 2, enemy.getEffect("burn").getDuration());
    }
    
    @Test
    public void testEffectStacking() {
        StatusEffect poison1 = new StatusEffect("poison", 2, 3.0);
        StatusEffect poison2 = new StatusEffect("poison", 2, 5.0);
        
        enemy.addEffect(poison1);
        enemy.addEffect(poison2);
        
        assertEquals(1, enemy.getActiveEffects().size());
        assertEquals(2, enemy.getEffect("poison").getDuration());
        assertEquals(5.0, enemy.getEffect("poison").getDamage(), 0.001);
    }
    
    @Test
    public void testEffectExpiration() {
        enemy.addEffect(new StatusEffect("burn", 1, 5.0));
        assertTrue(enemy.hasEffect("burn"));
        
        fightController.processStatusEffects(enemy);
        assertFalse(enemy.hasEffect("burn"));
    }
    
    @Test
    public void testMultipleEffects() {
        // First apply poison
        fightController.takePlayerTurn(PLAYER_INDEX, ENEMY_INDEX, "Left Hand");
        
        // Then apply burn with fire sword
        Weapon fireSword = new Weapon(4, "Fire Sword", "Burning", 20.0);
        fireSword.SetStatusEffect("burn");
        player.getInventory().EquipWeapon("Right Hand", fireSword);
        fightController.takePlayerTurn(PLAYER_INDEX, ENEMY_INDEX, "Right Hand");
        
        assertTrue("Enemy should be poisoned", enemy.isPoisoned());
        assertTrue("Enemy should be burning", enemy.isOnFire());
        
        double initialHealth = enemy.getHealth();
        fightController.processStatusEffects(enemy);
        
        // Poison damage (3) + burn damage (5) = 8 total
        assertEquals("Health should decrease by combined effect damage",
                    initialHealth - 8.0, enemy.getHealth(), 0.001);
    }
    
    @Test
    public void testProcessDefeatedEnemies() {
        enemy.setHealth(0);
        
        List<Integer> defeated = fightController.processDefeatedEnemies();
        assertEquals(1, defeated.size());
        assertEquals(1, fightController.fightingEntities.size());
    }
    
    @Test
    public void testNullDatabaseSafety() {
        ArrayList<EntityModel> fighters = new ArrayList<>();
        fighters.add(player);
        FightController nullDbController = new FightController(fighters, null);
        
        // Should not throw exceptions
        nullDbController.updatePlayerInDatabase(player);
        nullDbController.updateEnemyInDatabase(enemy);
        nullDbController.endCombatRound();
        nullDbController.processDefeatedEnemies();
    }
}