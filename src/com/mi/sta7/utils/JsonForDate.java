package com.mi.sta7.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;

import com.mi.sta7.Preferences;
import com.mi.sta7.bean.DateBean;
import com.mi.sta7.bean.HistoryBean;
import com.mi.sta7.bean.HistoryItemBean;
import com.mi.sta7.bean.ImageBean;
import com.mi.sta7.bean.MovieBean;
import com.mi.sta7.bean.NewBean;
import com.mi.sta7.bean.OneImageBean;
import com.mi.sta7.bean.OneNewBean;
import com.mi.sta7.bean.ProgramBean;
import com.mi.sta7.bean.RespBean;
import com.mi.sta7.bean.ResponseBean;
import com.mi.sta7.bean.ShareBean;
import com.mi.sta7.bean.SingerBean;
import com.mi.sta7.bean.SocialBean;
import com.mi.sta7.bean.commentsBean;
import com.mi.sta7.mangerdate.InitDate;
import com.mi.sta7.mangerdate.MangerDate;
import com.mi.sta7.ui.ShowSingerActivity;

public class JsonForDate {
	private static final String LOG_TAG = "JSONFORDATE";
	private static JSONObject jObject;
	private static JSONArray jArray;
	private static NewBean newBean;
	private static int rv;
	
   public static void getMovie(String string,MovieBean bean)throws Exception
   {
	   if (string==null) {
		return;
	}
		   jObject=new JSONObject(string);
		   bean.setId(jObject.getString("id"));
		   bean.setContent(jObject.getString("content"));
		   bean.setImageUrl(jObject.getString("image"));
		   bean.setPlayUrl(jObject.getString("playurl"));
		   bean.setSource(jObject.getString("source"));
		   bean.setTime(jObject.getString("created"));
		   bean.setTitle(jObject.getString("title"));

   }
	public static String getPoll(String string) throws Exception {
		jObject = new JSONObject(string);
		if (jObject.getInt("rv") != 0) {
			return null;
		}
		return jObject.getString("remainder");
	}

	public static void getResponse(String string, List<ResponseBean> beans)
		 {
		ResponseBean bean;
		if (string == null) {
			return;
		}
		try {
			jObject = new JSONObject(string);
			   String rvString=jObject.getString("rv");
				String data = jObject.getString("data");
				jArray = new JSONArray(data);
				for (int i = 0; i < jArray.length(); i++) {
				JSONArray	jArray2 = jArray.getJSONArray(i);
				for (int j = 0; j < jArray2.length(); j++) {
					jObject = jArray2.getJSONObject(j);
					bean = new ResponseBean();
					bean.setId(jObject.getInt("id"));
					bean.setTitle(jObject.getString("title"));
					bean.setBig_image(jObject.getString("big_image"));
					bean.setCreated(jObject.getString("created"));
					bean.setDescription(jObject.getString("description"));
					bean.setType(jObject.getString("type"));
					bean.setImage(jObject.getString("image"));
					beans.add(bean);
				}
				}
		} catch (Exception e) {
		}
	}

	public static void getProgram(String string, ProgramBean programBean) throws JSONException {
		JSONObject respJsonObject = new JSONObject(string);
		rv = respJsonObject.getInt("rv");
		if (rv != 0) return;
		programBean.setId_item(respJsonObject.getString("id_item"));
		programBean.setInfo(respJsonObject.getString("info"));
		programBean.setStartTime(Tools.timeConvert(respJsonObject.getString("start")));
		programBean.setImage(respJsonObject.getString("pic"));
		programBean.setEndTime(Tools.timeConvert(respJsonObject.getString("end")));
	}
	
	public static void getShareInfo(String string) throws JSONException {
		jObject = new JSONObject(string);
		rv = jObject.getInt("rv");
		if (rv != 0) {
			return;
		}
		JSONArray shareJsonArray = jObject.getJSONArray("nav");
		for (int i = 0; i < shareJsonArray.length(); i++) {
			JSONObject itemObject = (JSONObject) shareJsonArray.get(i);
			if (itemObject.getString("title").equals("sina")) {
				MangerDate.sinaShare = itemObject.getString("share");
				MangerDate.sinaInvite = itemObject.getString("invite");
				MangerDate.sinaSend = itemObject.getString("send");
			} else if (itemObject.getString("title").equals("wechat")) {
				MangerDate.weiXinShare = itemObject.getString("share");
				MangerDate.weixinInvite = itemObject.getString("invite");
			}
		}
	}
	
