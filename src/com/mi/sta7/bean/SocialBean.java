package com.mi.sta7.bean;

import java.util.ArrayList;
import java.util.List;

public class SocialBean {
	
	private String title = "";
	private List<String> ids = new ArrayList<String>();
	private String imageUrl = "";
	private String imageBGUrl = "";
	private String idsString = "";
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<String> getIds() {
		return ids;
	}
	public void setIds(List<String> ids) {
		this.ids = ids;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getImageBGUrl() {
		return imageBGUrl;
	}
	public void setImageBGUrl(String imageBGUrl) {
		this.imageBGUrl = imageBGUrl;
	}
	public String getIdsString() {
		return idsString;
	}
	public void setIdsString(String idsString) {
		this.idsString = idsString;
	}
	
}
