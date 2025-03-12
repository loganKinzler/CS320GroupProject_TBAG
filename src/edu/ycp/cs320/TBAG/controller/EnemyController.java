package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.EnemyModel;
import edu.ycp.cs320.TBAG.model.EntityModel;

public class EnemyController extends EntityController {
	private EnemyModel model;
	
	public EnemyController(EntityModel model) {
		super(model);
	}
	public EnemyController() {
		super();
	}
}
