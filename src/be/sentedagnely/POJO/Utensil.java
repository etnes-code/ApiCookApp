package be.sentedagnely.POJO;

public class Utensil {
	private int id;
	private String name;
	private String type;
	
	public Utensil() {}

	public Utensil(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getType() { return type; }
	public void setType(String type) { this.type = type; }

}