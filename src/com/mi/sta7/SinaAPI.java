package com.mi.sta7;

import android.util.Log;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.net.RequestListener;

/**
 * 请求朋友分组,请求朋友列表,@好友的类
 * @author frand
 *
 */
public class SinaAPI extends WeiboAPI {
	private static final String SERVER_URL_PRIX = API_SERVER + "/friendships";
	public static final String HTTPMETHOD_GET = "GET";
	
	public SinaAPI(Oauth2AccessToken oauth2AccessToken) {
		super(oauth2AccessToken);
	}
	
	/**
	 * 获取用户的分组列表
	 * 
	 * @param listener
	 */
	public void friendsGroup(RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		request(SERVER_URL_PRIX + "/groups.json", params, HTTPMETHOD_GET, listener);
	}
	
	/**
	 * 获取某一好友分组下的成员列表
	 * @param list_id 好友分组ID，建议使用返回值里的 idstr
	 * @param count 单页返回的记录条数，默认为50，最大不超过200
	 * @param cursor 分页返回结果的游标，下一页用返回值里的next_cursor，上一页用previous_cursor，默认为0
	 * @param listener
	 */
	public void friendsList(int list_id, int count, int cursor, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("list_id", list_id);
		if (count!=0) {
			params.add("count", count);
		}
		if (cursor!=0) {
			params.add("cursor", cursor);
		}
		request(API_SERVER + "/groups/members.json", params, HTTPMETHOD_GET, listener);
	}
	
	/**
	 * at 用户时的联想建议
	 * @param keyString 搜索的关键字，必须做URLencoding
	 * @param count 返回的记录条数，默认为10，粉丝最多1000，关注最多2000
	 * @param type 联想类型，0：关注、1：粉丝
	 * @param range 联想范围，0：只联想关注人、1：只联想关注人的备注、2：全部，默认为2
	 */
	public void friendsList(String keyString, int count, int type, int range, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("q", keyString);
		params.add("count", count);
		params.add("type", type);
		params.add("range", range);
		request(API_SERVER + "/search/suggestions/at_users.json", params, HTTPMETHOD_GET, listener);
	}
	
	/**
	 * 搜索某一话题下的微博
	 * @param topic 搜索的话题关键字，必须进行URLencode，utf-8编码。
	 * @param count 单页返回的记录条数，默认为10，最大为50。
	 * @param page 返回结果的页码，默认为1。 
	 * @param listener
	 */
	public void getTopic(String topic, int count, int page, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("q", topic);
		params.add("count", count);
		params.add("page", page);
		request(API_SERVER + "/search/topics.json", params, HTTPMETHOD_GET, listener);
	}
	
	/**
	 * 批量获取指定的一批用户的微博列表 
	 * @param uids 需要查询的用户ID，用半角逗号分隔，一次最多20个
	 * @param count 单页返回的记录条数，默认为20
	 * @param page 返回结果的页码，默认为1
	 * @param base_app 是否只获取当前应用的数据。0为否（所有数据），1为是（仅当前应用），默认为0
	 * @param feature 过滤类型ID，0：全部、1：原创、2：图片、3：视频、4：音乐，默认为0
	 */
	public void getWeibo(String uids, int count, int page, int base_app, int feature, RequestListener listener) {
		Log.d("sina", "getWeibo()");
		WeiboParameters params = new WeiboParameters();
		params.add("uids", uids);
		params.add("count", count);
		params.add("page", page);
		params.add("base_app", base_app);
		params.add("feature", feature);
		String url="request="+API_SERVER + "/statuses/timeline_batch.json" + "&uids="+uids+"&count"+count+"&page="+page+"&base_app="+base_app+"&feature"+feature;
		StDB.writeActRecord(url);
		Log.d("LOG_TAG", url);
		request(API_SERVER + "/statuses/timeline_batch.json", params, HTTPMETHOD_GET, listener);
	}
	
	/**
	 * 关注一个用户, uid 和 screen_name二者可选其一
	 * @param uid 需要关注的用户ID
	 * @param screen_name 需要关注的用户昵称
	 * @param listener
	 */
	public void createFriends(String uid, String screen_name, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		if (!uid.equals("")) {
			params.add("uid", uid);
		} else if (!screen_name.equals("")) {
			params.add("screen_name", screen_name);
		}
		request(API_SERVER + "/friendships/create.json", params, HTTPMETHOD_POST, listener);
	}
	
