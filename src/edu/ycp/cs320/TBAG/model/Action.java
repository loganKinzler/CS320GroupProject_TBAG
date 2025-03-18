package edu.ycp.cs320.TBAG.model;

import java.util.ArrayList;

public class Action {

	// vars
	private Boolean isValid;
	private String errorMessage, method;
	private ArrayList<String> params;
	
	
	// constructors
	public Action(String errorMessage) {
		this.isValid = false;
		this.errorMessage = errorMessage;
	}
	
	public Action(String method, ArrayList<String> params) {
		this.isValid  = true;
		this.method = method;
		this.params = params;
	}
	
	
	// getters / setters
	public Boolean IsValid() {return this.isValid;}
	public void SetValidity(Boolean isValid) {this.isValid = isValid;}

	public String GetErrorMessage() {return this.errorMessage;}	
	public void SetErrorMessage(String type) {this.errorMessage = type;}

	public String GetMethod() {return method;}
	public void SetMethod(String method) {this.method = method;}
	
	public ArrayList<String> GetParams() {return params;}
	public void SetParams(ArrayList<String> params) {this.params = params;}

	public void AddParam(String param) {this.params.add(method);}
	public void RemoveParam(Integer index) {this.params.remove((int) index);}
	public void RemoveParam(String param) {this.params.remove(param);}
}
