package com.kyee.mail.base;

import java.io.InputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import org.apache.log4j.Logger;

import HRP.Comm.Util.DotNetToJavaStringHelper;

/**
 * 邮件实体类
 */
public class MailInfo extends Authenticator {
	/**
	 * 发送服务器
	 */
	private String mailServerHost;
	/**
	 * 发送端口
	 */
	private String mailServerPort;
	/**
	 * 接受服务端口
	 */
	private String mailReceiveServer;
	/**
	 * 接受服务端口
	 */
	private int mailReceiveServerPort;
	/**
	 * 发送方邮箱
	 */
	private String fromAddress;
	/**
	 * 接收方邮箱
	 */
	private String[] toAddress;
	/**
	 * 设置抄送地址
	 */
	private String[] cCAddress;
	/**
	 * 邮箱用户名
	 */
	private String userName;
	/**
	 * 邮箱密码
	 */
	private String password;
	/**
	 * 是否需要账户校验
	 */
	private boolean validate = false;
	/**
	 * 邮件主题
	 */
	private String subject;
	/**
	 * 邮件内容
	 */
	private String content;

	/**
	 * 邮件附件
	 */
	private String[] attachFileNames;

	private static Logger logger = Logger.getLogger(MailInfo.class);

	/**
	 * 读取邮件服务信息
	 */
	public MailInfo() {

		InputStream is = this.getClass().getClassLoader().getResourceAsStream("mail-cfg.properties");
		Properties prop = new Properties();
		try {
			prop.load(is);
			is.close();
		} catch (Exception e) {

			logger.info("读取配置文件mail-cfg.properties时发生异常！详细：" + e.getMessage());
		}
		mailServerHost = prop.getProperty("smtpServer");
		mailServerPort = prop.getProperty("smtpServerPort");
		mailReceiveServer = prop.getProperty("mailReceiveServer");
		String tempMailReceiveServerPort = prop.getProperty("mailReceiveServerPort");
		if (!DotNetToJavaStringHelper.isNullOrEmpty(tempMailReceiveServerPort)) {
			mailReceiveServerPort = Integer.parseInt(tempMailReceiveServerPort);
		}
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(userName, password);
	}

	public String getMailServerHost() {
		return mailServerHost;
	}

	public void setMailServerHost(String mailServerHost) {
		this.mailServerHost = mailServerHost;
	}

	public String getMailServerPort() {
		return mailServerPort;
	}

	public String getMailReceiveServer() {
		return mailReceiveServer;
	}

	public void setMailReceiveServer(String mailReceiveServer) {
		this.mailReceiveServer = mailReceiveServer;
	}

	public void setMailServerPort(String mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	public int getMailReceiveServerPort() {
		return mailReceiveServerPort;
	}

	public void setMailReceiveServerPort(int mailReceiveServerPort) {
		this.mailReceiveServerPort = mailReceiveServerPort;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public String[] getAttachFileNames() {
		return attachFileNames;
	}

	public void setAttachFileNames(String[] fileNames) {
		this.attachFileNames = fileNames;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String[] getToAddress() {
		return toAddress;
	}

	public void setToAddress(String[] toAddress) {
		this.toAddress = toAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String textContent) {
		this.content = textContent;
	}

	public String[] getcCAddress() {
		return cCAddress;
	}

	public void setcCAddress(String[] cCAddress) {
		this.cCAddress = cCAddress;
	}

	/**
	 * 返回邮箱的基本配置
	 */
	public Properties getProperties() {

		Properties properties = new Properties();
		properties.put("mail.smtp.host", this.getMailServerHost());
		properties.put("mail.smtp.port", this.getMailServerPort());
		properties.put("mail.smtp.auth", validate ? "true" : "false");
		return properties;
	}
}
