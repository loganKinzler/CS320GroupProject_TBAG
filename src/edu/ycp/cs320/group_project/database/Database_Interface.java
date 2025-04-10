package edu.ycp.cs320.group_project.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

import edu.ycp.cs320.sqldemo.DBUtil;

public class Database_Interface {
	
	
   public Database_Interface() {
	   
   }
   
   public void getEntityInfoByID(int ID,Connection conn) {
	   // use the entity ID to get entity name, desc, current hp, location ID
	   PreparedStatement getentity = null;
	   try {
		   getentity = conn.prepareStatement(
				   //add sql string here
				   
				   
				   
				   
				   );
				   
	   }finally {
		   conn.commit();
	   }
   }
   public void getRoomInfoByID(int ID,Connection conn) {
	   //use the roomID to get the room name & room desc back
	   PreparedStatement getroom = null;
	   try {
		   getroom = conn.prepareStatement(
				   //add sql string here
				   
				   
				   
				   
				   );
				   
	   }finally {
		   conn.commit();
	   }
   }
   public void getRoomInhabitantByID(int ID,Connection conn) {
	   //use the roomID and search the entity list by the stuff that has that id in their table
	   PreparedStatement getInhab = null;
	   try {
		   getInhab = conn.prepareStatement(
				   //add sql string here
				   
				   
				   
				   
				   );
				   
	   }finally {
		   conn.commit();
	   }
   }
   public void getItemByID(int ID,Connection conn) {
	   // input item ID to get the name & desc of the item
	   PreparedStatement getItem = null;
	   try {
		   getItem = conn.prepareStatement(
				   //add sql string here
				   
				   
				   
				   
				   );
				   
	   }finally {
		   conn.commit();
	   }
   }
   public void getInventoryByID(int ID,Connection conn) {
	//use entity/room ID to get all the items that the ID owns
	   PreparedStatement getInventory = null;
	   try {
		   getInventory = conn.prepareStatement(
				   //add sql string here
				   
				   
				   
				   
				   );
				   
	   }finally {
		   conn.commit();
	   }
   }
   public void updateEntityLocationByID(int entityID,int roomID,Connection conn) {
	// use entity id to find the correct entity and then use the given roomID to replace the old location ID in the table
	   PreparedStatement updateLocation = null;
	   try {
		   updateLocation = conn.prepareStatement(
				   //add sql string here
				   
				   
				   
				   
				   );
				   
	   }finally {
		   conn.commit();
	   }
   }
   public void updateInventoryByID(int ownerID /*inventory*/,Connection conn) {
	   //using the owner ID and an array of item ID update the inventory for the thing
	   PreparedStatement updateInventory = null;
	   try {
		   updateInventory = conn.prepareStatement(
				   //add sql string here
				   
				   
				   
				   
				   );
				   
	   }finally {
		   conn.commit();
	   }
   }
   public void updateHealthByID(int entityID, double newHealth,Connection conn) {
	   //using entity ID overright the current health part with the newHealth being given
	   PreparedStatement updateHealth = null;
	   try {
		   updateHealth = conn.prepareStatement(
				   //add sql string here
				   
				   
				   
				   
				   );
				   
	   }finally {
		   conn.commit();
	   }
   }
   public void getConnectionsByID(int roomID,Connection conn) {
	   //input roomID to get a list of connections which includes the direction & output
	   PreparedStatement getConnections = null;
	   try {
		   getConnections = conn.prepareStatement(
				   //add sql string here
				   
				   
				   
				   
				   );
				   
	   }finally {
		   conn.commit();
	   }
   }
   public void updateconnectionbyID(int roomID,String Direction,int newOutcome,Connection conn) {
	   //given the roomID update the direction if already there, if not add it and set outcome = to newOutcome
	   PreparedStatement updateConnections = null;
	   try {
		   updateConnections = conn.prepareStatement(
				   //add sql string here
				   
				   
				   
				   
				   );
				   
	   }finally {
		   conn.commit();
	   }
   }
   
   
   
}