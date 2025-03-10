package edu.ycp.cs320.lab02.controller;

import edu.ycp.cs320.lab02.model.GuessingGame;
import edu.ycp.cs320.lab02.model.Numbers;

public class NumbersController {
	private Numbers model;
	
	public void setModel(Numbers model) {
		this.model = model;
	}
	public Numbers getModel() {return this.model;}
	
	public Double add(Double first, Double second) {
		return first + second;
	}
	public double add(Double first, Double second, Double third) {
		return first + second + third;
	}
	public double multiply(Double first, Double second) {
		return first * second;
	}
	public void add() {
		this.model.add();
	}
	public void multiply() {
		this.model.multiply();
	}
	
	public void setFactors(double factor1, double factor2) {
		this.model.setFactor1(factor1);
		this.model.setFactor2(factor2);
	}
	
	public void setNums(double num1, double num2, double num3) {
		this.model.setNum1(num1);
		this.model.setNum2(num2);
		this.model.setNum3(num3);
	}
}
