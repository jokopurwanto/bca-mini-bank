package com.bca.minibank.utils;

import javax.servlet.http.HttpServletRequest;

import com.bca.minibank.model.ModelSession;

public class UtilsSession {
	
	public static ModelSession getTransferInSession(HttpServletRequest req) {
		ModelSession modelSession = (ModelSession) req.getSession().getAttribute("myData");
		
		if(modelSession == null) {
			modelSession = new ModelSession();
			req.getSession().setAttribute("myData", modelSession);
		}
		
		return modelSession;		
	}
	
	public static void removeModelInfo(HttpServletRequest req) {
		req.getSession().removeAttribute("myData");
	}

}
