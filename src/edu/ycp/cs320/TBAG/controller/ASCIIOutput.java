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
			if (prof.equals("hake")) toOut += "<div class=\"hake-ascii-art\">";
			else if (prof.equals("babcock")) toOut += "<div class=\"babcock-ascii-art\">";
			while (reader.hasNextLine()) {
				String asciiLine = reader.nextLine();
				
				//ChatGPT recommended that i do this to avoid any issues with formatting
				asciiLine = asciiLine
						  .replace("&", "&amp;")
						  .replace("<", "&lt;")
						  .replace(">", "&gt;");
				
				toOut += asciiLine + "\n";
			}
			toOut += "</div>";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return toOut;
    }
}
