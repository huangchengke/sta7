package com.mi.sta7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.mi.sta7.bean.JYData;
import com.mi.sta7.ui.MainActivity;
import com.mi.sta7.utils.Encoding;
import com.mi.sta7.utils.Encoding.RSACoder;

/**
 * 提供金鹰网登录的接口类
 * @author frand
 *
 */
public class JYApi {
	private static final String API_URL = "http://58.83.217.94/?";
	private static final String USRNAME = "fxx1990happy@163.com";
	private static final String PASSWORD = "FXX1990HAPPY";
	private static final String SECRET_KEY = "HTV_FIH_@#$%";
	private static final String INVOKER = "fskphone";
	private static Map<String, String> paramMap = new HashMap<String, String>();
	
	/**
	 * 测试方法
	 */
	public static String login(String usrname, String password) {
		String reqString = getRequestString(USRNAME, PASSWORD); // 先写成死的测试,等待调用
		// 获取远程湖卫数据
		String result = requestApi(reqString);
		return result;
	}
	
	/**
	 * 获取请求的地址
	 * @return
	 */
	public static String getRequestString(String usrname, String password) {
		String reqString = "";
		reqString += API_URL + "invoker=" + INVOKER + "&data=" +
				getLoginData(usrname, password) + "&sign=" + getSign(usrname, password);
		return reqString;
	}
	
	/**
	 * 获取金鹰网提供的 public key
	 * @return
	 * @see http://stackoverflow.com/questions/10781828/keyfactory-generatepublic-from-hardcoded-x-509-certificate
	 */
	private static String getPublicKey () {
		// Note that public key != certificate, although a certificate does include a public key
		String publicKey = "";
		InputStream in = MainActivity.getContext().getResources().openRawResource(R.raw.server);
		try {
			publicKey = read(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return publicKey;
	}

	/**
	 * 获取登录所需要的信息
	 * @param usrname 用户名
	 * @param password 密码
	 * @return
	 */
	private static String getLoginData(String usrname, String password) {
		String passwordRSA = "";
		try {
			passwordRSA = RSACoder.encryptByPublicKey( // RSA方式加密密码
					password.getBytes(), getPublicKey()).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		JYData jYData = new JYData();
		jYData.action = "login";
		jYData.username = USRNAME;
		jYData.account_type = "0";
		jYData.password = passwordRSA;
		jYData.Os = "android4.0";
		jYData.phone_type = "htc";
		String loginData = jYData.toJson();
		// 开始对 data 进行 base64 编码
		loginData = Encoding.Base64.encode(loginData.getBytes()).toString();
		return loginData;
	}
	
	private static String getSign(String usrname, String password) {
		// 开始进行 sign 的计算
		if (paramMap != null) paramMap.clear();
		paramMap.put("invoker", "fskphone");
		paramMap.put("data", getLoginData(usrname, password));
		String sign = sign(SECRET_KEY, paramMap);
		return sign;
	}
	
	/**
	 * 获取一个流当中的字节信息
	 * @param in 需要读入的流
	 * @return 返回的字串
	 * @throws IOException
	 */
	private static String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}
	
	/**
	 * 获取 url 请求中的 sign
	 * @param secret
	 * @param paramMap
	 * @return
	 */
	public static String sign(String secret, Map<String, String> paramMap) {
		// 对参数名进行字典排序
		String[] keyArray = paramMap.keySet().toArray(new String[0]);
		Arrays.sort(keyArray);
		// 拼接有序的参数名-值串
		StringBuilder stringBuilder = new StringBuilder();
		for (String key : keyArray) {
			stringBuilder.append(key).append("=").append(paramMap.get(key)).append("&");
		}
		stringBuilder.append("secret_key").append("=").append(secret);
		String codes = stringBuilder.toString().toLowerCase();
		String sign = org.apache.commons.codec.digest.DigestUtils.shaHex(codes)
				.toUpperCase();
		return sign;
	}
	
	/**
	 * 根据请求地址,获取响应结果
	 * @param queryString
	 * @return
	 */
	public static String requestApi(String queryString) {
		String resultString = "";
		try {
            URL myurl = new URL(queryString);
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", System.getProperties().
    				getProperty("http.agent") + " ShopTrekkers");
    		conn.setConnectTimeout(7000);
    		conn.setReadTimeout(2000);
    		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    		conn.setRequestProperty("Connection", "Keep-Alive");
            conn.connect();
            resultString = read(conn.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
		return resultString;
	}
}
