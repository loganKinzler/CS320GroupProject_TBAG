package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.EntityModel;
import edu.ycp.cs320.TBAG.model.PlayerModel;

public class PlayerController extends EntityController {
	private PlayerModel model;
	
	public PlayerController(EntityModel model) {
		super(model);
	}
	public PlayerController() {
		super();
	}
	
	public void Respawn() {
		if (this.model.getLives() > 0) {
			//TODO fill this in
			//Put position in spawnroom
		}
	}

}
