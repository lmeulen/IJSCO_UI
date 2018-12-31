package nl.detoren.ijsco.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

import nl.detoren.ijsco.ui.control.IJSCOController;

public class AboutDialog extends JDialog {
	
	private static final long serialVersionUID = 2426089852777554719L;
	
	//FIXME: is there a way to extract from project??
	private static final String versionString = IJSCOController.getAppVersion();
	private static final String nameString = IJSCOController.c().appTitle;
	private static final String aboutString = 
			IJSCOController.c().appTitle + " is het indelingsprogramma voor de regionale jeugschaakcompetitie van de OSBO. " +
			"Deze software maakt het mogelijk op basis van de actuele lijst van IJSCO spelers snel en eenvoudig een indeling " +
			"te maken. Deze applicatie maakt een spreadsheet aan (Excel of Open Source variant). Hierin kunnen (alleen) de " +
			"uitslagen worden ingevoerd. Vervolgens kan deze na afoopt in de applicatie worden geimporteerd en verwerkt. " +
			"De applicatie maakt dan enkele uitslag bestanden aan die b.v. voor de website gebruikt kunnen worden.";
	
	public  AboutDialog(JFrame parent) {
		
		super(parent, "About " + nameString, true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		
		JLabel nameLabel = new JLabel(nameString);

		Font baseFont = nameLabel.getFont().deriveFont(Font.PLAIN);
		nameLabel.setFont(baseFont.deriveFont(Font.BOLD, baseFont.getSize()+ 1.0f));
		
		JLabel versionLabel = new JLabel(versionString);
		versionLabel.setFont(baseFont.deriveFont(baseFont.getSize() + 1.0f));
		
		JTextArea aboutText = new JTextArea(aboutString);
		aboutText.setFont(baseFont);
		aboutText.setBackground(null);
		aboutText.setEditable(false);
		aboutText.setBorder(null);
		aboutText.setLineWrap(true);
		aboutText.setWrapStyleWord(true);
		aboutText.setFocusable(false);
			
		//JLabel iconLabel = new JLabel();
		//iconLabel.setIcon(createAppIcon());
		
		final int margin = 24;
		final int topMargin = margin * 3 / 2;
		
		Box namePanel = Box.createVerticalBox();
		namePanel.add(nameLabel);
		namePanel.add(Box.createVerticalStrut(margin));
		namePanel.add(versionLabel);
		
		Box iconPanel = Box.createHorizontalBox();
		//iconPanel.add(iconLabel);
		iconPanel.add(Box.createHorizontalStrut(margin));
		iconPanel.add(namePanel);
		
		Box topPanel = Box.createVerticalBox();
		topPanel.setBorder(new EmptyBorder(topMargin, margin, margin, margin));
		topPanel.add(iconPanel);
		topPanel.add(Box.createVerticalStrut(topMargin));
		topPanel.add(aboutText);
				
		setContentPane(topPanel);
		
		setPreferredSize(new Dimension(640, 280));
		pack();
		setLocationByPlatform(true);
		setLocationRelativeTo(parent);
	}
	
	private ImageIcon createAppIcon()
	{
		try {
			BufferedImage iconBufferedImage;
			iconBufferedImage = ImageIO.read(getClass().getResource("/res/AppIcon.png"));
			iconBufferedImage = Scalr.resize(iconBufferedImage, Method.QUALITY, 64, 64, Scalr.OP_ANTIALIAS);
			ImageIcon icon = new ImageIcon(iconBufferedImage);
			return icon;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}

}