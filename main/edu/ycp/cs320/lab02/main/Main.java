package edu.ycp.cs320.lab02.main;

import java.io.File;

import org.eclipse.jetty.server.Server;
import edu.ycp.cs320.TBAG.tbagdb.persist.DerbyDatabase;

public class Main {
	public static void main(String[] args) throws Exception {
		
		int port = 8081;
		
		DerbyDatabase db = new DerbyDatabase("test");
		if (!db.dbExists("test")) db.create();
		
		String webappCodeBase = "./war";
		File warFile = new File(webappCodeBase);
		Launcher launcher = new Launcher();
		
		// initialize the database on runtime
		
		// get a server for port 8081
		System.out.println("CREATING: web server on port " + port);
		Server server = launcher.launch(true, port, warFile.getAbsolutePath(), "");
		
        // Start things up!		
		System.out.println("STARTING: web server on port " + port);
		server.start();
		
		// dump the console output - this will produce a lot of red text - no worries, that is normal
		server.dumpStdErr();
		
		// Inform user that server is running
		System.out.println("RUNNING: web server on port " + port);
		
        // The use of server.join() the will make the current thread join and
        // wait until the server is done executing.
        // See http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
		server.join();
	}
}
