package at.srfg.kmt.ehealth.phrs.messaging;

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.Email
import org.apache.commons.mail.SimpleEmail



public class MailService {
	Properties props;
	boolean flag_tls=true
	public MailService() {
		props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		configure();
	}

	private void configure() {
		//TODO ConfigApp.instance.config.
	}


	public void setMailProperties(Properties properties) {
		if (props == null)
			props = new Properties();
		if (properties != null && !properties.isEmpty())
			props.putAll(properties);
	}

	public Properties getMailProperties() {
		return props;
	}

	public void sendMessage(String host, int port, String username,
	String password, String from, String to, String subject, String text) {


		Email email = new SimpleEmail();
		email.setHostName(host) //smtp.gmail.com
		email.setSmtpPort(port) //587
		email.setAuthenticator(new DefaultAuthenticator(username, password));
		email.setTLS(flag_tls);
		email.setFrom(from);
		email.setSubject(subject);
		email.setMsg(text);
		email.addTo(to);
		email.send();
	}
	/*
	 Email email = new SimpleEmail();
	 email.setHostName("smtp.gmail.com");
	 email.setSmtpPort(587);
	 email.setAuthenticator(new DefaultAuthenticator("username", "password"));
	 email.setTLS(true);
	 email.setFrom("user@gmail.com");
	 email.setSubject("TestMail");
	 email.setMsg("This is a test mail ... :-)");
	 email.addTo("foo@bar.com");
	 email.send();
	 */

	private void initTest() {
		String host = "smtp.gmail.com";
		int port = 587;
		String username = "username";
		String password = "password";
		sendMessage(host, port,username,password,username,username,
				"subject - about test","the text message");
	}
	public static void main(String[] args) {
		MailService  mail = new MailService();
	}
}
