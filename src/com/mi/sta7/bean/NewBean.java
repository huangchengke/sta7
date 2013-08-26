package com.mi.sta7.bean;

public class NewBean {
	
	private int id;
	private String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getBig_image() {
		return big_image;
	}
	public void setBig_image(String big_image) {
		this.big_image = big_image;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setId(int id) {
		this.id = id;
	}
	private String url;
	private String image;
	private String big_image;
	private String time;
	private String content;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getId() {
		return id;
	}

}
