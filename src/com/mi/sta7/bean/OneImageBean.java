package com.mi.sta7.bean;

import java.io.Serializable;

public class OneImageBean implements Serializable {
private String title;
private String content;
private String image;
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
public String getImage() {
	return image;
}
public void setImage(String image) {
	this.image = image;
}
}
