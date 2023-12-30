package nl.detoren.ijsco.ui.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.activation.DataHandler;
//import javax.activation.DataSource;
//import javax.activation.FileDataSource;
import org.eclipse.angus.activation.*;

import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
//import javax.mail.BodyPart;
//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.Multipart;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeBodyPart;
//import javax.mail.internet.MimeMessage;
//import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;

import nl.detoren.ijsco.ui.Mainscreen;
import nl.detoren.ijsco.ui.control.IJSCOController;

public class SendAttachmentInEmail {

	private String _to;
	private String _from;
	private String _host;
	private String _username;
	private String _password;
	Properties _props = new Properties();
	private String _subject;
	private String _bodyHeader;
	private String _bodyText;
	private String _bodyFooter;
    // Create a multipart message
    Multipart _multipart = new MimeMultipart();

    // Create the message part
    BodyPart messageBodyPart = new MimeBodyPart();

	private final static Logger logger = Logger.getLogger(Mainscreen.class.getName());

	public SendAttachmentInEmail() {	

	    // Recipient's email ID needs to be mentioned.
	    _to = "ijsco@osbo.nl";
	    // Sender's email ID needs to be mentioned
	    //String from = "ijsco.osbo@gmail.com";
	    _from = "osbojeugd@schaakrating.nl";

	    // Assuming you are sending email through relay.jangosmtp.net
	    //String host = "smtp.gmail.com";
	    _host = "mail.mijndomein.nl";
	    //final String username = "ijsco.osbo@gmail.com";//change accordingly
	    _username = "osbojeugd@schaakrating.nl";//change accordingly
	    //final String password = "vmyoSP3s0BNKCmYAB33k";//change accordingly
	    _password = "GXyVm0gaEuIlEUhBOlKs";//change accordingly
	    
	    // Properties
		_props.put("mail.smtp.auth", "true");
		_props.put("mail.smtp.host", _host);
		// STARTTLS Factory
		//props.put("mail.smtp.starttls.enable", "true");
		//props.put("mail.smtp.port", "587");
		// SSL Factory 
		_props.put("mail.smtp.port", "465");
		_props.put("mail.smtp.socketFactory.class", 
            "javax.net.ssl.SSLSocketFactory");
		_subject = "";
	}

	public boolean setSubject(String subject) {
		try {
			_subject = subject;
		}
		catch (Exception ex)
		{
			logger.log(Level.WARNING, "Problem while setting subject", ex);
			return false;
		}
		return true;
	}
	
	public boolean setBodyHeader (String bodyHeader) {
	try {
		_bodyHeader = bodyHeader;
	}
	catch (Exception ex) {
		logger.log(Level.WARNING, "Problem while setting body header", ex);
		return false;
	}
	return true;
	}

	public boolean setBodyText (String bodyText) {
	try {
		_bodyText = bodyText;
	}
	catch (Exception ex) {
		logger.log(Level.WARNING, "Problem while setting body text", ex);
		return false;
	}
	return true;
	}

	public boolean setBodyFooter (String bodyFooter) {
	try {
		_bodyFooter = bodyFooter;
	}
	catch (Exception ex) {
		logger.log(Level.WARNING, "Problem while setting body footer", ex);
		return false;
	}
	return true;
	}

	public boolean addAttachement(String attachement) throws Exception {
	    // Part two is attachment
	         // adds attachments
	         if (attachement != null ) {
                 MimeBodyPart attachPart = new MimeBodyPart();
  
	                 try {
	                     attachPart.attachFile(attachement);
	                 }
	                 catch (Exception ex) {
	                	 logger.log(Level.WARNING, "Problem while adding attachment - IOException", ex);
	                     throw ex;
	                 }
	                 this._multipart.addBodyPart(attachPart);
	         }
		return true;
	}
	
	public void send() {

		// creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(_username, _password);
            }
        };		

        Session session = Session.getInstance(_props, auth);
                
      try {
         // Create a default MimeMessage object.
         Message message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(_from));

         // Set To: header field of the header.
         message.setRecipients(Message.RecipientType.TO,
            InternetAddress.parse(_to));

         // Set Subject: header field
         message.setSubject(_subject);

         // Create the message part
         BodyPart messageBodyPart = new MimeBodyPart();
         // Now set the actual message
         messageBodyPart.setText(_bodyHeader + "\r\n\r\n" + _bodyText + "\r\n\r\n" + _bodyFooter);

         // Set text message part
         _multipart.addBodyPart(messageBodyPart);

         // Send the complete message parts
         message.setContent(_multipart);

         // Send message
         Transport.send(message);
         logger.log(Level.INFO, "Email succesvol verzonden.");
 
      } catch (MessagingException e)
      {

    	  logger.log(Level.SEVERE, "Probleem met verzenden Email. Message: " + e.getMessage() + ". Cause: " + e.getCause() + ".");
    	  JOptionPane.showMessageDialog(null, "Versturen van uitslagen naar " + _to + "  mislukt. Probeer later in het menu OSBO -> handmatig versturen opnieuw.");
      }
   }
}