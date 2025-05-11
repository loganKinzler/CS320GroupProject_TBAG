package edu.ycp.cs320.TBAG.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import edu.ycp.cs320.TBAG.controller.FightController;
import edu.ycp.cs320.TBAG.tbagdb.persist.DerbyDatabase;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FightControllerTest {
    private DerbyDatabase db;
    private FightController fightController;
    private PlayerModel player;
    private EnemyModel enemy;
    private final int PLAYER_INDEX = 0;
    private final int ENEMY_INDEX = 1;
    
    @Before
    public void setUp() {
        try {
            // Initialize in-memory Derby database
            db = new DerbyDatabase("memory:testdb");
            db.createTables();
            
            // Create test data using direct JDBC
            Connection conn = db.connect();
            try {
                // Create player
                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO entities (id, health, maxHealth, lives, currentRoom) VALUES (1, 100.0, 100.0, 3, 0)");
                stmt.executeUpdate();
                
                // Create enemy
                stmt = conn.prepareStatement(
                    "INSERT INTO entities (id, health, maxHealth, lives, currentRoom, name, description) " +
                    "VALUES (2, 50.0, 50.0, 1, 1, 'Goblin', 'A nasty creature')");
                stmt.executeUpdate();
                
                conn.commit();
            } finally {
                conn.close();
            }
            
            // Create model objects
            player = new PlayerModel(100.0, 3, 0);
            enemy = new EnemyModel(50.0, 1, 1, "Goblin", "A nasty creature");
            enemy.setId(2);
            
            // Setup weapons
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
            fightController = new FightController(fighters, db);
            
        } catch (Exception e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }
    
    @After
    public void tearDown() {
        try {
            // Clean up database
            Connection conn = db.connect();
            try {
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM entities WHERE id > 1");
                stmt.executeUpdate();
                conn.commit();
            } finally {
                conn.close();
            }
            
            // Shutdown Derby
            db.deleteDb("memory:testdb", null);
        } catch (Exception e) {
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }

    // Combat mechanics tests
    @Test
    public void testStunEffectApplication() {
        fightController.takePlayerTurn(PLAYER_INDEX, ENEMY_INDEX, "Right Hand");
        assertTrue(enemy.isStunned());
        assertTrue(enemy.hasEffect("stun"));
        assertEquals(1, enemy.getEffect("stun").getDuration());
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
        assertTrue(enemy.isPoisoned());
        assertEquals(3, enemy.getEffect("poison").getDuration());
        
        double initialHealth = enemy.getHealth();
        fightController.processStatusEffects(enemy);
        assertEquals(initialHealth - 3.0, enemy.getHealth(), 0.001);
    }
    
    @Test
    public void testBleedEffect() {
        fightController.takeEnemyTurn(ENEMY_INDEX, PLAYER_INDEX);
        assertTrue(player.isBleeding());
        assertEquals(3, player.getEffect("bleed").getDuration());
    }
    
    @Test
    public void testBurnEffect() {
        Weapon fireSword = new Weapon(4, "Fire Sword", "Burning", 20.0);
        fireSword.SetStatusEffect("burn");
        player.getInventory().EquipWeapon("Right Hand", fireSword);
        
        fightController.takePlayerTurn(PLAYER_INDEX, ENEMY_INDEX, "Right Hand");
        assertTrue(enemy.isOnFire());
        assertEquals(2, enemy.getEffect("burn").getDuration());
    }
    
    @Test
    public void testEffectStacking() {
        StatusEffect poison1 = new StatusEffect("poison", 2, 3.0);
        StatusEffect poison2 = new StatusEffect("poison", 3, 5.0);
        
        enemy.addEffect(poison1);
        enemy.addEffect(poison2);
        
        assertEquals(1, enemy.getActiveEffects().size());
        assertEquals(5, enemy.getEffect("poison").getDuration());
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
        enemy.addEffect(new StatusEffect("poison", 2, 3.0));
        enemy.addEffect(new StatusEffect("burn", 1, 5.0));
        
        double initialHealth = enemy.getHealth();
        fightController.processStatusEffects(enemy);
        
        assertEquals(initialHealth - 8.0, enemy.getHealth(), 0.001);
        assertTrue(enemy.isPoisoned());
        assertTrue(enemy.isOnFire());
    }

    // Database integration tests
    @Test
    public void testUpdatePlayerInDatabase() {
        player.setHealth(80.0);
        player.setMaxHealth(120.0);
        player.setLives(2);
        
        fightController.updatePlayerInDatabase(player);
        
        try (Connection conn = db.connect();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT health, maxHealth, lives FROM entities WHERE id = 1")) {
            
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(80.0, rs.getDouble(1), 0.001);
            assertEquals(120.0, rs.getDouble(2), 0.001);
            assertEquals(2, rs.getInt(3));
        } catch (SQLException e) {
            fail("Database verification failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testUpdateEnemyInDatabase() {
        enemy.setHealth(30.0);
        enemy.setMaxHealth(60.0);
        enemy.setLives(0);
        
        fightController.updateEnemyInDatabase(enemy);
        
        try (Connection conn = db.connect();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT health, maxHealth, lives FROM entities WHERE id = 2")) {
            
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(30.0, rs.getDouble(1), 0.001);
            assertEquals(60.0, rs.getDouble(2), 0.001);
            assertEquals(0, rs.getInt(3));
        } catch (SQLException e) {
            fail("Database verification failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testEndCombatRound() {
        player.setHealth(90.0);
        enemy.setHealth(40.0);
        
        fightController.endCombatRound();
        
        try (Connection conn = db.connect()) {
            // Verify player
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT health FROM entities WHERE id = 1");
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(90.0, rs.getDouble(1), 0.001);
            
            // Verify enemy
            stmt = conn.prepareStatement(
                "SELECT health FROM entities WHERE id = 2");
            rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(40.0, rs.getDouble(1), 0.001);
        } catch (SQLException e) {
            fail("Database verification failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testProcessDefeatedEnemies() {
        enemy.setHealth(0);
        
        List<Integer> defeated = fightController.processDefeatedEnemies();
        
        assertEquals(1, defeated.size());
        assertEquals(Integer.valueOf(2), defeated.get(0));
        
        try (Connection conn = db.connect();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT currentRoom FROM entities WHERE id = 2")) {
            
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(-1, rs.getInt(1));
        } catch (SQLException e) {
            fail("Database verification failed: " + e.getMessage());
        }
        
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