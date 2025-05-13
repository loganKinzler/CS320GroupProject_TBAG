package edu.ycp.cs320.group_project.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ycp.cs320.TBAG.tbagdb.persist.DerbyDatabase;

public class WelcomePageServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		DerbyDatabase db = new DerbyDatabase("test");
		req.setAttribute("foundCommands", db.getFoundCommands());
		
		req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String endpoint = req.getParameter("endpoint");
		
		DerbyDatabase db = new DerbyDatabase("test");
		req.setAttribute("foundCommands", db.getFoundCommands());
		
		try {
			// send somewhere else
			switch (endpoint) {
				case "play":
					resp.sendRedirect("game");
				return;
			}
		} catch(Exception e) {}
		
		req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
	}
}