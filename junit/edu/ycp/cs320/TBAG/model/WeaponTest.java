package edu.ycp.cs320.TBAG.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class WeaponTest {
    private Weapon weapon;

    @Before
    public void setUp() {
        weapon = new Weapon(1, "Axe", "Heavy chopping weapon", 12.0);
    }

    @Test
    public void testWeaponProperties() {
        assertTrue(1 == weapon.GetID());  // Changed to assertTrue for ID
        assertEquals("Axe", weapon.GetName());
        assertEquals(12.0, weapon.GetDamage(), 0.001);
    }

    @Test
    public void testDamageModification() {
        weapon.SetDamage(15.0);
        assertEquals(15.0, weapon.GetDamage(), 0.001);
    }
}