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
	public HashMap<String, Weapon> GetWeaponsAsSlots() {return this.weapons;}
	public Weapon GetWeapon(String weaponSlot) {return this.weapons.get(weaponSlot);}
	
	public void EquipWeapon(String weaponSlot, Weapon weapon) {this.weapons.put(weaponSlot, weapon);}
	public Weapon DropWeaponInSlot(String weaponSlot) {return this.weapons.remove(weaponSlot);}
	
	public Boolean WeaponIsEquiped(Weapon weapon) {return this.weapons.containsValue( weapon );}
	public Boolean WeaponSlotIsFull(String weaponSlot) {return this.weapons.containsKey( weaponSlot );}
	public Boolean WeaponSlotIsEmpty(String weaponSlot) {return !this.WeaponSlotIsFull( weaponSlot );}
	
	@Override
	public Boolean ContainsItem(Item item) {
		if (item.getClass() == Item.class) return super.ContainsItem(item);// item is a base item : only check inventory
		if (this.WeaponIsEquiped((Weapon) item)) return true;// weapon could potentially be equipped
		return this.items.containsKey(item);// weapon could potentially be in regular inventory
	}
	
	@Override
	public Integer GetItemAmount(Item item) {
		Integer quantity = super.GetItemAmount(item);// inventory item amount
		if (item.getClass() == Item.class) return quantity;// item is a base item : only in regular inventory
		
		for (String slot : this.weapons.keySet())// run through weapons and count instances
			quantity += this.weapons.get(slot).equals((Weapon) item)? 1:0;// weapon is in slot : +1
		
		return quantity;// total is weapon slot amount + inventory amount
	}
	
	@Override public Boolean ContainsMoreThan(Item item, Integer itemAmount) {return this.GetItemAmount(item) > itemAmount;}
	@Override public Boolean ContainsExactly(Item item, Integer itemAmount) {return this.GetItemAmount(item) == itemAmount;}
	@Override public Boolean ContainsLessThan(Item item, Integer itemAmount) {return this.GetItemAmount(item) < itemAmount;}
}
