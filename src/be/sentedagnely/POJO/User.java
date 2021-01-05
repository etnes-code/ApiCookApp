package be.sentedagnely.POJO;

import java.util.HashSet;
import java.util.Set;


public class User {

	private int id;
	private String name;
	private String firstName;
	private String email;
	private String password;
	private String address;
	private Set<Recipe> listRecipe = new HashSet<>();

	public User() {
	}
	public User(int id, String name, String firstName, String email, String password, String address) {
		super();
		this.id = id;
		this.name = name;
		this.firstName = firstName;
		this.email = email;
		this.password = password;
		this.address = address;
	}
	//
	// GET/SET
	//

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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String adress) {
		this.address = adress;
	}
	
	public Set<Recipe> getListRecipe() {
		return listRecipe;
	}
	public void setListRecipe(Set<Recipe> listRecipe) {
		this.listRecipe = listRecipe;
	}
	public void addList(Recipe recipe) {
		if(!this.listRecipe.contains(recipe)) {
			listRecipe.add(recipe);
		}	
	}
	
	

}
