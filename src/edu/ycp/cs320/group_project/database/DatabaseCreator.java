package edu.ycp.cs320.group_project.database;

import java.sql.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DatabaseCreator {
    private static final String DB_URL = "jdbc:derby:gameDB;create=true";
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    
    // CSV file paths : need to create the csv's rn
    private static final Map<String, String> CSV_FILES;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("Rooms", "rooms.csv");
        map.put("RoomConnections", "room_connections.csv");
        map.put("Items", "items.csv");
        map.put("Weapons", "weapons.csv");
        map.put("BaseEntities", "base_entities.csv");
        map.put("Entity", "entities.csv");
        map.put("Inventory", "inventory.csv");
        CSV_FILES = Collections.unmodifiableMap(map);
    }

    
    public static void createDatabase() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // Load the Derby driver
            Class.forName(DRIVER);
            
            // Create connection to Derby database
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            
            // Create all tables
            createRoomsTable(stmt);
            createItemsTable(stmt);
            createWeaponsTable(stmt);
            createBaseEntitiesTable(stmt);
            createEntityTable(stmt);
            createRoomConnectionsTable(stmt);
            createInventoryTable(stmt);
            
            // Load data from CSV files
            loadInitialData(conn);
            
            System.out.println("Database created and initialized successfully.");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Derby driver not found.");
            e.printStackTrace();
        } catch (SQLException | IOException e) {
            handleException(e);
        } finally {
            closeResources(stmt, conn);
        }
    }
    
    // Table creation methods 
    private static void createRoomsTable(Statement stmt) throws SQLException {
        stmt.executeUpdate("CREATE TABLE Rooms (" +
            "RoomID INT PRIMARY KEY, " +
            "RoomName VARCHAR(255), " +
            "RoomDescription VARCHAR(1000))");
    }
    
    private static void createRoomConnectionsTable(Statement stmt) throws SQLException {
        stmt.executeUpdate("CREATE TABLE RoomConnections (" +
            "RoomID INT, " +
            "Direction VARCHAR(50), " +
            "OutcomeID INT, " +
            "PRIMARY KEY (RoomID, Direction), " +
            "FOREIGN KEY (RoomID) REFERENCES Rooms(RoomID), " +
            "FOREIGN KEY (OutcomeID) REFERENCES Rooms(RoomID))");
    }
    
    private static void createItemsTable(Statement stmt) throws SQLException {
        stmt.executeUpdate("CREATE TABLE Items (" +
            "ItemID INT PRIMARY KEY, " +
            "ItemName VARCHAR(255), " +
            "ItemDescription VARCHAR(1000))");
    }
    
    private static void createWeaponsTable(Statement stmt) throws SQLException {
        stmt.executeUpdate("CREATE TABLE Weapons (" +
            "ItemID INT PRIMARY KEY, " +
            "Damage DOUBLE, " +
            "AttackName VARCHAR(255), " +
            "AttackDescription VARCHAR(1000), " +
            "FOREIGN KEY (ItemID) REFERENCES Items(ItemID))");
    }
    
    private static void createBaseEntitiesTable(Statement stmt) throws SQLException {
        stmt.executeUpdate("CREATE TABLE BaseEntities (" +
            "EntityID INT PRIMARY KEY, " +
            "EntityName VARCHAR(255), " +
            "EntityDescription VARCHAR(1000), " +
            "EntityMaxHealth DOUBLE, " +
            "MaxLives INT)");
    }
    
    private static void createEntityTable(Statement stmt) throws SQLException {
        stmt.executeUpdate("CREATE TABLE Entity (" +
            "EntityID INT PRIMARY KEY, " +
            "EntityCurrentHP DOUBLE, " +
            "WeaponInventoryID INT, " +
            "EntityLocation INT, " +
            "CurrentLife INT, " +
            "FOREIGN KEY (EntityID) REFERENCES BaseEntities(EntityID), " +
            "FOREIGN KEY (WeaponInventoryID) REFERENCES Items(ItemID), " +
            "FOREIGN KEY (EntityLocation) REFERENCES Rooms(RoomID))");
    }
    
    private static void createInventoryTable(Statement stmt) throws SQLException {
        stmt.executeUpdate("CREATE TABLE Inventory (" +
            "OwnerID INT, " +
            "ItemID INT, " +
            "PRIMARY KEY (OwnerID, ItemID), " +
            "FOREIGN KEY (ItemID) REFERENCES Items(ItemID))");
    }
    
    private static void loadInitialData(Connection conn) throws SQLException, IOException {
        System.out.println("Loading initial data from CSV files...");
        
        for (Map.Entry<String, String> entry : CSV_FILES.entrySet()) {
            String tableName = entry.getKey();
            String csvPath = entry.getValue();
            
            try {
                List<String[]> csvData = readCSV(csvPath);
                if (!csvData.isEmpty()) {
                    insertDataFromCSV(conn, tableName, csvData);
                    System.out.println("Loaded " + csvData.size() + " records into " + tableName);
                }
            } catch (IOException e) {
                System.err.println("Warning: Could not load CSV file for " + tableName + ": " + e.getMessage());
            }
        }
    }
    
    private static List<String[]> readCSV(String filePath) throws IOException {
        List<String[]> records = new ArrayList<>();
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            System.err.println("CSV file not found: " + filePath);
            return records;
        }
        
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                // Parse the CSV
                String[] values = line.split(",");
                records.add(values);
            }
        }
        return records;
    }
    
    private static void insertDataFromCSV(Connection conn, String tableName, List<String[]> data) throws SQLException {
        if (data.isEmpty()) return;
        
        // Generate parameter placeholders based on column count
        String placeholders = String.join(",", Collections.nCopies(data.get(0).length, "?"));
        String sql = "INSERT INTO " + tableName + " VALUES (" + placeholders + ")";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String[] row : data) {
                for (int i = 0; i < row.length; i++) {
                    pstmt.setString(i + 1, row[i].trim());
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    private static void handleException(Exception e) {
        if (e instanceof SQLException && ((SQLException)e).getSQLState().equals("X0Y32")) {
            System.out.println("Database already exists.");
        } else {
            System.err.println("Error during database creation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void closeResources(Statement stmt, Connection conn) {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        createDatabase();
    }
}