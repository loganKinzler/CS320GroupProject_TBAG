package edu.ycp.cs320.group_project.servlet;

import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.tbagdb.DBController;
import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;

public class MapController {
	
	public String showHardcodedMapString() {
    	String mapString = 
    			  "        [L]                                    <br>" //1
    	    	+ "         |                                     <br>" //vertical connection
    	    	+ "    [L]–[H]–[L]                 [E]            <br>" //2
    	    	+ "         |                       |             <br>" //vertical connection
    	    	+ "    [E]–[H]–[E]–[G]     [M]     [H] [M]–[H]–[e]<br>" //3
    	    	+ "     |   v       |       |       |   |         <br>" //vertical connection
    	    	+ "[L]–[H]–[S] [L]–[H]–[b]–[H]–[E]–[H]–[L]        <br>" //4
    	    	+ "         |           |           |   v         <br>" //vertical connection
    	    	+ "        [e]         [H]         [G]–[H]        <br>" //5
    	    	+ "                                 |   ^         <br>" //vertical connection
    	    	+ "                                [E]–[L]        <br>" //6
    	    	+ "                                 |             <br>" //vertical connection
    	    	+ "                                [b]            <br>" //7
    	    	+ "                                 |             <br>" //vertical connection
    	    	+ "                                [C]            <br>"; //8
    	mapString = mapString.replace(" ", "&nbsp"); 	
    	String toOut =
    	  "<p class=\"map-string\">"
    	+ mapString
    	+ "</p>"; 
    	
    	return toOut;
    }
	@SuppressWarnings("unchecked")
	public static String modularMakeMap(IDatabase db) {
    	
    	//Get all rooms, get max x and y, make 2d array of strings with that
    	//Get all found rooms, use their x and y to put them in proper places in 2d array
    	//Fill rest with empty
    	
    	//Get all rooms and get the max x and y to know array size
    	List<Room> rooms = db.getRooms();
    	int[] dim = getRoomsDims(rooms);
    	int[] mins = getRoomsMins(rooms);
    	
    	//ArrayList of strings. each string is horizontal, each entry is vertical
    	ArrayList<ArrayList<String>> map = new ArrayList<>();
    	for (int i = 0; i < dim[1]; i++) {
    		ArrayList<String> toAdd = new ArrayList<>();
    		for (int j = 0; j <= dim[0]; j++) {
    			toAdd.add("   ");
    			if (j < dim[0]) toAdd.add("   ");
    		}
    		map.add(toAdd);
    		
    		if (i < dim[1]) {
    			ArrayList<String> connLine = (ArrayList<String>) toAdd.clone();
    			map.add(connLine);
    		}
    	}
    	
    	List<Room> filteredRooms = filterEnteredRooms(rooms);
    	
    	//Put rooms in
    	for (Room room : filteredRooms) {
    		if (room.getHas_Entered_Room()) {
    			int yOffset = room.getY_Position() - mins[1];
    			int yPos = dim[1] - yOffset;
        		map.get(2 * yPos).set(2 * room.getX_Position(), "[ ]");
        		
        		if (DBController.getPlayerCurrentRoom(db) == room.getRoomId()) {
        			map.get(2 * yPos).set(2 * room.getX_Position(), "░░░");
        		}
    		}
    		
    	}
    	
    	//Put connections in
    	for (Room room : filteredRooms) {
    		int[] conns = db.getConnectionsByRoomId(room.getRoomId()).getAllConnectionsInt();
    		
    		
    		if (conns[0] != 0) {
    			int yOffset = room.getY_Position() - mins[1];
    			int yPos = dim[1] - yOffset;
    			
    			map.get(2 * yPos - 1).set(2 * room.getX_Position(), " | ");
    			
    			List<Room> destArr = db.RoomsByIdQuery(conns[0]);
    			
    			if (destArr.size() == 1) {
    				Room dest = destArr.get(0);
    				if (!dest.getHas_Entered_Room()) {
    					yOffset = dest.getY_Position() - mins[1];
    	    			yPos = dim[1] - yOffset;
    	        		map.get(2 * yPos).set(2 * dest.getX_Position(), "___");
    				}
    			}
    		}
    		if (conns[2] != 0) {
    			int yOffset = room.getY_Position() - mins[1];
    			int yPos = dim[1] - yOffset;
    			
    			map.get(2 * yPos + 1).set(2 * room.getX_Position(), " | ");
    			
    			List<Room> destArr = db.RoomsByIdQuery(conns[2]);
    			
    			if (destArr.size() == 1) {
    				Room dest = destArr.get(0);
    				if (!dest.getHas_Entered_Room()) {
    					yOffset = dest.getY_Position() - mins[1];
    	    			yPos = dim[1] - yOffset;
    	        		map.get(2 * yPos).set(2 * dest.getX_Position(), "___");
    				}
    			}
    		}
    		if (conns[1] != 0) {
    			int yOffset = room.getY_Position() - mins[1];
    			int yPos = dim[1] - yOffset;
    			
    			map.get(2 * yPos).set(2 * room.getX_Position() + 1, "–––");
    			
    			List<Room> destArr = db.RoomsByIdQuery(conns[1]);
    			
    			if (destArr.size() == 1) {
    				Room dest = destArr.get(0);
    				if (!dest.getHas_Entered_Room()) {
    					yOffset = dest.getY_Position() - mins[1];
    	    			yPos = dim[1] - yOffset;
    	        		map.get(2 * yPos).set(2 * dest.getX_Position(), "___");
    				}
    			}
    		}

    		if (conns[3] != 0) {
    			int yOffset = room.getY_Position() - mins[1];
    			int yPos = dim[1] - yOffset;
    			
    			map.get(2 * yPos).set(2 * room.getX_Position() - 1, "–––");
    			
    			List<Room> destArr = db.RoomsByIdQuery(conns[3]);
    			
    			if (destArr.size() == 1) {
    				Room dest = destArr.get(0);
    				if (!dest.getHas_Entered_Room()) {
    					yOffset = dest.getY_Position() - mins[1];
    	    			yPos = dim[1] - yOffset;
    	        		map.get(2 * yPos).set(2 * dest.getX_Position(), "___");
    				}
    			}
    		}
    	}
    	
    	map = removeBlankLists(map);
    	
    	String mapString = "";
    	
    	for (ArrayList<String> arr : map) {
    		for (String str : arr) {
    			mapString += str;
    		}
    		
    		mapString += "<br>";
    	}
    	
    	mapString = mapString.replace(" ", "&nbsp"); 
    	
    	String toOut =
		  "<p class=\"map-string\">"
    	+ mapString
		+ "</p>"; 
    	
    	return toOut;
    }
    
