package edu.ycp.cs320.TBAG.model;

import java.util.HashMap;

public class EntityInventory extends Inventory {

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
		this.weapons = new HashMap<String, Weapon>();
	}
	
	
	// getters / setters
	public HashMap<String, Weapon> GetWeapons() {return this.weapons;}
	public Weapon GetWeapon(String weaponSlot) {return this.weapons.get(weaponSlot);}
	
	public void EquipWeapon(String weaponSlot, Weapon weapon) {this.weapons.put(weaponSlot, weapon);}
	public void DropWeapon(String weaponSlot) {this.weapons.remove(weaponSlot);}
}
