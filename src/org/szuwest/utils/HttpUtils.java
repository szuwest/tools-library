package org.szuwest.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

import my.base.App;
import my.base.net.HttpException;
import my.base.util.LogUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

public class HttpUtils {

	private static final String tag = "HttpUtils";
	private final static String PREFER_APN_URI = "content://telephony/carriers/preferapn";

	/**
	 * Performs HTTP GET using Apache HTTP Client v 4
	 * 
	 * @param url
	 * @return response in string
	 * @throws HttpException 
	 */
	public static String doGetText(String url, String parameters, boolean isProxyAPN) throws HttpException {
		String httpResponseStr = null;

		HttpEntity httpEntity = null;
		try {
			if(isProxyAPN)
				httpEntity = doGetEntity2(url, parameters);
			else
				httpEntity = doGetEntity(url + parameters);
			
			if (httpEntity == null) {
				return null;
			}
			httpResponseStr = EntityUtils.toString(httpEntity);
			LogUtils.i(tag, "--------------------");
			LogUtils.i(tag, "response size: " + httpResponseStr.length());
			LogUtils.i(tag, "response: ");
			LogUtils.i(tag, httpResponseStr);
			LogUtils.i(tag, "--------------------");
		} catch (UnknownHostException e) {
			throw new HttpException(e.getLocalizedMessage());
		} catch (SocketException e){
			throw new HttpException(e.getLocalizedMessage());
		} catch (ClientProtocolException e) {
			throw new HttpException(e.getLocalizedMessage());
		} catch (IOException e) {
			throw new HttpException(e.getLocalizedMessage());
		}

		return httpResponseStr;
	}
	
	private static HttpEntity doGetEntity(String url)
		    throws UnknownHostException, SocketException, ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(url);
		
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 15000);
		HttpConnectionParams.setSoTimeout(httpParams, 30000);
		
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpResponse httpResponse = client.execute(httpGet);
		
		int responseStatusCode = httpResponse.getStatusLine().getStatusCode();
		if (responseStatusCode == 200) {
			return httpResponse.getEntity();
		} else {
			LogUtils.e(tag, responseStatusCode + "");
			return null;
		}
	}
	
	
	//wap代理联网方式，该方法测试有时出错，有时又没有问题，暂不清楚原因
