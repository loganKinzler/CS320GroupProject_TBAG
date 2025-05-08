package edu.ycp.cs320.TBAG.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.TBAG.controller.FightController;
import edu.ycp.cs320.TBAG.tbagdb.persist.DerbyDatabase;
import java.util.ArrayList;

public class FightControllerTest {
    private FightController fightController;
    private PlayerModel player;
    private EnemyModel enemy;
    private DerbyDatabase db;
    
    
    
    @Before
    public void setUp() {
        db = new DerbyDatabase("junit");
        
        // Initialize player
        player = new PlayerModel(100.0, 3, 0);
        
        // Initialize enemy
        enemy = new EnemyModel(50.0, 1, 1, "Goblin", "A nasty creature");
        Weapon club = new Weapon(2, "Club", "Wooden club", 10.0);
        enemy.getInventory().GetWeaponsAsSlots().put("Left Hand", club);
        
        // Setup test weapons with 100% crit chance FOR TESTING ONLY
        Weapon hammer = new Weapon(1, "Hammer", "Blunt weapon", 15.0);
        hammer.SetDamageType("blunt");
        hammer.SetCritChance(100); // Only for tests
        
        Weapon poisonDagger = new Weapon(3, "Poison Dagger", "Toxic blade", 20.0);
        poisonDagger.SetStatusEffect("poison");
        poisonDagger.SetCritChance(100); // Only for tests
        
        Weapon axe = new Weapon(4, "Axe", "Sharp axe", 18.0);
        axe.SetDamageType("slashing");
        axe.SetCritChance(100); // Only for tests
        
        // Equip weapons
        player.getInventory().EquipWeapon("Right Hand", hammer);
        player.getInventory().EquipWeapon("Left Hand", poisonDagger);
        
        ArrayList<EntityModel> fighters = new ArrayList<>();
        fighters.add(player);
        fighters.add(enemy);
        fightController = new FightController(fighters, db);
    }
    
    @Test
    public void testGetFighter() {
        EntityModel fighter = fightController.GetFighter(0);
        assertEquals(player, fighter);
        
        fighter = fightController.GetFighter(1);
        assertEquals(enemy, fighter);
    }
    
    @Test
    public void testPlayerTurnDamage() {
        double initialHealth = enemy.getHealth();
        double damageDealt = fightController.TakePlayerTurn(0, 1, "Right Hand");
        
        assertTrue(damageDealt > 0);
        assertTrue(enemy.getHealth() < initialHealth);
        assertEquals(initialHealth - enemy.getHealth(), damageDealt, 0.001);
        
        // Verify enemy health changed - we can't check database directly
        assertNotEquals(initialHealth, enemy.getHealth());
    }
    
    @Test
    public void testCritCalculation() {
        Weapon testWeapon = new Weapon(3, "Test", "Test", 10.0);
        testWeapon.SetCritChance(100); // Guaranteed crit
        
        boolean isCrit = fightController.checkForCrit(testWeapon);
        assertTrue(isCrit);
        
        double damage = fightController.calculateDamage(testWeapon, true);
        assertEquals(15.0, damage, 0.001); // 10 * 1.5 for crit
    }
    
    
   
    
    @Test
    public void testStunEffectApplication() {
        // 1. Verify pre-conditions
        assertFalse("Enemy should not start stunned", enemy.isStunned());
        
        // 2. Get the equipped weapon
        Weapon equippedWeapon = player.getInventory().GetWeapon("Right Hand");
        assertNotNull("Weapon should be equipped", equippedWeapon);
        assertEquals("Weapon should be blunt type", 
                    "blunt", equippedWeapon.GetDamageType());
        
        // 3. Perform attack
        double damage = fightController.TakePlayerTurn(0, 1, "Right Hand");
        assertTrue("Attack should deal damage", damage > 0);
        
        // 4. Verify stun application
        assertTrue("Enemy should be stunned after blunt attack", 
                  enemy.isStunned());
        
        // 5. Verify turn skipping
        double initialHealth = player.getHealth();
        double enemyDamage = fightController.TakeEnemyTurn(1);
        assertEquals("Stunned enemy should deal 0 damage", 
                    0.0, enemyDamage, 0.001);
        assertEquals("Player health should not change",
                    initialHealth, player.getHealth(), 0.001);
        assertFalse("Stun should clear after turn", 
                   enemy.isStunned());
    }
    
   
    
    @Test
    public void testEffectDamageOverTime() {
        // Set up poison effect directly
        StatusEffect poison = new StatusEffect("poison", 5.0, 3, 0, true);
        fightController.statusEffects.put(enemy.getId(), poison);
        
        double initialHealth = enemy.getHealth();
        fightController.applyStatusEffects(enemy);
        
        assertTrue("Enemy should take poison damage", enemy.getHealth() < initialHealth);
        assertEquals(initialHealth - 5.0, enemy.getHealth(), 0.001);
    }
    
   
    
    @Test
    public void testEnemyTurn() {
        double initialHealth = player.getHealth();
        double damageDealt = fightController.TakeEnemyTurn(1);
        
        assertTrue(damageDealt > 0);
        assertTrue(player.getHealth() < initialHealth);
    }
    
