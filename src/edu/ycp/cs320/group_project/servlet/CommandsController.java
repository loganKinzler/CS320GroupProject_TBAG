package edu.ycp.cs320.group_project.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ycp.cs320.TBAG.controller.ASCIIOutput;
import edu.ycp.cs320.TBAG.controller.ConsoleInterpreter;
import edu.ycp.cs320.TBAG.controller.EntityController;
import edu.ycp.cs320.TBAG.controller.FightController;
import edu.ycp.cs320.TBAG.controller.PlayerController;
import edu.ycp.cs320.TBAG.model.Action;
import edu.ycp.cs320.TBAG.model.EnemyModel;
import edu.ycp.cs320.TBAG.model.EntityInventory;
import edu.ycp.cs320.TBAG.model.EntityModel;
import edu.ycp.cs320.TBAG.model.Inventory;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.PlayerModel;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.RoomInventory;
import edu.ycp.cs320.TBAG.model.Weapon;
import edu.ycp.cs320.TBAG.tbagdb.DBController;
import edu.ycp.cs320.TBAG.tbagdb.persist.DerbyDatabase;
import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;

public class CommandsController {
	public static String hardcodedCommands(
			GameEngineServlet servlet, 
			HttpSession session, 
			HttpServletRequest req, 
			HttpServletResponse resp, 
			IDatabase db, 
			Action userAction, 
			List<String> foundCommands,
			List<String> gameHistory, 
			ArrayList<String> params,
			int sudoStage) 
					throws ServletException, IOException {
		
		String responseOut = "";
    	
    	switch (userAction.GetMethod()) {
			//sudo rm -rf \ easter egg
    		case "sudoEasterEgg" :
    			responseOut = "Warning: executing 'rm -rf /' is extremely dangerous.<br>"
    					+ "Proceeding anyway...<br>"
    					+ "Deleting system...";
    			sudoStage = 1;
    	        session.setAttribute("sudoStage", sudoStage);
			break;
			
    		case "quit":
    			resp.sendRedirect("index");
    		break;
			
			//hake easter egg test
    		case "hakeTest" :
    			responseOut = ASCIIOutput.profAsciiEasterEgg(servlet, "hake");
    			session.setAttribute("playHakeSound", true);
    		break;
    		case "babcockTest":
    			responseOut = ASCIIOutput.profAsciiEasterEgg(servlet, "babcock");
    		break;
    		case "newSave":
    			db = new DerbyDatabase("test");
    			db.create();
    			responseOut = "Creating new save...";
    		break;
    		case "clearChat":
    			db.clearGameHistory();
    			gameHistory.clear();
    			LogsController.addToGameHistory(db, gameHistory, "Chat logs cleared...");
    		break;
    		case "showMap":
    			responseOut = MapController.modularMakeMap(db);
    			if (!foundCommands.contains("showMap")) LogsController.addToFoundCommands(db,foundCommands,"showMap");
    		break;
    		case "mirrorEasterEgg":
    			Inventory inv = db.GetPlayerInventory(); //Get player inventory
    			
    			boolean hasMirror = (inv.GetItemByName("mirror") != null);
    			boolean hasCamera = (inv.GetItemByName("camera") != null);
    			
    			System.out.println(hasMirror + ", " + hasCamera);
    			
    			String output = "You do not have the required items.";
    			if (hasMirror && hasCamera) {
    				output = ASCIIOutput.profAsciiEasterEgg(servlet, "hake");
    			}
    			responseOut = output;
    		break;
    	}
    	
    	return responseOut;
	}

	public static String moveCommand(IDatabase db, List<String> foundCommands, List<String> params, List<Room> rooms, PlayerModel player, String systemResponse) {
		String responseOut = systemResponse;
		if (!foundCommands.contains("move")) LogsController.addToFoundCommands(db,foundCommands,"move");
		
		//Integer nextRoom = rooms.nextConnection(player.getCurrentRoomIndex(),
				//params.get(0));
		//System.out.println(player.getCurrentRoomIndex());
		//boolean doesconnectionexist = rooms.get(player.getCurrentRoomIndex()).doesKeyExist(params.get(0));
		Integer nextRoom = null;
		
		
			nextRoom = rooms.get(player.getCurrentRoomIndex() - 1).getConnectedRoom(params.get(0));
			//for now the room will be null if it doesn't exist or is locked
			if(nextRoom <= 0) {
				nextRoom = null;
			}
		
		if (nextRoom == null) {
			systemResponse = String.format("The current room doesn't have a room %s of it.",
					params.get(0));
			return responseOut;
		}
		
		player.setCurrentRoomIndex(nextRoom);
		db.UpdatePlayerRoom(player.getCurrentRoomIndex());
		
		//These used to be offset by 1
		String short_description = rooms.get(nextRoom - 1).getShortRoomDescription();
		String long_description = rooms.get(nextRoom - 1).getLongRoomDescription();
		systemResponse = String.format("Moving %s...<br><br>Entered %s.<br>%s",
				params.get(0),
				short_description,
				long_description);
		
		return responseOut;
	}