//	public static HttpEntity doGetEntity2(String url, String parameters) throws ClientProtocolException, IOException{
//		URL tmpurl;
//		try {
//			tmpurl = new URL(url);
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//			return null;
//		}
//		String tmpUrl = "http://10.0.0.172" + tmpurl.getPath() + parameters;  
//		HttpClient client = new DefaultHttpClient();  
//		HttpGet request = new HttpGet(tmpUrl);  
//		request.setHeader("X-Online-Host", tmpurl.getHost());  
//		request.setHeader("User-Agent", "some user agent");
//		HttpResponse httpResponse = client.execute(request);
//		LogUtils.d(tag, "request-------------------------------");
//		LogUtils.d(tag, request.getURI().toString());
//		Header[] headers = request.getAllHeaders();
//		for (int i = 0; i < headers.length; i++) {
//			LogUtils.d(tag, headers[i].toString());
//		}
//		LogUtils.d(tag, "respose-------------------------------");
//		headers = httpResponse.getAllHeaders();
//		for (int i = 0; i < headers.length; i++) {
//			LogUtils.d(tag, headers[i].toString());
//		}
//		
//		int responseStatusCode = httpResponse.getStatusLine().getStatusCode();
//		if (responseStatusCode == 200) {
//			return httpResponse.getEntity();
//		} else {
//			LogUtils.e(tag, responseStatusCode + "");
//			return null;
//		}
//	}
	//wap代理联网方式
	private static HttpEntity doGetEntity2(String url, String parameters)
			throws ClientProtocolException, IOException {
		URL tmpurl = new URL(url + parameters);
		HttpHost proxy = new HttpHost("10.0.0.172", 80, "http");
		HttpHost target = new HttpHost(tmpurl.getHost(), 80, "http");

		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);

		HttpGet req = new HttpGet(url + parameters);

		System.out.println("executing request to " + target + " via " + proxy);
		HttpResponse rsp = httpclient.execute(target, req);

		System.out.println("----------------------------------------");
		System.out.println(rsp.getStatusLine());
		Header[] headers = rsp.getAllHeaders();
		for (int i = 0; i < headers.length; i++) {
			System.out.println(headers[i]);
		}
		System.out.println("----------------------------------------");


		int responseStatusCode = rsp.getStatusLine().getStatusCode();
		if (responseStatusCode == 200) {
			return rsp.getEntity();
		} else {
			LogUtils.e(tag, responseStatusCode + "");
			return null;
		}
	}


	// 获取当前APN属性是否是通过代理上网（cmwap或者3gwap）
	public static boolean isProxyAPN(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo == null|| networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
			return false;
		
		Uri PREFERRED_APN_URI = Uri.parse(PREFER_APN_URI);
		Cursor cursor_current = context.getContentResolver().query(
				PREFERRED_APN_URI, null, null, null, null);
		if (cursor_current != null && cursor_current.moveToFirst()) {
			String proxy = cursor_current.getString(cursor_current
					.getColumnIndex("proxy"));
			String apn = cursor_current.getString(cursor_current
					.getColumnIndex("apn"));
			String port = cursor_current.getString(cursor_current
					.getColumnIndex("port"));
			String current = cursor_current.getString(cursor_current
					.getColumnIndex("current"));
			cursor_current.close();

			if (proxy == null || apn == null || port == null || current == null
					|| proxy.equals("") || port.equals("")) {
				return false;
			}
			LogUtils.d(tag, "proxy=" + proxy + " port=" + port + " current=" + current);
			if ((proxy.equals("10.0.0.172") || proxy.equals("010.000.000.172"))
					&& port.equals("80")&& current.equals("1")) {
//				if ((proxy.equals("10.0.0.172") || proxy.equals("010.000.000.172"))
//						&& port.equals("80") && apn.equals("cmwap")
//						&& current.equals("1")) {
				return true;
			}
		}
		return false;
	}

	// 检查是否存在cmwap或者3gwap网络，它们都是通过代理上网
	public static boolean checkHasWapAPN(Context context) {
		Uri APN_TABLE_URI = Uri.parse("content://telephony/carriers");
		Cursor cursor_need = context.getContentResolver().query(APN_TABLE_URI,
				null, null, null, null);
		boolean flag = false;
		while (cursor_need != null && cursor_need.moveToNext()) {
			String id = cursor_need
					.getString(cursor_need.getColumnIndex("_id"));
			String port = cursor_need.getString(cursor_need
					.getColumnIndex("port"));
			String proxy = cursor_need.getString(cursor_need
					.getColumnIndex("proxy"));
			String current = cursor_need.getString(cursor_need
					.getColumnIndex("current"));
			String mmsc = cursor_need.getString(cursor_need
					.getColumnIndex("mmsc"));
			LogUtils.d(tag, "proxy=" + proxy + " port=" + port + " current="
					+ current + " id=" + id);
			if (proxy == null || port == null || current == null) {
				continue;
			}
			if ((proxy.equals("10.0.0.172") || proxy.equals("010.000.000.172"))
					&& port.equals("80") && current.equals("1") && mmsc == null) {
				// APN_Id = id;
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	public static boolean downloadFile(Context context, File file, URL dlurl, boolean isProxyAPN) throws IOException {

		final int BUFFER_SIZE = 1024*8;
        BufferedInputStream bis = null;
        byte[] buf = new byte[BUFFER_SIZE];
        HttpURLConnection conn = null;
        try {
        	if (isProxyAPN) {
				String domain = "";
				String path = "";
				String requestURL = dlurl.toString();
				int index = requestURL.indexOf("http://");
				if (index >= 0) {
					requestURL = requestURL.substring(index + 7);
					index = requestURL.indexOf("/");
					if (index > 0) {
						domain = requestURL.substring(0, index);
						path = requestURL.substring(index);
					} else {
						domain = requestURL;
						path = "/";
					}
				}
				LogUtils.d(App.tag, "domain:" + domain + " | path:" + path);
			    URL url = new URL("http://10.0.0.172" + path);   
			    conn = (HttpURLConnection) url.openConnection();   
			    conn.setRequestProperty("X-Online-Host", domain);   
			} else {
				conn = (HttpURLConnection) dlurl.openConnection();
			}
        	conn.setConnectTimeout(60 * 1000);
            conn.setRequestMethod("GET");
            conn.setAllowUserInteraction(true);
            conn.setRequestProperty("Connection", "Keep-Alive");
//            InputStream in = conn.getInputStream();
            bis = new BufferedInputStream(conn.getInputStream(), BUFFER_SIZE);
            
            String fileApath = file.getAbsolutePath();
            FileOutputStream fOut;
            if(fileApath.startsWith("/data/data"))
            	fOut = context.openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);
            else
            	fOut = new FileOutputStream(file);
            int lenght = 0;
            while ( (lenght = bis.read(buf, 0, BUFFER_SIZE)) > 0) {

            	fOut.write(buf, 0, lenght);
            }

            bis.close();
            fOut.close();
        } catch (IOException e) {
        	e.printStackTrace();
            Log.e(file.getAbsolutePath() , e.getMessage());
            return false;
        }
        Log.d(file.getAbsolutePath(), "download done!" );
		return true;
	}
}
