package com.mi.sta7.bean;

public class ImageBean {
	private int id;
	private String title;
	private String content;
	private String url;
	private String imgUrl;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public int getIsPhoto() {
		return isPhoto;
	}
	public void setIsPhoto(int isPhoto) {
		this.isPhoto = isPhoto;
	}
	private int isPhoto;

}
