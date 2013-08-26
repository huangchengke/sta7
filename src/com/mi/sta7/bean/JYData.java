package com.mi.sta7.bean;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class JYData {
	public String ResponCode;
	public String ResponMsg;
	public String action;
	public String username;
	public String account_type;
	public String password;
	public String Os;
	public String phone_type;
	/**
	 * 由 Java Object 回構成 Json 字串
	 * @return Json 字串, 如構建錯誤, 回 ""
	 */
	public String toJson() {
		Gson gson = new Gson();
		try {
			return gson.toJson(this);
		} catch (JsonParseException e) {
			return "";
		}
	}	
}
