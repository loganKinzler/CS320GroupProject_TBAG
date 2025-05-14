package edu.ycp.cs320.TBAG.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class EntityInventory extends Inventory {
	public static final List<String> WeaponSlots = new ArrayList<String>(Arrays.asList(new String[]{
		"Left Hand", "Right Hand"}));
	
	//vars
	private HashMap<String, Weapon> weapons;
	
	
	// constructors
	public EntityInventory() {
		super();
		this.weapons = new HashMap<String, Weapon>();
	}
	
	public EntityInventory(HashMap<Item, Integer> items) {
		super(items);
		this.weapons = new HashMap<String, Weapon>();
	}
	
	public EntityInventory(HashMap<Item, Integer> items, HashMap<String, Weapon> weapons) {
		super(items);
		this.weapons = weapons;
	}
	
	
	// getters / setters
	public HashMap<String, Weapon> GetWeaponsAsSlots() {return this.weapons;}
	
	public void EquipWeapon(String weaponSlot, Weapon weapon) {
	    if (!WeaponSlots.contains(weaponSlot)) {
	        throw new IllegalArgumentException("Invalid weapon slot: " + weaponSlot);
	    }
	    this.weapons.put(weaponSlot, weapon);
	}

	public Weapon GetWeapon(String weaponSlot) {
	    if (!WeaponSlots.contains(weaponSlot)) {
	        throw new IllegalArgumentException("Invalid weapon slot: " + weaponSlot);
	    }
	    return this.weapons.get(weaponSlot);
	}
	
	public Weapon DropWeaponInSlot(String weaponSlot) {return this.weapons.remove(weaponSlot);}

	public Weapon UnequipWeaponInSlot(String weaponSlot) {
		Weapon weapon = this.weapons.remove(weaponSlot);
		this.AddItem(weapon);
		return weapon;
	}
	
	public Boolean WeaponIsEquiped(Weapon weapon) {return this.weapons.containsValue( weapon );}
	public Boolean WeaponIsEquippedInSlot(String weaponSlot, Weapon weapon) {return this.weapons.get(weaponSlot).equals(weapon);}
	public Boolean WeaponSlotIsFull(String weaponSlot) {return this.weapons.containsKey( weaponSlot );}
	public Boolean WeaponSlotIsEmpty(String weaponSlot) {return !this.WeaponSlotIsFull( weaponSlot );}
	
	public HashMap<Item, Integer> GetAllItems() {
		
		@SuppressWarnings("unchecked")
		HashMap<Item, Integer> totalItems = (HashMap<Item, Integer>) this.items.clone();
		
		// add weapons in their slots to total items
		for (String slot : this.weapons.keySet()) {
			Weapon weapon = this.weapons.get(slot);
			Integer currentCount = totalItems.get(weapon);
			
			if (currentCount == null) continue;
			totalItems.put(weapon, currentCount + 1);
		}
			
		return totalItems;
	}
	
	@Override public Boolean ContainsMoreThan(Item item, Integer itemAmount) {return this.GetItemAmount(item) > itemAmount;}
	@Override public Boolean ContainsExactly(Item item, Integer itemAmount) {return this.GetItemAmount(item) == itemAmount;}
	@Override public Boolean ContainsLessThan(Item item, Integer itemAmount) {return this.GetItemAmount(item) < itemAmount;}

	
	@Override
	public boolean equals(Object obj) {
		EntityInventory inv = null;
		
		try {inv = (EntityInventory) obj;}
		catch (Exception e) {return false;}
		
		for (String slot : inv.GetWeaponsAsSlots().keySet()) {
			Weapon weaponInSlot = inv.GetWeapon(slot);
			if (!this.WeaponIsEquippedInSlot(slot, weaponInSlot)) return false;
		}
		
		return super.equals(obj);
	}
}
