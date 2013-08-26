package com.mi.sta7.mangerdate;

import java.util.ArrayList;
import java.util.List;
import com.mi.sta7.bean.DateBean;
public class InitDate {

	public static List<DateBean> dateBeans = new ArrayList<DateBean>();

	public static List<DateBean> getDateBeans() {
		return dateBeans;
	}
	public static void setDateBeans(List<DateBean> dateBeans) {
		InitDate.dateBeans = dateBeans;
	}

	public static String mac_wifi;
	public static String imie;

}
