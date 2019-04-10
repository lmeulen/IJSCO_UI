package nl.detoren.ijsco.ui.util;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;

import nl.detoren.ijsco.ui.Mainscreen;
import nl.detoren.ijsco.ui.control.IJSCOController;

public class SendAttachmentInEmail {

	private final static Logger logger = Logger.getLogger(Mainscreen.class.getName());

	public void sendAttachement(String attachement) {
      // Recipient's email ID needs to be mentioned.
      String to = "ijsco@osbo.nl";

      // Sender's email ID needs to be mentioned
      //String from = "ijsco.osbo@gmail.com";
      String from = "osbojeugd@schaakrating.nl";

      //final String username = "ijsco.osbo@gmail.com";//change accordingly
      final String username = "osbojeugd@schaakrating.nl";//change accordingly
      //final String password = "vmyoSP3s0BNKCmYAB33k";//change accordingly
      final String password = "GXyVm0gaEuIlEUhBOlKs";//change accordingly

      // Assuming you are sending email through relay.jangosmtp.net
      //String host = "smtp.gmail.com";
      String host = "mail.mijndomein.nl";

      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.host", host);
      // STARTTLS Factory
      //props.put("mail.smtp.starttls.enable", "true");
      //props.put("mail.smtp.port", "587");
      // SSL Factory 
      props.put("mail.smtp.port", "465");
      props.put("mail.smtp.socketFactory.class", 
              "javax.net.ssl.SSLSocketFactory"); 
      // Get the Session object.
      Session session = Session.getInstance(props,
         new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(username, password);
            }
         });

      try {
         // Create a default MimeMessage object.
         Message message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.setRecipients(Message.RecipientType.TO,
            InternetAddress.parse(to));

         // Set Subject: header field
         message.setSubject("IJSCO Uitslag van Toernooi " + IJSCOController.t().getBeschrijving() + ".");

         // Create the message part
         BodyPart messageBodyPart = new MimeBodyPart();

         // Now set the actual message
         messageBodyPart.setText("Beste IJSCO uitslagverwerker,\r\n\r\nHierbij de uitslagen van het toernooi " + IJSCOController.t().getBeschrijving() + " van " + IJSCOController.t().getDatum() + " te " + IJSCOController.t().getPlaats() + ".\r\n\r\nAangemaakt met " + IJSCOController.c().appTitle + " " + IJSCOController.getAppVersion());

         // Create a multipart message
         Multipart multipart = new MimeMultipart();

         // Set text message part
         multipart.addBodyPart(messageBodyPart);

         // Part two is attachment
         messageBodyPart = new MimeBodyPart();
         //String filename = "Uitslagen.json";
         DataSource source = new FileDataSource(attachement);
         messageBodyPart.setDataHandler(new DataHandler(source));
         messageBodyPart.setFileName(attachement);
         multipart.addBodyPart(messageBodyPart);

         // Send the complete message parts
         message.setContent(multipart);

         // Send message
         Transport.send(message);
         logger.log(Level.INFO, "Email succesvol verzonden.");
 
      } catch (MessagingException e)
      {
    	  logger.log(Level.SEVERE, "Probleem met verzenden Email. Message: " + e.getMessage() + ". Cause: " + e.getCause() + ".");
    	  JOptionPane.showMessageDialog(null, "Versturen van uitslagen naar OSBO mislukt. Probeer later het menu OSBO handmatig opnieuw.");
      }
   }
}