package com.kyee.mail.core;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.kyee.mail.base.MailInfo;

/**
 * 邮件发送基础类
 */
public abstract class MailBase {

	private static Logger logger = Logger.getLogger(MailBase.class);

	private static ThreadLocal<MailInfo> currMailInfo = new ThreadLocal<MailInfo>();
	
	/**
	 * 获取邮件对象
	 */
	private static MailInfo getMailInfo(){
		
		MailInfo mail = currMailInfo.get();
		if(mail == null){
			mail = new MailInfo();
			currMailInfo.set(mail);
		}
		return mail;
	}
	
	/**
	 * 获取邮件session，不进行密码验证器校验
	 */
	public final Session getMailSession(MailInfo mailInfo){

		Session mailSession = null;
		try {
			Properties properties = mailInfo.getProperties();
			mailSession = Session.getInstance(properties, mailInfo);
		} catch (Exception e) {
			logger.info("获取邮件session时发生异常！详细：" + e.getMessage());
		}
		return mailSession;
	}

	/**
	 * 获取邮件的地址
	 */
	public Address getMailAddress(String mailAddress){
		
		Address address = null;
		try {
			address = new InternetAddress(mailAddress);
		} catch (Exception e) {
			logger.info("获取邮件的地址时发生异常！详细：" + e.getMessage());
		}
		return address;
	}

	/**
	 * 构建邮件抄送地址
	 */
	public Address[] getMailAddress(String[] mailAddress){
		
		Address[] address = null;
		try {
			if (mailAddress == null || mailAddress.length <= 0) {
				return null;
			}
			int len = mailAddress.length;
			address = new Address[len];
			for (int i = 0; i < len; i++) {
				Address addr = new InternetAddress(mailAddress[i]);
				address[i] = addr;
			}
		} catch (Exception e) {
			logger.info("构建邮件抄送地址时发生异常！详细：" + e.getMessage());
		}
		return address;
	}

	/**
	 * 构建消息对象
	 */
	public final Message getMailMessage(MailInfo mailEntity, Session mailSession){

		Message mailMessage = null;
		try {
			mailMessage = new MimeMessage(mailSession);
			Address fromAddress = getMailAddress(mailEntity.getFromAddress());
			Address[] toAddress = getMailAddress(mailEntity.getToAddress());
			mailMessage.setFrom(fromAddress);
			if (toAddress != null && toAddress.length > 0) {
				mailMessage.setRecipients(Message.RecipientType.TO, toAddress);// 设置邮件接收的地址
			}
			Address[] cCAddress = getMailAddress(mailEntity.getcCAddress());
			if (cCAddress != null && cCAddress.length > 0) {
				mailMessage.setRecipients(Message.RecipientType.CC, cCAddress);// 设置邮件抄送地址
			}
			mailMessage.setSubject(mailEntity.getSubject());
			mailMessage.setSentDate(new Date());
			mailMessage.setContent(mailEntity.getContent(), "text/html; charset=utf-8");
		} catch (Exception e) {
			logger.info("构建消息对象时发生异常！详细：" + e.getMessage());
		}
		return mailMessage;
	}

	/**
	 * 发送邮件
	 */
	public final boolean sendMailMessage(Message mailMessage){
		
		try {
			Transport.send(mailMessage);
		} catch (Exception e) {
			logger.info("发送邮件时发生异常！详细：" + e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 测试是否可以成功连接服务器
	 */
	public final boolean testSendMail(Session mailSession, MailInfo mailInfo){
		try {
			mailSession.setDebug(true);
			Transport transport = mailSession.getTransport("smtp");
			transport.connect(mailInfo.getMailServerHost(), Integer.parseInt(mailInfo.getMailServerPort()),
					mailInfo.getFromAddress(), mailInfo.getPassword());
			transport.close();
			return true;
		} catch (MessagingException e) {
			logger.info("测试是否可以成功连接服务器时发生异常！详细：" + e.getMessage());
			return false;
		}
	}

	/**
	 * 发送邮件，调用此方法必须重写setUserInfo、setMailTemplate方法
	 * 
	 * @throws Exception
	 */
	public void sendMail(){
		
		MailInfo mailInfo = getMailInfo();

		// 1、设置账户信息（用户名、密码、发送者邮箱、收件者地址）
		setUserInfo(mailInfo);

		// 2、创建邮件模板
		setMailTemplate(mailInfo);

		// 3、获取session
		Session mailSession = getMailSession(mailInfo);

		// 4、构建消息对象
		Message mailMessage = getMailMessage(mailInfo, mailSession);

		// 5、测试是否连接
		boolean testResult = testSendMail(mailSession, mailInfo);
		
		if(testResult){
			 logger.info("邮件测试成功！");
			 // 6、发送邮件
			 boolean sendResult = sendMailMessage(mailMessage);
			 
			 if(sendResult){
				 logger.info("邮件发送成功！");
			 }else{
				 logger.error("邮件发送失败！");
			 }
		}else{
			 logger.error("邮件测试失败！");
		}
		currMailInfo.remove();

	}

	/**
	 * 设置发送者信息（必须设置发送者信息及目标邮件信息）
	 * @param FromAddress 发送者邮箱
	 * @param UserName 发送者名称
	 * @param Password 发送者密码
	 * @param ToAddress、cCAddress等
	 */
	protected void setUserInfo(MailInfo mailInfo){};

	/**
	 * 设置邮箱模板并设置邮件主题及内容（必须设置邮件主题及内容）
	 * @param subject  邮件主题
	 * @param content 邮件内容
	 */
	protected void setMailTemplate(MailInfo mailInfo){};

}
