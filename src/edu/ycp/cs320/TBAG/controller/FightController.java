package edu.ycp.cs320.TBAG.controller;

import java.util.ArrayList;
import java.util.Collections;


import edu.ycp.cs320.TBAG.comparator.PlayerPreferedComparator;

import edu.ycp.cs320.TBAG.controller.EntityController;
import edu.ycp.cs320.TBAG.model.EntityModel;

import edu.ycp.cs320.TBAG.model.Weapon;

public class FightController {
	private ArrayList<EntityModel> fightingEntities;
	
	public FightController(ArrayList<EntityModel> fighters) {
		this.fightingEntities = fighters;
		SortByPlayerFirst();
	}
	
	public void SortByPlayerFirst() {
		Collections.sort(fightingEntities, new PlayerPreferedComparator());
	}
	
	
	private void TakeTurn(Integer entityIndex, Integer attackIndex, String weaponSlot) {
		
		// attack an entity
//		new EntityController(fightingEntities.get(attackIndex)).AddHealthClamped(...);
	}
	
//	public void TakePlayerTurn(Integer playerIndex, Integer attackIndex, String weaponSlot) {
//		
//	}
//	
//	public void TakeEnemyTurn(Weapon attackWeapon) {
//		
//	}
}