	/**
	 * 解析返回的歌手数据
	 * @param resp 返回的 json string
	 * @param singerBeans 歌手表
	 * @throws JSONException
	 */
	public static void getAllSinger(String resp, List<SingerBean> singerBeans) throws JSONException {
		if (resp == null) return;
		JSONObject respJsonObject = new JSONObject(resp);
		if (respJsonObject.getInt("rv") != 0) return;
		JSONArray singersArray = respJsonObject.getJSONArray("singers");
		for (int i = 0; i < singersArray.length(); i++) {
			SingerBean singerBean = new SingerBean();
			JSONObject singerJSONObject  = singersArray.getJSONObject(i);
			singerBean.setId(singerJSONObject.getString("id_singer"));
			singerBean.setImage(singerJSONObject.getString("pic"));
			singerBean.setSex(singerJSONObject.getString("sex"));
			singerBean.setSingerName(singerJSONObject.getString("name"));
			singerBean.setInfo(singerJSONObject.getString("info"));
			singerBean.setRank(singerJSONObject.getInt("rank"));
			singerBean.setAge(singerJSONObject.getInt("age"));
			singerBean.setSingerVotes(0);
			singerBeans.add(singerBean);
		}
	}
	
	public static void getSingerPollSid(String string, boolean isall)
			throws Exception {
		jObject = new JSONObject(string);
		SingerBean singerBean = null;
		int rv = jObject.getInt("rv");
		if (rv != 0) {
			return;
		}
		String remainder = jObject.getString("remainder");
		ShowSingerActivity.setCountVote(Integer.valueOf(remainder));
		Preferences.setSettings("countVote", ShowSingerActivity.getCountVote());
		String votesString = jObject.getString("votes");
		jArray = new JSONArray(votesString);
		if (isall) {
			for (int j = 0; j < MangerDate.allSingerBeans.size(); j++) {
				singerBean = MangerDate.allSingerBeans.get(j);
			
				for (int i = 0; i < jArray.length(); i++) {
					MangerDate.singerPool.put(singerBean.getId(), jArray
							.getJSONObject(j).getString(singerBean.getId()));
				}
			}
		} else {
			for (int j = 0; j < MangerDate.singerBeans.size(); j++) {
				singerBean = MangerDate.singerBeans.get(j);
			for (int i = 0; i < jArray.length(); i++) {
					MangerDate.singerPool.put(singerBean.getId(), jArray
							.getJSONObject(j).getString(singerBean.getId()));
				}
			}
		}

	}

	public static void getChannelBean(String string) throws JSONException {
		if (!InitDate.getDateBeans().isEmpty()) {
			return;
		}
		String navString;
		DateBean bean = null;
		if (string == null) return;
		jObject = new JSONObject(string);
		int rv = jObject.getInt("rv");
		if (rv != 0) return;
		navString = jObject.getString("nav");
		jArray = new JSONArray(navString);
		for (int i = 0; i < jArray.length(); i++) {
			bean = new DateBean();
			jObject = jArray.getJSONObject(i);
			bean.setId(jObject.getInt("id"));
			bean.setApi(jObject.getString("api"));
			bean.setTitle(jObject.getString("title"));
			bean.setType(jObject.getString("type"));
			bean.setImgUrl(jObject.getString("img"));
			bean.setPic(jObject.getString("pic"));
			bean.setKey(jObject.getString("key"));
			InitDate.getDateBeans().add(bean);
		}

	}

	public static void getNewDate(List<NewBean> newBeans, String string)
			throws Exception {
		if (null == string) {
			return;
		}
		jArray = new JSONArray(string);
		for (int i = 0; i < jArray.length(); i++) {
			jObject = jArray.getJSONObject(i);
			newBean = new NewBean();
			newBean.setId(jObject.getInt("id"));
			newBean.setBig_image(jObject.getString("big_image"));
			newBean.setContent(jObject.getString("description"));
			newBean.setTime(jObject.getString("created"));
			newBean.setImage(jObject.getString("image"));
			newBean.setUrl(jObject.getString("url"));
			newBean.setTitle(jObject.getString("title"));
			newBeans.add(newBean);
		}

	}

	public static void getOneNew(OneNewBean oneNewBean, String string)
			throws Exception {
		if (null == string) {
			return;
		}
		jObject = new JSONObject(string);
		oneNewBean.setId(jObject.getInt("id"));
		oneNewBean.setContent(jObject.getString("content"));
		oneNewBean.setImage(jObject.getString("image"));
		oneNewBean.setSource(jObject.getString("source"));
		oneNewBean.setTime(jObject.getString("created"));
		oneNewBean.setTitle(jObject.getString("title"));
		oneNewBean.setUrl(jObject.getString("url"));

	}

