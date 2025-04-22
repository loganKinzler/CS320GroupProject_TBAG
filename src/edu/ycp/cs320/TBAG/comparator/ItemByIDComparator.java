package edu.ycp.cs320.TBAG.comparator;

import java.util.Comparator;

import edu.ycp.cs320.TBAG.model.Item;

public class ItemByIDComparator implements Comparator<Item> {

	@Override
	public int compare(Item item1, Item item2) {
		return item1.GetID().compareTo( item2.GetID() );
	}
}
