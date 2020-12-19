package be.sentedagnely.POJO;

public class Step {
	private int id;
	private int order; 
	private String text; 
	private int duration; 
	
	public Step() {}

	public Step(int id, int order, String text, int duration) {
		super();
		this.id = id;
		this.order = order;
		this.text = text;
		this.duration = duration;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	
	
	

}
