package edu.ycp.cs320.TBAG.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.lang.Math;

import edu.ycp.cs320.TBAG.comparator.PlayerPreferedComparator;

import edu.ycp.cs320.TBAG.controller.EntityController;
import edu.ycp.cs320.TBAG.model.EntityModel;

import edu.ycp.cs320.TBAG.model.Weapon;

public class FightController {
	private ArrayList<EntityModel> fightingEntities;
	
	public FightController(ArrayList<EntityModel> fighters) {
		this.fightingEntities = fighters;
		Collections.sort(fightingEntities, new PlayerPreferedComparator());
	}
	
	public EntityModel GetFighter(Integer index) {return fightingEntities.get(index);}
	
	private Double TakeTurn(Integer entityIndex, Integer attackIndex, String weaponSlot) {
		new EntityController(fightingEntities.get(attackIndex)).AddHealthClamped(
				-fightingEntities.get(entityIndex).getInventory().GetWeapon(weaponSlot).GetDamage()
		);
		
		return Math.min(fightingEntities.get(entityIndex).getInventory().GetWeapon(weaponSlot).GetDamage(),
				fightingEntities.get(entityIndex).getHealth());
	}
	
	public Double TakePlayerTurn(Integer entityIndex, Integer attackIndex, String weaponSlot) {
		return TakeTurn(entityIndex, attackIndex, weaponSlot);
	}
	
	public Double TakeEnemyTurn(Integer enemyIndex) {
		Set<String> enemyWeaponSlots = fightingEntities.get(enemyIndex).getInventory().GetWeaponsAsSlots().keySet();
		if (enemyWeaponSlots.size() == 0) return 0.0;
		
		Integer slotIndex = (int) (enemyWeaponSlots.size() * Math.random());
		String randomWeaponSlot = (String) enemyWeaponSlots.toArray()[slotIndex];
		
		return TakeTurn(enemyIndex, 0, randomWeaponSlot);
	}
}
