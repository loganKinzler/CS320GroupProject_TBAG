package edu.ycp.cs320.TBAG.model;

import edu.ycp.cs320.TBAG.model.Item;
import java.util.HashMap;

public class RoomInventory extends Inventory {
	public RoomInventory() {super();}
	public RoomInventory(HashMap<Item, Integer> items) {super(items);}
}
