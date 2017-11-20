package com.kyee.mail.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.Session;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.apache.log4j.Logger;

import com.kyee.mail.base.MailInfo;
import com.kyee.mail.core.MailBase;

import HRP.Comm.BLL.BaseBllException;
import HRP.Comm.BLL.HrpUser;
import HRP.Comm.DataBase.DotNet.DataColumn;
import HRP.Comm.DataBase.DotNet.DataRow;
import HRP.Comm.DataBase.DotNet.DataTable;
import HRP.Comm.DataBase.Helper.DBUtil;
import HRP.Comm.DataBase.Helper.HrpDataTable;
import HRP.Comm.DataBase.Helper.IDataBase;
import HRP.Comm.Util.DotNetToJavaStringHelper;

/**
 * 邮件工具类
 * 
 * @author Administrator
 *
 */
public class MailUtil extends MailBase {

	private static ThreadLocal<MailInfo> currMailInfo = new ThreadLocal<MailInfo>();
	private static Logger logger = Logger.getLogger(MailUtil.class);

	/**
	 * 此处使用一个内部类来进行维护单例
	 */
	private static class SingleFactory {

		private static MailUtil instance = new MailUtil();
	}

	/**
	 * 获取实例 returnType：HrpCommUtils
	 */
	public static MailUtil getInstance() {

		return SingleFactory.instance;
	}

	/**
	 * 发送邮件，调用此方法时必须在MailInfo实体中设置发送者、目标者、邮件标题及内容信息
	 * 
	 * @param mailInfo
	 *            returnType：void
	 */
	public boolean sendMail(MailInfo mailInfo) {

		boolean result = false;
		currMailInfo.set(mailInfo);
		// 1、获取session
		Session mailSession = getMailSession(mailInfo);

		// 2、构建消息对象
		Message mailMessage = getMailMessage(mailInfo, mailSession);

		// 3、测试是否连接
		boolean testResult = testSendMail(mailSession, mailInfo);

		if (testResult) {
			logger.info("邮件测试成功！");
			// 4、发送邮件
			result = sendMailMessage(mailMessage);

			if (result) {
				logger.info("邮件发送成功！");
			} else {
				logger.error("邮件发送失败！");
			}
		} else {
			logger.error("邮件测试失败！");
		}
		currMailInfo.remove();
		return result;
	}

	/**
	 * 
	 * <pre>
	 * 任务号:HRPCOMMDEVJAVA-853
	 * 描述：获取未读邮件
	 * 作者：徐魁
	 * 时间：2016年6月12日 下午1:28:52
	 * &#64;param mailInfo
	 * &#64;param hrpDb
	 * &#64;return
	 * &#64;throws MessagingException
	 * &#64;throws BaseBllException
	 * </pre>
	 */
	public HrpDataTable getUnreadMail(IDataBase hrpDb, String userName) throws BaseBllException {
		HrpUser hrpuser = hrpDb.getHrpUser();
		String dbName = hrpDb.getDbName();
		if (DotNetToJavaStringHelper.isNullOrEmpty(dbName)) {
			dbName = "HRP";
			String orgId = hrpuser.get_Org_Id() + "";
			if (!DotNetToJavaStringHelper.isNullOrEmpty(orgId) && !"1001".equals(orgId)) {
				dbName = orgId;
			}
		}
		String sqlQuery = "SELECT A.MESSAGE_NAME,\n" + "       A.SENDER,\n" + "       A.MESSAGE_BODY,\n"
				+ "       A.MESSAGE_STATUS,\n" + "       A.LAST_UPDATED\n" + "  FROM INBOX A\n" + " WHERE A.REPOSITORY_NAME = '" + userName + "'\n"
				+ " ORDER BY A.MESSAGE_NAME ";
		ResultSet rs = null;
		Connection conn = null;
		java.sql.PreparedStatement ps = null;
		InputStream in = null;
		
		List<DataRow> rows = new ArrayList<DataRow>();
		try {
			conn = DBUtil.getInstance().getConnection(dbName, hrpuser.get_Ora_Account(), hrpuser.get_Ora_Password());
			ps = (java.sql.PreparedStatement) conn.prepareStatement(sqlQuery);
			rs = ps.executeQuery();
			int i = 0;
			while (rs.next()) {
				++i;
				String messageStatus = rs.getString("MESSAGE_STATUS");
				if ("0".equals(messageStatus)) {
					List<DataColumn> dataColumns = new ArrayList<>();
					DataColumn messageIdColumn = new DataColumn("MESSAGE_ID", i);
					dataColumns.add(messageIdColumn);
					DataColumn senderColumn = new DataColumn("SENDER", rs.getString("SENDER"));
					dataColumns.add(senderColumn);
					oracle.sql.BLOB blob = (oracle.sql.BLOB) rs.getBlob("MESSAGE_BODY");
					in = blob.getBinaryStream(); // 建立输出流
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line = null;
					while ((line = reader.readLine()) != null) {
						if (line.startsWith("Subject: ")) {
							String[] subjectArray = line.split("[?]");
							String subject = null;
							if (subjectArray.length >= 5) {
								subject = subjectArray[3];
								String codingType = subjectArray[2];
								String textType = subjectArray[1];
								if ("B".equals(codingType)) {
									byte[] deCode = Base64.decodeBase64(subject.getBytes("utf-8"));
									subject = new String(deCode, textType);
								} else {
									byte[] deCode = QuotedPrintableCodec
											.decodeQuotedPrintable(subject.getBytes("utf-8"));
									subject = new String(deCode, textType);
								}
							}
							if (DotNetToJavaStringHelper.isNullOrEmpty(subject)) {
								subject = line.replace("Subject: ", "");
							}

							DataColumn subjectColumn = new DataColumn("SUBJECT", subject);
							dataColumns.add(subjectColumn);
							DataColumn sendTimeColumn = new DataColumn("SEND_TIME", rs.getString("LAST_UPDATED"));
							dataColumns.add(sendTimeColumn);
							break;
						}
					}
					DataRow row = new DataRow(dataColumns);
					
					rows.add(0, row);
					
				}
			}
		} catch (Exception e) {
			throw new BaseBllException(e.getMessage());
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				throw new BaseBllException(e.getMessage());
			}
		}
		HrpDataTable result = new HrpDataTable();
		DataTable table=new DataTable(rows);
		result.setDataTable(table);
		result.DataTable.Columns.add("MESSAGE_ID");// 邮件id
		result.DataTable.Columns.add("SENDER");// 发件人
		result.DataTable.Columns.add("SUBJECT");// 主题
		result.DataTable.Columns.add("SEND_TIME");// 主题
		result.setCount(result.getDataTable().getRow().size());
		return result;
	}

}
