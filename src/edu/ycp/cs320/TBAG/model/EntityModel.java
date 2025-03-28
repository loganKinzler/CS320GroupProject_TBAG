package edu.ycp.cs320.TBAG.model;

public abstract class EntityModel {
	private double maxHealth, health;
	private int lives;
	private int currentRoomIndex;
	
	public EntityModel(double health, int lives, int currentRoomIndex) {
		this.maxHealth = health;
		this.health = health;
		this.lives = lives;
		this.currentRoomIndex = currentRoomIndex;
	}
	
	public double getHealth() {return health;}
	public void setHealth(double health) {
		if (health < 0) {
			System.out.println("health cannot be negative");
		}
		else if (health > this.maxHealth) {
			System.out.println("health cannot exceed maxHealth (" + health + ", " + this.maxHealth + ")");
		}
		else {
			this.health = health;
		}
	}
	public int getLives() {return lives;}
	public void setLives(int lives) {this.lives = lives;}
	public double getMaxHealth() {return maxHealth;}
	public void setMaxHealth(double maxHealth) {
		if (maxHealth < 0) {
			System.out.println("maxHealth cannot be negative");
		}
		else {
			this.maxHealth = maxHealth;
		}
	}
	public int getCurrentRoomIndex() {return currentRoomIndex;}
	public void setCurrentRoomIndex(int currentRoomIndex) {this.currentRoomIndex = currentRoomIndex;}
}
