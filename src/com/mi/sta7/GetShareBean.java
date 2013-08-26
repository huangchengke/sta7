package com.mi.sta7;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.mi.sta7.bean.Response;
import com.mi.sta7.bean.ShareBean;
import com.mi.sta7.utils.HttpsUtil;
import com.mi.sta7.utils.Tools;

public class GetShareBean {
	private final String URL = "https://118.145.12.100:8450/mi1/st5?scr=get_weibo&off_set=";
	private List<ShareBean> shareBean = new ArrayList<ShareBean>();
	private int page;
	private String respString;

	public GetShareBean(int page) {
		this.page = page;
	}

	public List<ShareBean> getShareBean() {
		getShare();
		return shareBean;
	}

	public void setShareBean(List<ShareBean> shareBean) {
	
		this.shareBean = shareBean;
	}

	private void getShare() {
				try {
					respString = HttpsUtil.prvMultipartHttpRequest(URL + page*25);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Log.d("LOG_TAG", "responsestring"+respString);
				com.mi.sta7.bean.Response response = com.mi.sta7.bean.Response
						.constructFromJson(respString);
				for (int i = 0; i < response.list.get(0).item.size(); i++) {
					Response.Item item = new Response.Item();
					item = response.list.get(0).item.get(i);
					String jsonString = item.lbl.get(0).val;
					try {
						JSONObject jsonObject = new JSONObject(jsonString);
						String content = jsonObject.getString("text");
						String time = Tools.sinaTimeConvert(jsonObject.getString("created_at"));
						JSONObject jsonObjectUsr = jsonObject.getJSONObject("user");
						String name = jsonObjectUsr.getString("screen_name");
						String url = jsonObjectUsr.getString("profile_image_url");
						String verified_reason = jsonObjectUsr.getString("verified_reason");
						ShareBean bean = new ShareBean();
						bean.setName(name);
						bean.setContent(content);
						bean.setImg(url);
						bean.setTime(time);
						bean.setVerified_reason(verified_reason);
						shareBean.add(bean);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
	}

	/**
	 * 将传进的字串解析成 shareBean 的 list 形式
	 * @param srcString 传进的字串
	 * @return
	 */
	public static List<ShareBean> convertToShareBeans(String srcString) {
		List<ShareBean> shareBeans = new ArrayList<ShareBean>();
		try {
			JSONObject respObject = new JSONObject(srcString);
			JSONArray statuseArray = respObject.getJSONArray("statuses");
			for (int i=0; i<statuseArray.length(); i++) {
				JSONObject jsonObject = (JSONObject)statuseArray.get(i);
				String id = jsonObject.getString("idstr");
				String time = Tools.sinaTimeConvert(jsonObject.getString("created_at"));
				String created_at = time.substring(5, 16);
				String text = jsonObject.getString("text");
				JSONObject usrObject = jsonObject.getJSONObject("user");
				String name = usrObject.getString("screen_name");
				String img = usrObject.getString("profile_image_url");
				String verified_reason = usrObject.getString("verified_reason");
				ShareBean shareBean = new ShareBean();
				shareBean.setId(id);
				shareBean.setContent(text);
				shareBean.setImg(img);
				shareBean.setName(name);
				shareBean.setTime(created_at);
				shareBean.setVerified_reason(verified_reason);
				shareBeans.add(shareBean);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return shareBeans;
	}

}