    public static int[] getRoomsDims(List<Room> rooms) {
    	int[] maxes = {0,0};
    	int[] mins = {0,0};
    	int[] dims = new int[2];
    	
    	for (Room room : rooms) {
    		//Set maxes
    		if (room.getX_Position() > maxes[0]) maxes[0] = room.getX_Position();
    		if (room.getY_Position() > maxes[1]) maxes[1] = room.getY_Position();
    		//Set mins
    		if (room.getX_Position() < mins[0]) mins[0] = room.getX_Position();
    		if (room.getY_Position() < mins[1]) mins[1] = room.getY_Position();
    	}
    	
    	dims[0] = maxes[0] - mins[0];
    	dims[1] = maxes[1] - mins[1];
    	
    	return dims;
    }
    
    public static int[] getRoomsMins(List<Room> rooms) {
    	int[] mins = {0,0};
    	
    	for (Room room : rooms) {
    		if (room.getX_Position() < mins[0]) mins[0] = room.getX_Position();
    		if (room.getY_Position() < mins[1]) mins[1] = room.getY_Position();
    	}
    	
    	return mins;
    }
    
    //Code generated by chatgpt (i coulnt be bothered)
    public static ArrayList<ArrayList<String>> removeBlankLists(ArrayList<ArrayList<String>> input) {
        ArrayList<ArrayList<String>> result = new ArrayList<>();

        for (ArrayList<String> inner : input) {
            boolean onlyBlanks = true;
            for (String s : inner) {
                if (!s.equals("   ")) {
                    onlyBlanks = false;
                    break;
                }
            }
            if (!onlyBlanks) {
                result.add(inner);
            }
        }

        return result;
    }
    
    public static List<Room> filterEnteredRooms(List<Room> rooms) {
        List<Room> filtered = new ArrayList<>();

        for (Room room : rooms) {
            if (room.getHas_Entered_Room()) {
                filtered.add(room);
            }
        }

        return filtered;
    }
}