	public static void getImageList(List<ImageBean> imBeans, String string)
			throws Exception {
		ImageBean imageBean;
		if (null == string) {
			return;
		}
		jArray = new JSONArray(string);
		jObject = new JSONObject();
		for (int i = 0; i < jArray.length(); i++) {

			jObject = jArray.getJSONObject(i);
			imageBean = new ImageBean();
			imageBean.setId(jObject.getInt("id"));
			imageBean.setContent(jObject.getString("description"));
			imageBean.setImgUrl(jObject.getString("image"));
			imageBean.setIsPhoto(jObject.getInt("is_photo"));
			imageBean.setTitle(jObject.getString("title"));
			imBeans.add(imageBean);
		}

	}

	public static void getImages(List<OneImageBean> imageBean, String string)
			throws Exception {
		OneImageBean oneImageBean;
		if (null == string) {
			return;
		}
		jArray = new JSONArray(string);
		for (int i = 0; i < jArray.length(); i++) {
			jObject = jArray.getJSONObject(i);
			if (jObject != null) {
				oneImageBean = new OneImageBean();
				oneImageBean.setTitle(jObject.getString("title"));
				oneImageBean.setImage(jObject.getString("image"));
				oneImageBean.setContent(jObject.getString("description"));
				imageBean.add(oneImageBean);
			}
		}

	}
	
	public static void getCommentsBeans(List<commentsBean> commentsBeans, String jsonString) throws JSONException {
		if(jsonString.equals("")) return;
		JSONObject respJsonObject = new JSONObject(jsonString);
		JSONArray commentsArray = respJsonObject.getJSONArray("comments");
		for(int i=0; i<commentsArray.length(); i++) {
			JSONObject commmentObject = (JSONObject)commentsArray.get(i);
			commentsBean commentsBean = new commentsBean();
			String create_at = Tools.sinaTimeConvert(commmentObject.getString("created_at"));
			commentsBean.setTime(create_at.substring(5, 16));
			commentsBean.setText(commmentObject.getString("text"));
			JSONObject usrJsonObject = commmentObject.getJSONObject("user");
			commentsBean.setUsr_name(usrJsonObject.getString("screen_name"));
			commentsBean.setPic(usrJsonObject.getString("profile_image_url"));
			commentsBeans.add(commentsBean);
		}
	}
	
	public static void setStatusResp(RespBean respBean, String jsonString) throws JSONException {
		if(respBean==null || jsonString.equals("")) return;
		JSONObject respJsonObject = new JSONObject(jsonString);
		respBean.setTime(Tools.sinaTimeConvert(respJsonObject.getString("created_at")));
		respBean.setText(respJsonObject.getString("text"));
		respBean.setIdStr("idstr");
	}
	
	public static void setCollectResp(RespBean respBean, String jsonString) throws JSONException {
		if(respBean==null || jsonString.equals("")) return;
		JSONObject jsonObject = new JSONObject(jsonString);
		JSONObject respJsonObject = jsonObject.getJSONObject("status");
		respBean.setTime(Tools.sinaTimeConvert(respJsonObject.getString("created_at")));
		respBean.setText(respJsonObject.getString("text"));
		respBean.setIdStr("idstr");
	}
	
	public static void getHistoryBean(List<HistoryBean> historyBeans, String resp) throws JSONException {
		JSONObject respJsonObject = new JSONObject(resp);
		if(!respJsonObject.getString("rv").equals("0")) return;
		JSONArray itemJsonArray = respJsonObject.getJSONArray("item");
		for(int i=0; i<itemJsonArray.length(); i++) {
			HistoryBean historyBean = new HistoryBean();
			historyBean.setHistory_name(((JSONObject)itemJsonArray.get(i)).getString("name"));
			historyBean.setHistory_no(((JSONObject)itemJsonArray.get(i)).getString("id_item"));
			historyBean.setHistory_url(((JSONObject)itemJsonArray.get(i)).getString("pic"));
			historyBeans.add(historyBean);
		}
	}
	
	public static void getAV(String resp, MangerDate.AV avBean) throws JSONException {
		JSONObject respJsonObject = new JSONObject(resp);
		if (respJsonObject.getString("rv").equals("0")) {
			JSONObject msgJsonObject = respJsonObject.getJSONObject("msg");
			avBean.av = msgJsonObject.getString("av");
			avBean.av_url = msgJsonObject.getString("av_url");
		}
	}
	
