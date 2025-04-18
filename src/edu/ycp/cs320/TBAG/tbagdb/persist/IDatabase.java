package edu.ycp.cs320.TBAG.tbagdb.persist;

import java.util.List;

import edu.ycp.cs320.TBAG.model.Item;

public interface IDatabase {
	public abstract List<Item> ItemsByNameQuery(String itemName);
}
