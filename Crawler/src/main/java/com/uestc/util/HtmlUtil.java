package com.uestc.util;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

/**
 * 页面解析工具类
 * @author 王俊
 */
public class HtmlUtil {

	public static String getFieldByRegex(TagNode rootNode,String xpath,String regex){
		String val = "0";
		Object[] evaluateXPath = null;
		try {
			evaluateXPath=rootNode.evaluateXPath(xpath);
			 if(evaluateXPath.length>0){
				 TagNode node = (TagNode) evaluateXPath[0];
				 return  node.getText().toString();
				 //将得到的string再处理一下
//				 Pattern numberPattern = Pattern.compile(regex, Pattern.DOTALL);
//				 number = RegexUtil.getPageInfoByRegex(node.getText().toString(), numberPattern, 0);
			 }
		} catch (XPatherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return val;
	}
	/**
	 * 获取标签属性值
	 * @param tagNode
	 * @param xpath
	 * @param att
	 * @return
	 */
	public static String getAttributeByName(TagNode tagNode,String xpath,String att){
		String result = null;
		Object[] evaluateXPath = null;
		try {
			evaluateXPath = tagNode.evaluateXPath(xpath);
			if(evaluateXPath.length>0){
				TagNode node = (TagNode) evaluateXPath[0];
				result = node.getAttributeByName(att);
			}
		} catch (XPatherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}
}
