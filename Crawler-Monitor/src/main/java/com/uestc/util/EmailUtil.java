package com.uestc.util;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 * 邮件发送工具类
 * @author 王俊
 *
 */
public class EmailUtil {
	public static void sendEmail(String subject,String message) {  
        //发送email  
        HtmlEmail email = new HtmlEmail();  
        try {  
            //这里是SMTP发送服务器的名字：qq的如下："smtp.qq.com"  
            email.setHostName(LoadPropertyUtil.getEmail("mail.host"));  
            // 字符编码集的设置  
            email.setCharset(LoadPropertyUtil.getEmail("mail.encoding"));  
            //收件人的邮箱  
            email.addTo(LoadPropertyUtil.getEmail("mail.to"));  
            
            //设置是否加密
            email.setSSLOnConnect(false);
            
            email.setSmtpPort(Integer.parseInt(LoadPropertyUtil.getEmail("mail.smtp.port")));
            // 发送人的邮箱  
            email.setFrom(LoadPropertyUtil.getEmail("mail.from"), LoadPropertyUtil.getEmail("mail.nickname"));  
            // 如果需要认证信息的话，设置认证：用户名-密码。分别为发件人在邮件服务器上的注册名称和授权码 
            email.setAuthentication(LoadPropertyUtil.getEmail("mail.username"), LoadPropertyUtil.getEmail("mail.password"));  
            // 要发送的邮件主题  
            email.setSubject(subject);  
            // 要发送的信息，由于使用了HtmlEmail，可以在邮件内容中使用HTML标签  
            email.setMsg(message);  
            // 发送  
            email.send();   
        } catch (EmailException e) {  
            e.printStackTrace();  
        }

    }
    public static void main(String[] args){
	    sendEmail("nihao","<p>efeaaafe</p>");
    }
}
