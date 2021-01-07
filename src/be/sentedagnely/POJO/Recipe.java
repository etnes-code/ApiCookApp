package be.sentedagnely.POJO;

import java.util.HashSet;
import java.util.Set;

public class Recipe {
	
	private int id;
	private String name;
	private int difficulty; // difficulté de 1  à 5
	private int totalDuration;  // le temps sera compté en minute
	private String category;
	private String urlPicture;
	private Set<Ingredient>  listIngredient= new HashSet<>();
	private Set<Step>  listStep= new HashSet<>();
	
	public Recipe() {}
	public Recipe(int id, String name,String category, int difficulty, int totalDuration, String urlPicture) {
		this.id = id;
		this.name = name;
		this.category= category;
		this.difficulty = difficulty;
		this.totalDuration = totalDuration;
		this.urlPicture = urlPicture;
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
	public int getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	public int getTotalDuration() {
		return totalDuration;
	}
	public void setTotalDuration(int totalDuration) {
		this.totalDuration = totalDuration;
	}
	public String getUrlPicture() {
		return urlPicture;
	}
	public void setUrlPicture(String urlPicture) {
		this.urlPicture = urlPicture;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	public Set<Ingredient> getListIngredient() {
		return listIngredient;
	}
	public void setListIngredient(Set<Ingredient> listIngredient) {
		this.listIngredient = listIngredient;
	}
	public Set<Step> getListStep() {
		return listStep;
	}
	public void setListStep(Set<Step> listStep) {
		this.listStep = listStep;
	}
	public void addListIngredient(Ingredient ingredient) {
		if(!this.listIngredient.contains(ingredient)) {
			listIngredient.add(ingredient);
		}
	}
	public void addListStep(Step step) {
		if(!this.listStep.contains(step)) {
			listStep.add(step);
		}
	}
	
		
}
