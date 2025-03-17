package edu.ycp.cs320.TBAG.model;

public class Weapon extends Item {
	
	// vars
	protected Double damage;
	
	
	// contstructors
	public Weapon(String name, String description, Double damage) {
		super(name, description);
		this.damage = damage;
	}
	
	
	// getters / setters
	public Double GetDamage() {return this.damage;}
	public void SetDamage(Double damage) {this.damage = damage;}
	
	public Boolean equals(Weapon weapon) {
		return super.equals(weapon) && this.damage == weapon.GetDamage();
	}
}