	public static void getMentorData(String resp, Map<Integer, SocialBean> mapSocialMap) throws JSONException {
		JSONObject respObject = new JSONObject(resp);
		if (!respObject.getString("rv").equals("0")) return;
		mapSocialMap.clear();
		JSONArray jsonArray = respObject.getJSONArray("nav");
		for (int i=0; i<jsonArray.length(); i++) {
			SocialBean socialBean = new SocialBean();
			JSONObject itemObject = (JSONObject)jsonArray.get(i);
			socialBean.setTitle(itemObject.getString("title"));
			String idsString = itemObject.getString("id");
			socialBean.setIdsString(idsString);
			String ids[] = idsString.split(",");
			for (int j=0; j<ids.length; j++) {
				socialBean.getIds().add(ids[j]);
			}
			mapSocialMap.put(i, socialBean);
		}
	}
	
	/**
	 * 将传进的字串解析成 shareBean 的 list 形式
	 * @param resp 传进的字串
	 * @throws JSONException 
	 */
	public static void getWeibo(String resp, List<ShareBean> shareBeans) throws JSONException {
		JSONObject respObject = new JSONObject(resp);
		JSONArray statuseArray = respObject.getJSONArray("statuses");
		for (int i=0; i<statuseArray.length(); i++) {
			JSONObject jsonObject = (JSONObject)statuseArray.get(i);
			String id = jsonObject.getString("idstr");
			String time = Tools.sinaTimeConvert(jsonObject.getString("created_at"));
			String created_at = time.substring(5, 16);
			String text = jsonObject.getString("text");
			boolean isFavorited = jsonObject.getBoolean("favorited");
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
			shareBean.setFavorited(isFavorited);
			shareBeans.add(shareBean);
		}
	}
	
	public static void getSingerVotes(String resp, List<SingerBean> singerBeans) throws JSONException {
		JSONObject respJsonObject = new JSONObject(resp);
		if(!respJsonObject.getString("rv").equals("0")) return;
		JSONArray votesArray = respJsonObject.getJSONArray("votes");
		for (int i=0; i<singerBeans.size(); i++) {
			for (int j=0; j<votesArray.length(); j++) {
				String num = null;
				try {
					num = votesArray.getJSONObject(j).getString(singerBeans.get(i).getId());
				} catch (Exception e) {
				} finally {
					if (num != null) {
						singerBeans.get(i).setSingerVotes(Integer.parseInt(num));
						num = null;
					}
				}
			}
		}
	}
	
	public static void voteSinger(String resp, List<SingerBean> singerBeans) throws JSONException {
		JSONObject respJsonObject = new JSONObject(resp);
		if(!respJsonObject.getString("rv").equals("0")) return;
		int remainder = respJsonObject.getInt("remainder");
		if(remainder!=-1) {
			Preferences.setSettings("countVote", remainder);
			Log.d(LOG_TAG, "countVote="+Preferences.getSettings("countVote", 0));
			ShowSingerActivity.setCountVote(Preferences.getSettings("countVote", 0));
		}
		JSONArray votesArray = respJsonObject.getJSONArray("votes");
		for (int i=0; i<singerBeans.size(); i++) {
			for (int j=0; j<votesArray.length(); j++) {
				String num = null;
				try {
					num = votesArray.getJSONObject(j).getString(singerBeans.get(i).getId());
				} catch (Exception e) {
				} finally {
					if (num != null) {
						singerBeans.get(i).setSingerVotes(Integer.parseInt(num));
						num = null;
					}
				}
			}
		}
	}
	
	public static void getHistoryItem(String resp, List<SingerBean> singerBeans, HistoryItemBean historyItemBean) throws JSONException {
		JSONObject respJsonObject = new JSONObject(resp);
		if(!respJsonObject.getString("rv").equals("0")) return;
		String info = respJsonObject.getString("info");
		String pic = respJsonObject.getString("pic");
		String join = respJsonObject.getString("join");
		String like = respJsonObject.getString("like");
		String dislike = respJsonObject.getString("dislike");
		String note = respJsonObject.getString("note");
		String votes = respJsonObject.getString("votes");
		if(info!=null) historyItemBean.setHistory_item_title(info);
		if(join!=null) historyItemBean.setInterract_all(join);
		if(like!=null) historyItemBean.setLike_all(like);
		if(dislike!=null) historyItemBean.setDislike_all(dislike);
		if(note!=null) historyItemBean.setKeep_eye(note);
		if(votes!=null) historyItemBean.setVotes_all(votes);
		JSONArray votesArray = respJsonObject.getJSONArray("singer");
		for (int i=0; i<singerBeans.size(); i++) {
			for (int j=0; j<votesArray.length(); j++) {
				String num = null;
				try {
					num = votesArray.getJSONObject(j).getString(singerBeans.get(i).getId());
				} catch (Exception e) {
				} finally {
					if (num != null) {
						singerBeans.get(i).setSingerVotes(Integer.parseInt(num));
						num = null;
					}
				}
			}
		}
	}
}
