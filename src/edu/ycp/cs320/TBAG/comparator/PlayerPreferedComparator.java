package edu.ycp.cs320.TBAG.comparator;

import java.util.Comparator;

import edu.ycp.cs320.TBAG.model.EntityModel;
import edu.ycp.cs320.TBAG.model.PlayerModel;

public class PlayerPreferedComparator implements Comparator<EntityModel> {

	@Override
	public int compare(EntityModel entity1, EntityModel entity2) {
		Boolean firstIsPlayer = entity1.getClass().equals( PlayerModel.class );
		Boolean secondIsPlayer = entity2.getClass().equals( PlayerModel.class );
		
		// both are Players (equal)
		if (firstIsPlayer && secondIsPlayer) return 0;
		
		// first is a Player (greater than)
		if (firstIsPlayer && !secondIsPlayer) return -1;
		
		// second is a Player (less than)
		if (!firstIsPlayer && secondIsPlayer) return 1;
		
		// both are not Players (equal)
		return 0;
	}
}
