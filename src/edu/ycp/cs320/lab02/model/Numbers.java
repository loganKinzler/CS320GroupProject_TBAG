package edu.ycp.cs320.lab02.model;

public class Numbers {
	private double num1, num2, num3;
	private double factor1, factor2;
	private double sum, product;
	
	public Numbers() {
		
	}

	public double getNum1() {
		return num1;
	}

	public void setNum1(double num1) {
		this.num1 = num1;
	}

	public double getNum2() {
		return num2;
	}

	public void setNum2(double num2) {
		this.num2 = num2;
	}

	public double getNum3() {
		return num3;
	}

	public void setNum3(double num3) {
		this.num3 = num3;
	}

	public double getFactor1() {
		return factor1;
	}

	public void setFactor1(double factor1) {
		this.factor1 = factor1;
	}

	public double getFactor2() {
		return factor2;
	}

	public void setFactor2(double factor2) {
		this.factor2 = factor2;
	}

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public double getProduct() {
		return product;
	}

	public void setProduct(double product) {
		this.product = product;
	}
	
	public void add() {
		this.sum = this.num1 + this.num2 + this.num3;
	}
	public void multiply() {
		this.product = this.factor1 * this.factor2;
	}
}
