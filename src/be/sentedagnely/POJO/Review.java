package be.sentedagnely.POJO;

public class Review {
	private int id;
	private int note;
	private String remark;
	public Review() {}
	public Review(int id, int note, String remark) {
		super();
		this.id = id;
		this.note = note;
		this.remark = remark;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNote() {
		return note;
	}
	public void setNote(int note) {
		this.note = note;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
