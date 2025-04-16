package edu.ycp.cs320.TBAG.tbagdb.persist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.tbagdb.persist.ReadCSV;

public class InitialData {
	public static List<Item> getItemTypes() throws IOException {
		List<Item> itemTypesList = new ArrayList<Item>();
		ReadCSV readItemTypes = new ReadCSV("itemTypes.csv");
		
		try {
			while (true) {
				List<String> tuple = readItemTypes.next();
				if (tuple == null) break;
				
				Iterator<String> i = tuple.iterator();
				Item item = new Item(i.next(), i.next());
				itemTypesList.add(item);
			}
			
			return itemTypesList;
		}
		finally {
			readItemTypes.close();
		}
	}
}
