package edu.ycp.cs320.TBAG.comparator;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import edu.ycp.cs320.TBAG.model.EntityModel;
import edu.ycp.cs320.TBAG.model.PlayerModel;
import edu.ycp.cs320.TBAG.model.EnemyModel;


public class PlayerPreferedComparatorTest{
	private PlayerPreferedComparator comparator;
	private EntityModel firstEntity, secondEntity;
	
	@Before
	public void SetUp() {
		this.comparator = new PlayerPreferedComparator();
		this.firstEntity = new PlayerModel(50, 2, 4);
		this.secondEntity = new EnemyModel(30, 1, 2, "Bat", "SQUEEK!");
	}

	@Test
	public void PlayerPreferedTest() {
		
		// player > enemy
		assertEquals(this.comparator.compare(firstEntity, secondEntity), -1);
		
		// player = player
		this.secondEntity = new PlayerModel(75, 4, 1);
		assertEquals(this.comparator.compare(firstEntity, secondEntity), 0);
		
		// enemy < player
		this.firstEntity = new EnemyModel(5, 1, 0, "Chair", "It's just sitting there! Menecingly!");
		System.out.println(this.comparator.compare(firstEntity, secondEntity));
		assertEquals(this.comparator.compare(firstEntity, secondEntity), 1);
		
		// enemy = enemy
		this.secondEntity = new EnemyModel(500, 4, 14, "Light", "On. Off. On. Off...");
		assertEquals(this.comparator.compare(firstEntity, secondEntity), 0);
	}
}