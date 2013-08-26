package com.mi.sta7.finaldate;


public class HttpUrl {
	public static final String SERVER_URL_PRIX = "http://42.120.19.68:8443/mi13/?";//测试118.145.12.100   正式版 42.120.19.68
	public static final String GET_HOME_News="http://api.3g.hunantv.com/fih/news/latest?limit=20&since_id=1&max_id=10";
    public static final String GET_ONE_NEW="http://api.3g.hunantv.com/fih/hn02?&type=mix&id=";
    public static final String GET_PHOTO="http://api.3g.hunantv.com/iphone/photos?limit=";
    public static final String GET_ONE_IMAGE="http://api.3g.hunantv.com/iphone/photos/?id=";
    public static final String GET_PASS_ALL=SERVER_URL_PRIX+"scr=get&type=item_history&id_item=all&mac_wifi=";
    public static final String GET_PASS_ONE=SERVER_URL_PRIX+"scr=get&type=item_history";
    public static final String GET_ALL_SINGER=SERVER_URL_PRIX+"scr=get&type=singer&";
    public static final String GET_SINGER_POLL=SERVER_URL_PRIX+"scr=get&type=votes&";
    public static final String SEND_POLLO=SERVER_URL_PRIX+"scr=rec&type=vote&mac_wifi=";
    public static final String GET_ONE_MOVIE="http://api.3g.hunantv.com/fih/hn02?&type=av&id=";
}
