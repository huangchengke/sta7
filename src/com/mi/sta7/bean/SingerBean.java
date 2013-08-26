package com.mi.sta7.bean;

public class SingerBean {
	
	private int rank;
	private int age;
	private int singerVotes;
	private String id;
	private String sex;
	private String singerName;
	private String image;
	private String info;
	
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getSingerVotes() {
		return singerVotes;
	}
	public void setSingerVotes(int singerVotes) {
		this.singerVotes = singerVotes;
	}
	public String getSingerName() {
		return singerName;
	}
	public void setSingerName(String singerName) {
		this.singerName = singerName;
	}
}
