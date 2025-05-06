package edu.ycp.cs320.group_project.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionInfoController {
	public static void sessionPlaySound(HttpServletRequest req, String attribute) {
    	HttpSession session = req.getSession();
    	
    	Boolean playSound = (Boolean) session.getAttribute(attribute);
        if (playSound != null && playSound) {
            req.setAttribute(attribute, true);
            session.removeAttribute(attribute); // ensure it's only played once
        }
    }
}
