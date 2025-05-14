package edu.ycp.cs320.TBAG.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemTest {
    private Item item;
    private Weapon weapon;

    @Before
    public void setUp() {
        item = new Item(1, "Health Potion", "Restores 10 HP");
        weapon = new Weapon(2, "Sword", "Sharp blade", 10.0);
    }

    @Test
    public void testItemCreation() {
        assertTrue(1== item.GetID());
        assertEquals("Health Potion", item.GetName());
        assertEquals("Restores 10 HP", item.GetDescription());
    }

    @Test
    public void testWeaponCreation() {
        assertTrue(2== weapon.GetID());
        assertEquals("Sword", weapon.GetName());
        assertEquals(10.0, weapon.GetDamage(), 0.001);
    }
}