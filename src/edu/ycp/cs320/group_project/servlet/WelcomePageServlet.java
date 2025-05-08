package edu.ycp.cs320.group_project.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class WelcomePageServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String endpoint = req.getParameter("endpoint");
		
		// send somewhere else
		switch (endpoint) {
			case "play":
				resp.sendRedirect("game");
			break;
		}
		
		resp.sendRedirect("index");
	}
}