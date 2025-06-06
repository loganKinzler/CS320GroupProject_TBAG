package edu.ycp.cs320.TBAG.controller;

import java.io.InputStream;
import java.util.Scanner;

import edu.ycp.cs320.group_project.servlet.GameEngineServlet;

public class ASCIIOutput {
	public static String profAsciiEasterEgg(GameEngineServlet servlet, String prof) {
    	System.out.println(prof);
    	String toOut = "";
    	try {
    		InputStream in = servlet.getServletContext().getResourceAsStream("/recs/" + prof + ".txt");
			Scanner reader = new Scanner(in);
			if (prof.equals("hake")) toOut += "<p class=\"hake-ascii-art\">";
			else if (prof.equals("babcock")) toOut += "<p class=\"babcock-ascii-art\">";
			while (reader.hasNextLine()) {
				String asciiLine = reader.nextLine();
				
				//ChatGPT recommended that i do this to avoid any issues with formatting
				asciiLine = asciiLine
						  .replace("&", "&amp;")
						  .replace("<", "&lt;")
						  .replace(">", "&gt;");
				
				toOut += asciiLine + "\n";
			}
			toOut += "</p>";
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return toOut;
    }
	
	public static String ItemAscii(GameEngineServlet servlet, String shovel) {
    	String toOut = "";
    	try {
    		InputStream in = servlet.getServletContext().getResourceAsStream("/recs/shovel.txt");
			Scanner reader = new Scanner(in);
			
			if (shovel.equals("biiig shovel")) toOut += "<p class=\"item-ascii-art\">";
			
			else if(shovel.equals("flimsy orange key")) {
				in = servlet.getServletContext().getResourceAsStream("/recs/flimsy orange key.txt");
				reader = new Scanner(in);
				toOut += "<p class=\"item-ascii-art\">";
			}
			
			else if(shovel.equals("soldering iron")) {
				in = servlet.getServletContext().getResourceAsStream("/recs/iron.txt");
				reader = new Scanner(in);
				toOut += "<p class=\"item-ascii-art\">";
			}
			
			else if(shovel.equals("lead pipe")) {
				in = servlet.getServletContext().getResourceAsStream("/recs/lead_pipe.txt");
				reader = new Scanner(in);
				toOut += "<p class=\"item-ascii-art\">";
			}
			
			else {
				reader.close();
				return "<p>";
			}
			
			//System.out.println("It worked");
			while (reader.hasNextLine()) {
				String asciiLine = reader.nextLine();
				
				//ChatGPT recommended that i do this to avoid any issues with formatting
				asciiLine = asciiLine
						  .replace("&", "&amp;")
						  .replace("<", "&lt;")
						  .replace(">", "&gt;");
				
				toOut += asciiLine + "\n";
			}
			toOut += "</p>";
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block0
			e.printStackTrace();
		}
    	
    	return toOut;
	}
}
