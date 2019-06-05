package com.uestc.service.impl;


import com.uestc.entity.DoubanPage;
import com.uestc.entity.ZonghengPage;
import com.uestc.runner.ZonghengTask;
import com.uestc.service.IDownLoadService;
import com.uestc.util.LoadPropertyUtil;
import com.uestc.util.RedisUtil;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HttpClient页面下载实现类
 * @author 王俊
 */
@Scope("prototype")
@Service
public class HttpClientDownLoadService implements IDownLoadService {
	HttpClientBuilder builder = null;
	CloseableHttpClient client = null;
	RedisUtil redisUtil=null;
	String ip_port=null;
	//所有的编码方式
	List<String> codes;
	String code ;//该url网页的编码
	private final static String USER_AGENT=LoadPropertyUtil.getZongheng("User-Agent");

	public HttpClientDownLoadService(){
		//初始化builder和client
		builder = HttpClients.custom();
		/******************设置动态ip***********************/
//		redisUtil = new RedisUtil();
//		//获取代理ip
//		ip_port = redisUtil.getSet("proxy");
//		if(StringUtils.isNotBlank(ip_port)){
//			String[] arr = ip_port.split(":");
//			String proxy_ip = arr[0];
//			int proxy_port = Integer.parseInt(arr[1]);
//			//设置代理
//			HttpHost proxy = new HttpHost(proxy_ip,proxy_port );
//			client = builder.setProxy(proxy).build();
//		}else{   //ip库中没有了，就使用本地ip
//			client = builder.build();
//		}
		client = builder.build();
		//所有的编码方式
		codes=new ArrayList<>();
		String temp= LoadPropertyUtil.getDecode("codes");
		for(String s:temp.split(",")){
			codes.add(s);
		}
	}
	public String download(String url) {
		// TODO Auto-generated method stub
		HttpGet request = new HttpGet(url);
		String content = null;
		try {
			request.setHeader("User-Agent",USER_AGENT);
			CloseableHttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			content = EntityUtils.toString(entity,code);
/*----------------------------------------解决中文乱码问题--------------------------------------------*/
			//得到header中的编码方式
			ContentType contentType = ContentType.get(entity);
			if (contentType != null&&contentType.getCharset()!=null) {
				//toString()中会解码  源码：Reader reader = new InputStreamReader(instream, charset);
				return content; //已经正确解码，直接返回
			}
			//如果解析不到编码方式，只能从源码中解析（先用utf-8解析得到源码在解析）
			//得到charset=之后的字符串即编码方式
			Pattern pattern=Pattern.compile(LoadPropertyUtil.getZongheng("codeRegex"));
			Matcher matcher=pattern.matcher(content);
			String temp="";
			if(matcher.find()){
				temp=matcher.group();
			}
			//从编码仓库中匹配
			for(String str:codes){
				if(temp.contains(str))
					code=str;
			}
			//只能重新下载解码
			response = client.execute(request);
			entity = response.getEntity();
			content = EntityUtils.toString(entity,code);
			return content;
		} catch(HttpHostConnectException e){
			e.printStackTrace();
			//如果当前ip不可用，从动态代理ip库里面删除
			redisUtil.deleteSet(LoadPropertyUtil.getZongheng("ipset"), ip_port);
			System.out.println("删除 ："+ip_port+"------------------------------------------------");
			new HttpClientDownLoadService().download(url);
		}catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}

}