	/**
	 * 获取用户的粉丝列表 
	 * @param uid 需要查询的用户UID
	 * @param screen_name 需要查询的用户昵称。 
	 * @param count 单页返回的记录条数，默认为50，最大不超过200
	 * @param cursor 返回结果的游标，下一页用返回值里的next_cursor，上一页用previous_cursor，默认为0
	 * @param trim_status 返回值中user字段中的status字段开关，0：返回完整status字段、1：status字段仅返回status_id，默认为1
	 * @param listener
	 */
	public void getFollowers(int uid, String screen_name, int count, int cursor, int trim_status, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		if (uid!=0) {
			params.add("uid", uid);
		} else if (!screen_name.equals("")) {
			params.add("screen_name", screen_name);
		}
		if (count!=50) {
			params.add("count", count);
		}
		if (cursor!=0) {
			params.add("cursor", cursor);
		}
		if (trim_status!=1) {
			params.add("trim_status", trim_status);
		}
		request(API_SERVER + "/friendships/followers.json", params, HTTPMETHOD_GET, listener);
	}
	
	
	/**
	 * 根据微博ID返回某条微博的评论列表
	 * @param id 需要查询的微博ID
	 * @param since_id 若指定此参数，则返回ID比since_id大的评论（即比since_id时间晚的评论），默认为0
	 * @param max_id 若指定此参数，则返回ID小于或等于max_id的评论，默认为0
	 * @param count 单页返回的记录条数，默认为50
	 * @param page 返回结果的页码，默认为1
	 * @param filter_by_author 作者筛选类型，0：全部、1：我关注的人、2：陌生人，默认为0
	 * @param listener
	 */
	public void getComments(String id, int since_id, int max_id, int count, int page, int filter_by_author, RequestListener listener) {
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("id", id);
		parameters.add("since_id", since_id);
		parameters.add("count", count);
		parameters.add("page", page);
		parameters.add("filter_by_author", filter_by_author);
		// https://api.weibo.com/2/comments/show.json
		request(API_SERVER+"/comments/show.json", parameters, HTTPMETHOD_GET, listener);
	}
	
	/**
	 * 对一条微博进行评论
	 * @param comment 评论内容，必须做URLencode，内容不超过140个汉字
	 * @param id 需要评论的微博ID
	 * @param comment_ori 当评论转发微博时，是否评论给原微博，0：否、1：是，默认为0
	 * @param listener
	 */
	public void setComments(String comment, String id, int comment_ori, RequestListener listener) {
		// https://api.weibo.com/2/comments/create.json
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("comment", comment);
		parameters.add("id", id);
		parameters.add("comment_ori", comment_ori);
		request(API_SERVER+"/comments/create.json", parameters, HTTPMETHOD_POST, listener);
	}
	
	/**
	 * 添加一条微博到收藏里
	 * @param id 要收藏的微博ID
	 * @param listener
	 */
	public void collectWeibo(String id, RequestListener listener) {
		// https://api.weibo.com/2/favorites/create.json
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("id", id);
		request(API_SERVER+"/favorites/create.json", parameters, HTTPMETHOD_POST, listener);
	}
	
	/**
	 * 删除一条微博到收藏里
	 * @param id 要收藏的微博ID
	 * @param listener
	 */
	public void destoryWeibo(String id, RequestListener listener) {
		// https://api.weibo.com/2/favorites/create.json
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("id", id);
		request(API_SERVER+"/favorites/destroy.json", parameters, HTTPMETHOD_POST, listener);
	}
	
	/**
	 * 转发一条微博
	 * @param id 要转发的微博ID
	 * @param status 添加的转发文本，必须做URLencode，内容不超过140个汉字，不填则默认为“转发微博”
	 * @param is_comment 是否在转发的同时发表评论，0：否、1：评论给当前微博、2：评论给原微博、3：都评论，默认为0
	 * @param listener
	 */
	public void repostWeibo(String id, String status, int is_comment, RequestListener listener) {
		// https://api.weibo.com/2/statuses/repost.json
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("id", id);
		parameters.add("status", status);
		parameters.add("is_comment", is_comment);
		request(API_SERVER+"/statuses/repost.json", parameters, HTTPMETHOD_POST, listener);
	}
}
