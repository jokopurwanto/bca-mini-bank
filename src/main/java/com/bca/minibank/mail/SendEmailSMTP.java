package com.bca.minibank.mail;


import com.sun.mail.smtp.SMTPTransport;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class SendEmailSMTP {
	private String smtpServer; 		//= "smtp.gmail.com";
    private String port;			// = "587";
    private String usernameEmail; 	//= "westbankPKWT@gmail.com";
    private String passwordEmail; 	//= "westbankPKWT03";

    private String emailFrom; 		//= "westbankPKWT@gmail.com";
    private String emailTo; 		//= "hanyjulianasitio@gmail.com, billybrazzo@gmail.com";
    private String emailToCC; 		//= "";

    private String emailSubject;	// = "Selamat bergabung di WEST BANK PKWT";
    private String emailContent;		// = "Hello Java Mail \n ABC123";
    private String emailContentType;


	public SendEmailSMTP(String smtpServer, String port, String usernameEmail, String passwordEmail, String emailFrom,
			String emailTo, String emailToCC, String emailSubject, String emailContent, String emailContentType) {
		super();
		this.smtpServer = smtpServer;
		this.port = port;
		this.usernameEmail = usernameEmail;
		this.passwordEmail = passwordEmail;
		this.emailFrom = emailFrom;
		this.emailTo = emailTo;
		this.emailToCC = emailToCC;
		this.emailSubject = emailSubject;
		this.emailContent = emailContent;
		this.emailContentType = emailContentType;
	}


	public void sendEmail() {
		
        Properties prop = System.getProperties();
        prop.put("mail.smtp.host", smtpServer); 
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", port); 		
        prop.put("mail.smtp.starttls.enable", "true");
        
        Session session = Session.getInstance(prop, null);
        
        try {
            Message msg = new MimeMessage(session);
            
			// from
            msg.setFrom(new InternetAddress(emailFrom));

			// to 
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailTo, false));

			// cc
            msg.setRecipients(Message.RecipientType.CC,
                    InternetAddress.parse(emailToCC, false));

			// subject
            msg.setSubject(emailSubject);
			
			// content 
            //msg.setText(emailContent);
            msg.setContent(emailContent, emailContentType);
			
            msg.setSentDate(new Date());

			// Get SMTPTransport
            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
			
			// connect
            t.connect(smtpServer, usernameEmail, passwordEmail);
			
			// send
            t.sendMessage(msg, msg.getAllRecipients());

            //System.out.println("Response: " + t.getLastServerResponse());

            t.close();

        } catch (MessagingException e) {
            e.printStackTrace();
        }


    }

}