	public static String useCommand(IDatabase db, List<String> foundCommands, List<String> params, List<Room> rooms, String systemResponse) {
		String responseOut = systemResponse;
		
		if (!foundCommands.contains("use")) LogsController.addToFoundCommands(db,foundCommands,"use");
		
		responseOut = String.format("Used %s...", params.get(0));
		
		return responseOut;
	}

	public static String pickupCommand(
			GameEngineServlet servlet, 
			IDatabase db, 
			List<Room> rooms, 
			List<String> foundCommands, 
			List<String> params, 
			List<String> gameHistory, 
			PlayerController player, 
			String systemResponse) {
		String responseOut = systemResponse;
		if (!foundCommands.contains("pickup")) LogsController.addToFoundCommands(db,foundCommands,"pickup");
		
		responseOut = String.format("Picking up %s...<br><br>", params.get(1));
		
		Integer pickupQuantity;
		if (params.get(0).equals("all")) pickupQuantity = Integer.MAX_VALUE;
		else pickupQuantity = Integer.parseInt(params.get(0));
		
		// pickup all items
		if (params.get(0).equals("all") && params.get(1).equals("items")) {
			
			
			Set<Item> roomInventoryKeys = new HashSet<Item>();
			roomInventoryKeys.addAll(rooms.get(player.getCurrentRoomIndex() - 1).getItems().keySet());            			
			if (roomInventoryKeys.isEmpty()){
				responseOut = String.format("This room does not contain any items to pickup.<br>");
				return responseOut;
			}
			
			for (Item roomItem : roomInventoryKeys) {
				Integer itemQuantity = player.PickUp(rooms.get(player.getCurrentRoomIndex() - 1), roomItem, pickupQuantity);
				responseOut += String.format("Picked up %d %s<br>",
    					itemQuantity, roomItem.GetName());
			}
			
			db.UpdateRoomInventory(player.getCurrentRoomIndex(), rooms.get(player.getCurrentRoomIndex() - 1).getRoomInventory());
			db.UpdatePlayerInventory(player.getInventory());
			return responseOut;
		}
		
		Item pickupItem = db.ItemsByNameQuery(params.get(1));
		if (pickupItem == null || !rooms.get(player.getCurrentRoomIndex() - 1).getRoomInventory().ContainsItem(pickupItem)) {
			responseOut = String.format("This room does not contain an item named %s.<br>",
					params.get(1));
			return responseOut;
		}
		
		Integer roomQuantity = player.PickUp(rooms.get(player.getCurrentRoomIndex() - 1), pickupItem, pickupQuantity);
		responseOut += String.format("Picked up %d %s<br>",
				roomQuantity, params.get(1));
		
		db.UpdateRoomInventory(player.getCurrentRoomIndex(), rooms.get(player.getCurrentRoomIndex() - 1).getRoomInventory());
		db.UpdatePlayerInventory(player.getInventory());
		
		return responseOut;
	}
	
