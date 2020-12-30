package be.sentedagnely.POJO;

import java.util.HashSet;
import java.util.Set;

public class Ingredient {
	private int id;
	private String name;
	private String type;
	private int calories;
	private String massUnit;
	
	public Ingredient(){}
	public Ingredient(int id, String name, String type, int calories, String massUnit) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.calories = calories;
		this.massUnit = massUnit;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getCalories() {
		return calories;
	}
	public void setCalories(int calories) {
		this.calories = calories;
	}
	public String getMassUnit() {
		return massUnit;
	}
	public void setMassUnit(String massUnit) {
		this.massUnit = massUnit;
	}
	
		
}
