package com.mi.sta7.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class Response {
	public static final String[] ALL_ELEMENT_NAME = {"lbl", "btn", "img", "opt", "chk", "swt", "txt"};
	public static final String PARSE_ERROR = "500";
	private static final String LOG_TAG = "RESPONSE";
	
	public String rv = "0"; // server 回返值, 必須設初值, 否則新建的 Response 會被視為無效
	public Message msg = new Message(); // server 回返訊息
	public String sid; // Session ID
	public String act; // 讀入 Response 後, 第一個要執行的行為或動作
	public String x; // google 座標
	public String y; // google 座標
	public String bx; // Baidu 座標
	public String by; // Baidu 座標
	public String err; // Client 修改资料时，Server 回返的修改状态
	public String style; // 整體版面排列風格
	public String uid;
	public ArrayList<List> list; // 主資料
	public ArrayList<List> pop = null; // Popup window 資料
	public ArrayList<List> bar; // 常駐條資料
	public ArrayList<Item> sbo = null; // Search box 資料
	public ArrayList<Item> nav; // 導航條資料
	public ArrayList<Item> item; // 單一 List 之資料, 多item
	public String region; // 區域
	public String local; // 新建本地資料
	public String cache;
	public String update; // 通知更新本地資料
	public String upd; // 从端更新主端资料接口
	public String del; // 从端删除主端资料接口
	public String req;

	/**
	 * 在 Response 下加一個 List
	 * @param list: 要加入的 List
	 */
	public void addList(List list) {
		if (this.list == null) this.list = new ArrayList<Response.List>();
		this.list.add(list);
	}
	
	/**
	 * 在 Response 下加一個 Item
	 * @param item: 要加入的 Item
	 */
	public void addItem(Item item) {
		if (this.item == null) this.item = new ArrayList<Response.Item>();
		this.item.add(item);
	}
	
	/**
	 * 检查此 response 是否合格
	 * @return
	 */
	public boolean isValid() {
		return this != null && this.rv != null && this.rv.equals("0");
	}
	
	/**
	 * 由 Json 字串構建一個 Java Object
	 * @param json 字串
	 * @return Java 物件, 如無法剖析, 則回 null
	 */
	public static Response constructFromJson(String json) {
		Response mResp = new Response();
		String mRv = "0";
		if (json != null) {
			Matcher m = Pattern.compile("\"rv\":(\\d*?),").matcher(json);
			if (m.find()) mRv = m.group(1);
		}
		Gson gson = new Gson();
		try {
			mResp = gson.fromJson(json, Response.class);
			if (mResp != null && mResp.rv == null) {
				mResp.rv = mRv;
			}
		} catch (JsonParseException e) {
			Log.d(LOG_TAG, "(constructFromJson) JsonParseException=" + mResp.rv);
			mResp.rv = PARSE_ERROR;
		}
		if (mResp == null) {
			mResp = new Response();
			mResp.rv = "-1";
		}
		return mResp;
	}

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
	
	public static class List implements Cloneable {
		public String local; // 需建置本地資料庫
		public String prompt; // 提示字, 用於分類資料
		public String title; // list 標題
		public String style; // 排列風格
		public String height; // 高度
		public String layout; // 採用版型
		public String load; // 動態載入入口
		public String offset; // 動態載入之偏移量
		public String total; // 紀錄總數
		public String bg; // 背景圖
		public String update; // 通知更新本地資料
		public String upd; // 从端更新主端资料接口
		public String del; // 从端删除主端资料接口
		public String req;
		public String src; // 读取本地资源的名字
		public String order; // 排序依据
		public ArrayList<Item> item; // 包含之子項

		/**
		 * 在 List 下加一個 Item
		 * @param item: 要加入的 Item
		 */
		public void addItem(Item item) {
			if (this.item == null) this.item = new ArrayList<Response.Item>();
			this.item.add(item);
		}

		public Response.List Clone() {
			Response.List list = new Response.List();
			if (this.bg != null) list.bg = this.bg;
			if (this.del != null) list.del = this.del;
			if (this.height != null) list.height = this.height;
			if (this.layout != null) list.layout = this.layout;
			if (this.load != null) list.load = this.load;
			if (this.local != null) list.local = this.local;
			if (this.offset != null) list.offset = this.offset;
			if (this.order != null) list.order = this.order;
			if (this.prompt != null) list.prompt = this.prompt;
			if (this.src != null) list.src = this.src;
			if (this.style != null) list.style = this.style;
			if (this.title != null) list.title = this.title;
			if (this.total != null) list.total = this.total;
			if (this.upd != null) list.upd = this.upd;
			if (this.update != null) list.update = this.update;
			if (this.item != null) {
				list.item = new ArrayList<Response.Item>();
				for (int i=0; i<this.item.size(); i++) list.item.add(i, this.item.get(i).Clone());
			}
			return list;
		}
		
		/**
		 * 将 list 转换成 json 字串
		 * @return
		 */
		public String toJson() {
			try {
				return new Gson().toJson(this);
			} catch (JsonParseException e) {
				return "";
			}
		}
		/**
		 * 依據 list 下的 item[] 中的某元素的 val 屬性分組, 相等即為同一 group,
		 * list 需已依該元素排序
		 * @param elemName 要判定的元素名, 如 lbl, img....
		 * @param index 元素索引
		 * @return 返回一個 Response, 其中的多個 List[] 已分好組
		 */
		public Response groupBy(String elemName, int index) {
			ArrayList<Response.List> groupedLists = new ArrayList<Response.List>();
			ArrayList<Response.Item> groupedItems = new ArrayList<Response.Item>();
			String currentVal = "";
			for (Item mItem : this.item) {
				String mVal = mItem.get(elemName, index).val;
				if (!mVal.equals(currentVal)) {
					if (groupedItems.size() > 0) {
						Response.List oneList = new Response.List();
						oneList.item = (ArrayList<Item>) groupedItems;
						oneList.title = currentVal;
						oneList.style = "H3";
						oneList.layout = this.layout;
						groupedLists.add(oneList);
					}
					groupedItems = new ArrayList<Response.Item>();
					currentVal = mVal;
				}
				groupedItems.add(mItem);
			}
			Response resp = new Response();
			resp.list = groupedLists;
			resp.style = "L";
			return resp;
		}
		
		/**
		 * 依據 list 下的 item[] 中的某元素的 val 屬性排序,
		 * @param elemName 要判定的元素名, 如 lbl, img....
		 * @param index 元素索引
		 * @param order 升冪或降冪
		 * @return 返回一個排序好的 Response.List
		 */
		public Response.List sortBy(String elemName, int index, String order) {
			if (this.item == null || this.item.size() == 0) return this;
			if (this.item.get(0).get(elemName, index) == null) return this;
			SortedMap<String,Integer> sortedKeys = new TreeMap<String,Integer>();
			int count = this.item.size();
			for (int i=0; i<count; i++) {
				String mVal = this.item.get(i).get(elemName, index).val;
				sortedKeys.put(mVal + String.format("%03d"), i);
			}
			Response.List result = new Response.List();
			result.item = new ArrayList<Response.Item>();
			for (int i=0; i<sortedKeys.size(); i++) {
				int j = order.matches("desc|DESC") ? count -1 - i : i;
				result.item.add(this.item.get(sortedKeys.get(j)));
				i++;
			}
			return result;
		}

		/**
		 * 依據 list 下的 item[] 中的多個元素的 val 屬性排序,
		 * @param strOrder 語法如 "lbl1, lbl3 desc"
		 * @return 返回一個排序好的 Response.List
		 */
		public List sortBy(String strOrder) {
			Response.List result = this;
			String[] orders = strOrder.split(",");
			for (String oneOrder : orders) {
				String[] orderVal = oneOrder.split("\\s+");
				if (orderVal == null || orderVal.length == 0) continue;
				if (orderVal[0].length() < 3) continue;
				String elemName = orderVal[0].substring(0, 3);
				int index = Integer.valueOf(orderVal[0].substring(3));
				String order = orderVal.length > 1 ? orderVal[1] : "";
				result = result.sortBy(elemName, index, order);
			}
			return result;
		}
		
		/**
		 * 依據 list 下的 item[] 中的多個元素的 val 屬性排序,
		 * @param strOrder 語法如 "lbl1, lbl3 desc"
		 * @return 返回一個排序好的 Response.List
		 */
		public List sort() {
			Response.List result = this;
			if (this.item == null || this.item.equals("") ||
					this.order == null || this.order.equals("")) return this;
			String[] orders = this.order.split(","); // 排序参数的数组
			int strOrderSize = orders.length;
			String orderArray[][] = new String[orders.length][2];
			for(int orderInd=0; orderInd<strOrderSize; orderInd++) {
				orderArray[orderInd] = orders[orderInd].split("\\s+"); // 将参数全存至数组
			}
			for (int orderInd=0; orderInd<strOrderSize; orderInd++) { // 从第一个参数开始排
				for (int i=0; i<this.item.size(); i++) {
					for (int j=i+1; j<this.item.size(); j++) { // 采用冒泡排序法
						boolean isAllMatched = true;
						for (int orderIndex=orderArray.length-1; orderIndex>=0; orderIndex--) { // 从后往前遍历数组，只有当前的满足条件才可以交换
							String order = orderArray[orderIndex][1];
							String eleName = orderArray[orderIndex][0].substring(0, 3); // 进行排序的 element 的名称
							int eleIndex = Integer.parseInt(orderArray[orderIndex][0].substring(3, orderArray[orderIndex][0].length())); // 进行排序的 element 的序号
							if (orderIndex==orderInd) {
								if (order.equals("asc")) {
									isAllMatched &= ((this.item.get(i).get(eleName, eleIndex)
											.val.compareTo(this.item.get(j).get(eleName, eleIndex).val))>0);
								} else if (order.equals("desc")) {
									isAllMatched &= ((this.item.get(i).get(eleName, eleIndex)
											.val.compareTo(this.item.get(j).get(eleName, eleIndex).val))<0);
							}
						} else if (orderIndex!=orderInd && ((this.item.get(i).get(eleName, eleIndex)
									.val.compareTo(this.item.get(j).get(eleName, eleIndex).val))!=0)) {
								isAllMatched = false;
							}
						}
						if (isAllMatched) {
							Response.Item temp = this.item.get(i).Clone();
							this.item.add(i, this.item.get(j).Clone());
							this.item.remove(i+1);
							this.item.add(j, temp);
							this.item.remove(j+1);
						}
					}
				}
			}
			return result;
		}
	}
	
	public static class Item implements Cloneable { // 每個 Item 可含多個元素 Element
		public String act; // Item 的觸發行為或動作
		public String id; // 每個 Item 的唯一識別號
		public String bg; // Item 的背景图
		public ArrayList<Element> lbl; // label
		public ArrayList<Element> img; // image
		public ArrayList<Element> btn; // button
		public ArrayList<Element> txt; // textbox
		public ArrayList<Element> swt; // switch
		public ArrayList<Element> chk; // checkbox
		public ArrayList<Element> opt; // option

		public Response.Item Clone() {
			Response.Item item = new Response.Item();
			item.act = this.act;
			item.id = this.id;
			if (this.lbl != null) {
				item.lbl = new ArrayList<Response.Element>();
				for (int i=0; i<this.lbl.size(); i++) item.lbl.add(i, this.lbl.get(i).Clone());
			}
			if (this.btn != null) {
				item.btn = new ArrayList<Response.Element>();
				for (int i=0; i<this.btn.size(); i++) item.btn.add(i, this.btn.get(i).Clone());
			}
			if (this.chk != null) {
				item.chk = new ArrayList<Response.Element>();
				for (int i=0; i<this.chk.size(); i++) item.chk.add(i, this.chk.get(i).Clone());
			}
			if (this.img != null) {
				item.img = new ArrayList<Response.Element>();
				for (int i=0; i<this.img.size(); i++) item.img.add(i, this.img.get(i).Clone());
			}
			if (this.opt != null) {
				item.opt = new ArrayList<Response.Element>();
				for (int i=0; i<this.opt.size(); i++) item.opt.add(i, this.opt.get(i).Clone());
			}
			if (this.swt != null) {
				item.swt = new ArrayList<Response.Element>();
				for (int i=0; i<this.swt.size(); i++) item.swt.add(i, this.swt.get(i).Clone());
			}
			if (this.txt != null) {
				item.txt = new ArrayList<Response.Element>();
				for (int i=0; i<this.txt.size(); i++) item.txt.add(i, this.txt.get(i).Clone());
			}
			return item;
		}
		
		/**
		 * 將 Item 轉成 Json 字串
		 */
		public String toJson() {
			try {
				return new Gson().toJson(this);
			} catch (JsonParseException e) {
				return "";
			}
		}	

		/**
		 * 利用元件名找出 item 下的某元件陣列
		 * @param elemName 要找的元件名
		 * @return 如找不到, 返回 null
		 */
		@SuppressWarnings("unchecked")
		public ArrayList<Element> get(String elementName) {
			ArrayList<Response.Element> elements = null;
			Class<Response.Item> classItem = Response.Item.class;
			try {
				Field mField = classItem.getDeclaredField(elementName);
				elements = (ArrayList<Element>) mField.get(this);
			} catch (IllegalAccessException e1) {
			} catch (NoSuchFieldException e) {
			}
			return elements;
		}

		/**
		 * 利用元件名及索引號找出 item 下的某元素
		 * @param elemName 要找的元件名
		 * @param index 要找的索引號
		 * @return 如找不到, 返回 null
		 */
		public Element get(String elementName, int index) {
			Response.Element element = null;
			try {
				element = this.get(elementName).get(index);
			} catch (ArrayIndexOutOfBoundsException e) {
			} catch (NullPointerException e) {
			}
			return element;
		}

		/**
		 * 判斷是否兩個 item 資料全等
		 * @param item
		 * @return
		 */
		public boolean equalTo(Item item) {
			if ((act != item.act) && !(act != null && act.equals(item.act))) return false;
			if ((id != item.id) && !(id != null && id.equals(item.id))) return false;

			if (((lbl != item.lbl) && !(lbl.size() == item.lbl.size()))) return false;
			if (lbl != null) for (int i=0; i<lbl.size(); i++) if (!lbl.get(i).equalTo(item.lbl.get(i))) return false;
			if (((img != item.img) && !(img.size() == item.img.size()))) return false;
			if (img != null) for (int i=0; i<img.size(); i++) if (!img.get(i).equalTo(item.img.get(i))) return false;
			if (((btn != item.btn) && !(btn.size() == item.btn.size()))) return false;
			if (btn != null) for (int i=0; i<btn.size(); i++) if (!btn.get(i).equalTo(item.btn.get(i))) return false;
			if (((txt != item.txt) && !(txt.size() == item.txt.size()))) return false;
			if (txt != null) for (int i=0; i<txt.size(); i++) if (!txt.get(i).equalTo(item.txt.get(i))) return false;
			if (((swt != item.swt) && !(swt.size() == item.swt.size()))) return false;
			if (swt != null) for (int i=0; i<swt.size(); i++) if (!swt.get(i).equalTo(item.swt.get(i))) return false;
			if (((chk != item.chk) && !(chk.size() == item.chk.size()))) return false;
			if (chk != null) for (int i=0; i<chk.size(); i++) if (!chk.get(i).equalTo(item.chk.get(i))) return false;
			if (((opt != item.opt) && !(opt.size() == item.opt.size()))) return false;
			if (opt != null) for (int i=0; i<opt.size(); i++) if (!opt.get(i).equalTo(item.opt.get(i))) return false;
			return true;
		}
	}

	public static class Element implements Cloneable {
		public String name = ""; // 名稱
		public String val = ""; // 值
		public String url = ""; // 圖檔下載位址
		public String act = ""; // 行為, 動作
		public String tag = ""; // 標記
		public String re = ""; // 正規表達式, 用於 editText
		
		/**
		 * Response.Element Constructor, 順序: name, val, url, act, tag, re
		 */
		public Element(String... params) {
			if (params.length >= 1) this.name = params[0];
			if (params.length >= 2) this.val = params[1];
			if (params.length >= 3) this.url = params[2];
			if (params.length >= 4) this.act = params[3];
			if (params.length >= 5) this.tag = params[4];
			if (params.length >= 6) this.re = params[5];
		}

		public Response.Element Clone() {
			Response.Element element = new Response.Element();
			if (this.act != null) element.act = this.act;
			if (this.name != null) element.name = this.name;
			if (this.re != null) element.re = this.re;
			if (this.tag != null) element.tag = this.tag;
			if (this.url != null) element.url = this.url;
			if (this.val != null) element.val = this.val;
			return element;
		}

		/**
		 * 判斷 Element 的全部屬性相等
		 * @param elem
		 * @return
		 */
		public boolean equalTo(Element elem) {
			if ((val != elem.val) && !(val != null && val.equals(elem.val))) return false;
			if ((name != elem.name) && !(name != null && name.equals(elem.name))) return false;
			if ((act != elem.act) && !(act != null && act.equals(elem.act))) return false;
			if ((url !=elem.url) && !(url != null && url.equals(elem.url.trim()))) return false;
			if ((re !=elem.re) && !(re != null && re.equals(elem.re))) return false;
			if ((tag !=elem.tag) && !(tag != null && tag.equals(elem.tag))) return false;
			return true;		
		}

		/**
		 * 將 Element 轉成 Json 字串
		 * @return
		 */
		public String toJson() {
			try {
				return new Gson().toJson(this);
			} catch (JsonParseException e) {
				return "";
			}
		}	

	}
	
	public static class Message {
		public String usrname; // 用戶名
		public String reg_tel; // 登記電話
		public String email; // 郵箱
		public String dc; // Data connection
		public String is; // Image servers
		public String av; // Android version
	}
}