	public static String dropCommand(
			IDatabase db,
			List<String> foundCommands,
			List<String> params,
			List<Room> rooms,
			PlayerController player, 
			String systemResponse
			) {
		String responseOut = systemResponse;
		
		if (!foundCommands.contains("drop")) LogsController.addToFoundCommands(db,foundCommands,"drop");
		
		responseOut = String.format("Dropping %s...<br><br>", params.get(1));
		
		Integer dropQuantity;
		if (params.get(0).equals("all")) dropQuantity = Integer.MAX_VALUE;
		else dropQuantity = Integer.parseInt(params.get(0));
		
		// pickup all items
		if (params.get(0).equals("all") && params.get(1).equals("items")) {
			
			Set<Item> playerInventoryKeys = new HashSet<Item>();
			playerInventoryKeys.addAll(player.getInventory().GetItems().keySet());
			
			if (playerInventoryKeys.isEmpty()){
				responseOut = String.format("The player does not have any items to drop.<br>");
				return responseOut;
			}
			
			for (Item playerItem : playerInventoryKeys) {
				Integer itemQuantity = player.Drop(
						rooms.get(player.getCurrentRoomIndex() - 1),
						playerItem, dropQuantity);
				
				responseOut += String.format("Dropped %d %s<br>",
    					itemQuantity, playerItem.GetName());
			}
			
			db.UpdateRoomInventory(player.getCurrentRoomIndex(), rooms.get(player.getCurrentRoomIndex() - 1).getRoomInventory());
			db.UpdatePlayerInventory(player.getInventory());
			return responseOut;
		}
		
		Item dropItem = player.getInventory().GetItemByName(params.get(1));
		if (dropItem == null) {
			responseOut = String.format("Your inventory does not contain an item named %s.<br>",
					params.get(1));
			return responseOut;
		}
		
		Integer playerQuantity = player.Drop(
				rooms.get(player.getCurrentRoomIndex() - 1),
				dropItem, dropQuantity);
		
		responseOut += String.format("Dropped %d %s<br>",
				playerQuantity, params.get(1));
		
		db.UpdateRoomInventory(player.getCurrentRoomIndex(), rooms.get(player.getCurrentRoomIndex() - 1).getRoomInventory());
		db.UpdatePlayerInventory(player.getInventory());
		
		return responseOut;
	}
	