    @Test
    public void testTurnOrder() {
        // Verify player goes first (due to PlayerPreferedComparator)
        EntityModel firstFighter = fightController.GetFighter(0);
        assertEquals(player, firstFighter);
        
        EntityModel secondFighter = fightController.GetFighter(1);
        assertEquals(enemy, secondFighter);
    }
    
    @Test
    public void testStunnedSkipTurn() {
        // Setup stun
        enemy.setStunned(true);
        double initialHealth = player.getHealth();
        
        // Execute enemy turn
        double damageDealt = fightController.TakeEnemyTurn(1);
        
        // Verify results
        assertEquals("Stunned enemy should deal no damage", 0.0, damageDealt, 0.001);
        assertEquals("Player health should not change", initialHealth, player.getHealth(), 0.001);
        assertFalse("Stun should be cleared after turn", enemy.isStunned());
        
        // Additional debug output
        System.out.println("Enemy stunned status: " + enemy.isStunned());
        System.out.println("Damage dealt: " + damageDealt);
        System.out.println("Player health: " + player.getHealth());
    }
    
    @Test
    public void testPoisonEffectDamage() {
        // Setup poison weapon
        Weapon dagger = new Weapon(2, "Poison Dagger", "Toxic blade", 15.0);
        dagger.SetStatusEffect("poison");
        
        // Apply effect directly for testing
        StatusEffect poison = new StatusEffect("poison", 7.5, 3, 0, false);
        poison.setActive(true);
        fightController.applyTestStatusEffect(enemy, poison);
        
        // Verify damage application
        double initialHealth = enemy.getHealth();
        fightController.applyStatusEffects(enemy);
        assertEquals("Health should reduce by poison damage", 
                     initialHealth - 7.5, 
                     enemy.getHealth(), 
                     0.001);
    }

    @Test
    public void testStunEffectBehavior() {
        // Setup stun weapon
        Weapon hammer = new Weapon(3, "Hammer", "Heavy blunt", 25.0);
        hammer.SetDamageType("blunt");
        
        // Apply stun
        fightController.TakePlayerTurn(0, 1, "Right Hand");
        
        // Verify stun
        assertTrue("Enemy should be stunned", enemy.isStunned());
        
        // Verify turn skip
        double initialHealth = player.getHealth();
        double damage = fightController.TakeEnemyTurn(1);
        assertEquals("Stunned enemy should do no damage", 0.0, damage, 0.001);
        assertEquals("Player health should not change", initialHealth, player.getHealth(), 0.001);
    }
    
    @Test
    public void testBleedEffectApplication() {
        // Use the slashing weapon (axe)
        Weapon axe = new Weapon(4, "Axe", "Sharp axe", 18.0);
        axe.SetDamageType("slashing");
        axe.SetCritChance(100);
        player.getInventory().EquipWeapon("Right Hand", axe);
        
        // Attack (should crit and apply bleed)
        fightController.TakePlayerTurn(0, 1, "Right Hand");
        
        // Verify
        StatusEffect effect = fightController.statusEffects.get(enemy.getId());
        assertNotNull("Bleed effect should be applied", effect);
        assertEquals("bleed", effect.getType());
        assertTrue("Bleed should be active", effect.isActive());
    }

    @Test
    public void testStatusEffectActivation() {
        // Use the poison dagger
        fightController.TakePlayerTurn(0, 1, "Left Hand");
        fightController.TakePlayerTurn(0, 1, "Left Hand");
        fightController.TakePlayerTurn(0, 1, "Left Hand");
        fightController.TakePlayerTurn(0, 1, "Left Hand");
        fightController.TakePlayerTurn(0, 1, "Left Hand");
        
        // Verify poison was applied and activated
        StatusEffect effect = fightController.statusEffects.get(enemy.getId());
        assertNotNull("Poison effect should exist", effect);
        assertEquals("poison", effect.getType());
        assertTrue("Poison should be active", effect.isActive());
    }

    @Test
    public void testEffectDuration() {
        // Setup paralysis effect directly
        StatusEffect paralysis = new StatusEffect("paralysis", 0, 2, 0, false);
        paralysis.setActive(true);
        fightController.statusEffects.put(enemy.getId(), paralysis);
        
        // First tick
        fightController.applyStatusEffects(enemy);
        assertTrue("Should be paralyzed (1)", enemy.isStunned());
        
        // Second tick
        fightController.applyStatusEffects(enemy);
        assertTrue("Should be paralyzed (2)", enemy.isStunned());
        
        // Third tick should clear
        fightController.applyStatusEffects(enemy);
        assertFalse("Should not be paralyzed", enemy.isStunned());
        assertNull("Effect should be removed", 
                  fightController.statusEffects.get(enemy.getId()));
    }

    @Test
    public void testStatusEffectApplication() {
        // Create burn weapon for this test
        Weapon fireSword = new Weapon(5, "Fire Sword", "Burning", 25.0);
        fireSword.SetStatusEffect("burn");
        fireSword.SetCritChance(100);
        player.getInventory().EquipWeapon("Right Hand", fireSword);
        
        // Attack
        fightController.TakePlayerTurn(0, 1, "Right Hand");
        fightController.TakePlayerTurn(0, 1, "Right Hand");
        
        // Verify
        StatusEffect effect = fightController.statusEffects.get(enemy.getId());
        assertNotNull("Burn effect should exist", effect);
        assertEquals("burn", effect.getType());
        assertTrue("Burn should be active", effect.isActive());
    }
    
}