	public static String describeCommand(
			IDatabase db,
			List<String> params,
			List<String> foundCommands,
			PlayerController player,
			List<Room> rooms,
			String systemResponse
			) {
		String responseOut = systemResponse;
		
		switch (params.get(0)) {
		case "room":
			if (!foundCommands.contains("describeGroup_room")) LogsController.addToFoundCommands(db,foundCommands,"describeGroup_room");
			if (!foundCommands.contains("describe_room")) LogsController.addToFoundCommands(db,foundCommands,"describe_room");
			
//			System.out.println(db.GetPlayer().getCurrentRoomIndex());
			responseOut = String.format("Describing room...<br><br>%s<br>%s",
					rooms.get(db.GetPlayer().getCurrentRoomIndex() - 1).getShortRoomDescription(),
					rooms.get(db.GetPlayer().getCurrentRoomIndex() - 1).getLongRoomDescription());
		break;
		
		//  [######--]
		
		case "stats":
			if (!foundCommands.contains("describeGroup_attack")) LogsController.addToFoundCommands(db,foundCommands,"attack");
			if (!foundCommands.contains("describe_stats")) LogsController.addToFoundCommands(db,foundCommands,"describe_stats");
			
			Integer healthBarSize = 10;
			Double lifeRatio = player.getHealth() / player.getMaxHealth();
			Integer healthBarLength = (int) Math.round(lifeRatio * healthBarSize);
			
			responseOut = String.format("Describing stats...<br><br>Lives: %d<br>Health: [%s%s] (%.1f / %.1f)",
					player.getLives(),
					"#".repeat(healthBarLength),
					"-".repeat(healthBarSize - healthBarLength),
					player.getHealth(),
					player.getMaxHealth());
		break;
		
		case "enemies":
			if (!foundCommands.contains("describeGroup_attack")) LogsController.addToFoundCommands(db,foundCommands,"describeGroup_attack");
			if (!foundCommands.contains("describe_enemies")) LogsController.addToFoundCommands(db,foundCommands,"describe_enemies");
			
			//TODO: Use this method once set up
//			ArrayList<EnemyModel> enemies = DBController.getEnemiesByRoomId(db, DBController.getPlayerCurrentRoom(db));
			ArrayList<EnemyModel> enemies = db.GetEnemiesInRoom(DBController.getPlayerCurrentRoom(db));
			responseOut = String.format("Describing enemies...<br><br>");
			
			// remove dead enemies
			for (int i=enemies.size()-1; i>=0; i--)
				if (enemies.get(i).getHealth() == 0)
					enemies.remove(i);
			
			// no enemies in room
			if (enemies.size() == 0) {
				responseOut += String.format("There are no enemies in this room.");
				break;
			}
			
			responseOut += String.format("Enemies in this room:");
			
			Integer enemyCount = 0;
			for (int i=0; i<enemies.size(); i++) {
				if (enemies.get(i).getHealth() == 0) continue;
				enemyCount++;
				
				healthBarSize = 10;
				lifeRatio = enemies.get(i).getHealth() / enemies.get(i).getMaxHealth();
				healthBarLength = (int) Math.round(lifeRatio * healthBarSize);
				
				responseOut += String.format("<br>&num;%d: %s<br> - Health: [%s%s] (%.1f / %.1f)<br> - %s<br>",
						enemies.size(), enemies.get(i).getName(),
						"#".repeat(healthBarLength),
						"-".repeat(healthBarSize - healthBarLength),
						enemies.get(i).getDescription());
			}
			
			// all enemies are dead
			if (enemies.size() == 0) {
				responseOut = String.format("Describing enemies...<br><br>");
				responseOut += String.format("There are no enemies in this room.");
			}
		break;
		
		case "moves":
			if (!foundCommands.contains("describeGroup_room")) LogsController.addToFoundCommands(db,foundCommands,"describeGroup_room");
			if (!foundCommands.contains("describe_moves")) LogsController.addToFoundCommands(db,foundCommands,"describe_moves");
			
			responseOut = String.format("Describing moves...<br><br>Possible moves:");
			
			List<String> roomConnections = rooms.get(player.getCurrentRoomIndex() - 1).getAllConnections();
			List<String> directions = new ArrayList<String>(Arrays.asList(new String[] {
					"East", "South", "North", "West"}));
			
			
			for (int i=0; i<roomConnections.size(); i++) {
				String direction = directions.get(i);
				String camelCaseDirection = direction.substring(0, 1).toUpperCase() + direction.substring(1).toLowerCase();
				Integer connectionID = Integer.parseInt(roomConnections.get(i));
				
				// connection doesn't exist
				if (connectionID == 0) continue;
				
//				System.out.println(String.format("%s : %d", roomConnections.get(i), connectionID));
				responseOut += String.format("<br> - %s &mdash;&mdash;&#62; %s", camelCaseDirection,
						rooms.get(connectionID - 1).getShortRoomDescription());
			}
		break;
		
		case "directions":
			if (!foundCommands.contains("describeGroup_room")) LogsController.addToFoundCommands(db,foundCommands,"describeGroup_room");
			if (!foundCommands.contains("describe_directions")) LogsController.addToFoundCommands(db,foundCommands,"describe_directions");
			
			responseOut = String.format("Describing directions...<br><br>Possible directions:<br>");
		
			for (String direction : ConsoleInterpreter.MOVE_DIRECTIONS)
				responseOut += String.format(" - %s<br>",
						direction.substring(0, 1).toUpperCase() + direction.substring(1).toLowerCase());
		break;
		
		case "inventory":
			if (!foundCommands.contains("describeGroup_items")) LogsController.addToFoundCommands(db,foundCommands,"describeGroup_items");
			if (!foundCommands.contains("describe_inventory")) LogsController.addToFoundCommands(db,foundCommands,"describe_inventory");
			
			EntityInventory playerInventory = db.GetPlayerInventory();
			HashMap<Item, Integer> playerItems = playerInventory.GetItems();
			HashMap<String, Weapon> playerEquips = playerInventory.GetWeaponsAsSlots();
			responseOut = String.format("Describing inventory...<br><br>");

			// no items in inventory
			if (playerItems.size() == 0 && playerEquips.size() == 0) {
				responseOut += String.format("There are no items in your inventory.");
				break;
			}
			
			responseOut += String.format("Items in your inventory:");
			
			for (String slot : playerEquips.keySet()) 
				responseOut += String.format("<br><br>%s: %s<br> - Damage: %.1f<br> - %s",
						slot, playerEquips.get(slot).GetName(),
						playerEquips.get(slot).GetDamage(),
						playerEquips.get(slot).GetDescription());
			
			for (Item playerItem : playerItems.keySet()) {
				responseOut += String.format("<br><br>%s: %d<br> - %s",
						playerItem.GetName(), playerItems.get(playerItem),
						playerItem.GetDescription());
				
				// if item is a weapon, also display the damage
				if (playerItem.getClass().equals(Weapon.class))
					responseOut += String.format("<br> - Damage: %.1f",
							((Weapon) playerItem).GetDamage());
			}
				
		break;
		
		case "items":
			if (!foundCommands.contains("describeGroup_items")) LogsController.addToFoundCommands(db,foundCommands,"describeGroup_items");
			if (!foundCommands.contains("describe_items")) LogsController.addToFoundCommands(db,foundCommands,"describe_items");
			
			RoomInventory roomInventory = db.GetRoomInventoryByID(player.getCurrentRoomIndex());
			HashMap<Item, Integer> roomItems = roomInventory.GetItems();
			responseOut = String.format("Describing items...<br><br>");
			
			// no enemies in room
			if (roomItems.size() == 0) {
				responseOut += String.format("There are no items in this room.");
				break;
			}
			
			responseOut += String.format("Items in this room:");
			
			for (Item roomItem : roomItems.keySet()) {
				responseOut += String.format("<br><br>%s: %d<br> - %s",
						roomItem.GetName(), roomItems.get(roomItem),
						roomItem.GetDescription());

				// if item is a weapon, also display the damage
				if (roomItem.getClass().equals(Weapon.class))
					responseOut += String.format("<br> - Damage: %.1f",
							((Weapon) roomItem).GetDamage());
			}
		break;
		
		default:
			responseOut = String.format("Cannot describe %s.",
					params.get(0));
		break;
	}
		
		return responseOut;
	}

	public static String equipCommand(
			IDatabase db,
			List<String> foundCommands,
			List<String> params,
			PlayerController player,
			String systemResponse
			) {
		String responseOut = systemResponse;
		
		if (!foundCommands.contains("equip")) LogsController.addToFoundCommands(db,foundCommands,"equip");
		
		responseOut = String.format("Equipping %s...<br><br>", params.get(0));
		
		
		Weapon equipItem = (Weapon) player.getInventory().GetWeaponByName(params.get(0));
		if (equipItem == null) {
			responseOut = String.format("The player does not have a weapon named %s.<br>",
					params.get(0));
			return responseOut;
		}
		
		String weaponSlot = "";
			for (String word : params.get(1).split(" "))
				weaponSlot += word.substring(0, 1).toUpperCase() + word.substring(1) + " ";
			weaponSlot = weaponSlot.trim();
		
		if (!EntityInventory.WeaponSlots.contains(weaponSlot)) {
			responseOut = String.format("The player does not have a weapon slot named %s.<br>",
					params.get(1));
			return responseOut;
		}
		
		player.getInventory().ExtractItem(equipItem);
		player.getInventory().EquipWeapon(weaponSlot, equipItem);
		
		responseOut += String.format("Equipped %s into %s.<br>",
				params.get(0), weaponSlot);
		
		for (Item i : player.getInventory().GetAllItems().keySet()) {
			System.out.println(i.GetName());
		}
		
		db.UpdatePlayerInventory(player.getInventory());
		
		return responseOut;
	}

	public static String unequipCommand(
			IDatabase db,
			PlayerController player,
			List<String> foundCommands,
			List<String> params,
			Map<String, Weapon> weaponSlots,
			String systemResponse
			) {
		String responseOut = systemResponse;
		
		String unequipName = "";
		
		// make camel case
		for (String word : params.get(0).split(" "))
			unequipName += word.substring(0, 1).toUpperCase() + word.substring(1) + " ";
		unequipName = unequipName.trim();
		
		// using slot name
		if (weaponSlots.containsKey(unequipName)) {
			if (!foundCommands.contains("unequip")) LogsController.addToFoundCommands(db,foundCommands,"unequip");
			if (!foundCommands.contains("unequip_slot")) LogsController.addToFoundCommands(db,foundCommands,"unequip_slot");
			
			Weapon unequippedWeapon = player.getInventory().UnequipWeaponInSlot(unequipName);
			
			responseOut = String.format("Unequipping %s from %s...<br><br>", 
					unequippedWeapon.GetName(), params.get(0));
			
			db.UpdatePlayerInventory(player.getInventory());
			return responseOut;
		}
		
		// no weapon in slot
		if (EntityInventory.WeaponSlots.contains(unequipName)) {
			if (!foundCommands.contains("unequip")) LogsController.addToFoundCommands(db,foundCommands,"unequip");
			if (!foundCommands.contains("unequip_slot")) LogsController.addToFoundCommands(db,foundCommands,"unequip_slot");
			
			responseOut = String.format("There is no weapon equipped in the %s.<br>", 
					params.get(0));
			return responseOut;
		}
		
		// name could be the name of a weapon
		for (String slotName : weaponSlots.keySet()) {
			if (weaponSlots.get(slotName).GetName().toLowerCase().equals(unequipName)) {
				unequipName = slotName;
				break;
			}
		}
		
		// using weapon name
		if (weaponSlots.containsKey(unequipName)) {
			if (!foundCommands.contains("unequip")) LogsController.addToFoundCommands(db,foundCommands,"unequip");
			if (!foundCommands.contains("unequip_weapon")) LogsController.addToFoundCommands(db,foundCommands,"unequip_weapon");
			
			Weapon unequippedWeapon = player.getInventory().UnequipWeaponInSlot(unequipName);
			
			responseOut = String.format("Unequipping %s from %s...<br><br>", 
					unequippedWeapon.GetName(), params.get(0));
			
			db.UpdatePlayerInventory(player.getInventory());
			return responseOut;
		}
		
		responseOut = String.format("There is no weapon equipped or slot named '%s'.",
				params.get(0));
		
		return responseOut;
	}
	
	public static String attackCommand(
			IDatabase db,
			Action userAction,
			List<String> foundCommands,
			List<String> params,
			Map<String, Weapon> weaponSlots,
			List<Room> rooms,
			PlayerController player,
			String systemResponse
			) {
		String responseOut = systemResponse;
		
		if (!foundCommands.contains("attack")) { 
			LogsController.addToFoundCommands(db,foundCommands,"attack");
		}
		
		EntityInventory playerInventory = db.GetPlayerInventory();
		weaponSlots = playerInventory.GetWeaponsAsSlots();
		String attackName = "";
		
		// make camel case
		for (String word : params.get(1).split(" "))
			attackName += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ";
		attackName = attackName.trim();
		
		
		// get weapon slot
		if (!weaponSlots.containsKey(attackName)) {
			if (EntityInventory.WeaponSlots.contains(attackName.toLowerCase())) {
				responseOut = String.format("There is no weapon equipped in the %s.<br>", 
					attackName);
				return responseOut;
			}
			
			// name could be the name of a weapon
			for (String slotName : weaponSlots.keySet()) {
				if (weaponSlots.get(slotName).GetName().toLowerCase().equals(attackName.toLowerCase())) {
					attackName = slotName;
					break;
				}
			}
		}
		
		if (!weaponSlots.containsKey(attackName)) {
			responseOut = String.format("There is no weapon equipped in the %s.<br>", 
					attackName);
				return responseOut;
		}
		
		//TODO: Use this method once set up
//		ArrayList<EnemyModel> roomEnemies = DBController.getEnemiesByRoomId(db, DBController.getPlayerCurrentRoom(db));
		ArrayList<EnemyModel> roomEnemies = db.GetEnemiesInRoom(player.getCurrentRoomIndex());
		ArrayList<EntityModel> fighters = new ArrayList<EntityModel>();
		fighters.add(db.GetPlayer());
		fighters.addAll(roomEnemies);

		Integer attackIndex = -1;         			
		try {
			attackIndex = Integer.parseInt(params.get(0));
		} catch (Exception e) {
			
			// index might be the name of an enemy
			for (EnemyModel enemy : roomEnemies) 
				if (enemy.getName().toLowerCase().equals(params.get(0).toLowerCase()))
					attackIndex = fighters.indexOf(enemy);
		}
		
		if (attackIndex == -1) {
			responseOut = String.format("There is no enemy with index or name of %s.<br>", 
					params.get(0));
			return responseOut;
		}
		
		
		FightController fightController = new FightController(fighters, db);
		if (fightController.GetFighter(attackIndex).getHealth() == 0) {
			responseOut = String.format("There is no enemy with index or name of %s.<br>", 
					params.get(0));
			return responseOut;
		}

		String attackedName = ((EnemyModel) fightController.GetFighter(attackIndex)).getName();
		
		Double attackDamage = fightController.TakePlayerTurn(0, attackIndex, attackName);
		responseOut = String.format("Attacked %s with %s.<br><br>%s took %.1f damage.<br>",
				attackedName,
				userAction.GetParams().get(1),
				attackedName,
				attackDamage
				);
		
		if (fightController.GetFighter(attackIndex).getHealth() == 0) {
			new EntityController(fightController.GetFighter(attackIndex)).Die(
					db, rooms.get(player.getCurrentRoomIndex() - 1));
			
			responseOut += String.format("%s has died.<br>",
					attackedName);
		}
		
		for (int i=0; i<roomEnemies.size(); i++) {
			if (i != 0) responseOut += "<br>";
			
			if (player.getHealth() == 0) {
				player.Die(db, rooms.get(player.getCurrentRoomIndex() - 1));
				break;
			}
			
			if (roomEnemies.get(i).getHealth() == 0) continue;
			Double enemyDamage = fightController.TakeEnemyTurn(i + 1);
			
			if (enemyDamage == 0) continue;
			responseOut += String.format("%s attacked. You took %.1f damage.",
					((EnemyModel) fightController.GetFighter(i + 1)).getName(),
					enemyDamage);
		}
		
		return responseOut;
	}
}