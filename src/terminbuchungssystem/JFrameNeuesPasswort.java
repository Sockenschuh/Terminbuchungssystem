package terminbuchungssystem;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import java.awt.SystemColor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
/**
 * Fenster zum Festlegen eines neuen Passworts. Notwendige Anforderungen werden angezeigt und erfüllte Anforderungen
 * gekennzeichnet.
 *
 * @author Nicholas Kappauf
 */
public class JFrameNeuesPasswort extends JFrame {

	private JPanel contentPane;
	private JTextField jtfPasswort;
	private JTextField jtfWiederholung;
	JLabel lblFehler;
	Login login;
	JTextPane txtpnPasswortanforderungen;

	/**
	 * Erzeugt das Fenster mit zwei textfelder zum eintragen und wiederholen des Passworts. Das obere Passwort wird
	 * auf erfüllte Anforderungen geprüft, wann immer es ausgewählt ist und eine Taste betätigt wird.
	 *
	 * Enthält außerdem einen dynamischen Text zum Anzeigen der erfüllten Anforderungen und eine Taste um das Passwort
	 * und dessen Wiederholung zu prüfen und zu übernehmen.
	 */
	public JFrameNeuesPasswort(Login login) {
		setTitle("Neues Passwort");
		this.login = login;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 260);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow][]", "[][][grow][][][][]"));
		
		jtfPasswort = new JTextField();
		jtfPasswort.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String klarPasswort = jtfPasswort.getText(); 
				
				String haken = "✅";
				String kreuz ="❎";
				
				String laengeCheck = klarPasswort.matches(".{12,}")?haken:kreuz;
				String großBsCheck = klarPasswort.matches(".*[A-ZÄÖÜ]+.*")?haken:kreuz;
				String kleinBsCheck = klarPasswort.matches(".*[a-zäöü]+.*")?haken:kreuz;
				String zifferCheck = klarPasswort.matches(".*[1-9]+.*")?haken:kreuz;
				String sonderCheck =  klarPasswort.matches(".*(?=\\.*?[#?!@$%^&*-\\+])+.*")?haken:kreuz;
				
				String regeln = "Passwortanforderungen:\r\n"
				+ "\r\n"
				+ "•	Passwortlänge mind. 12 Zeichen "+laengeCheck+"\r\n"
				+ "•	Mindestens 1 Großbuchstabe "+großBsCheck+"\r\n"
				+ "•	Mindestens 1 Kleinbuchstabe "+kleinBsCheck+"\r\n"
				+ "•	Mindestens 1 Ziffer "+zifferCheck+"\r\n"
				+ "•	Mindestens 1 Sonderzeichen " +sonderCheck;	
                txtpnPasswortanforderungen.setText(regeln);				
			}
		});
		contentPane.add(jtfPasswort, "cell 0 0,growx");
		jtfPasswort.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Neues Passwort");
		contentPane.add(lblNewLabel, "cell 1 0");
		
		jtfWiederholung = new JTextField();
		contentPane.add(jtfWiederholung, "cell 0 1,growx");
		jtfWiederholung.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Wiederholung");
		contentPane.add(lblNewLabel_1, "cell 1 1");
		
		txtpnPasswortanforderungen = new JTextPane();
		txtpnPasswortanforderungen.setRequestFocusEnabled(false);
		txtpnPasswortanforderungen.setBackground(SystemColor.menu);
		txtpnPasswortanforderungen.setText("Passwortanforderungen:\r\n\r\n•\tPasswortlänge mind. 12 Zeichen\r\n•\tMindestens 1 Großbuchstabe\r\n•\tMindestens 1 Kleinbuchstabe\r\n•\tMindestens 1 Ziffer\r\n•\tMindestens 1 Sonderzeichen");
		txtpnPasswortanforderungen.setEditable(false);
		contentPane.add(txtpnPasswortanforderungen, "cell 0 2,growx,aligny top");
		
		JButton btnEnter = new JButton("bestätigen");
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enterAktion();
			}
		});
		
		lblFehler = new JLabel("Fehler:");
		lblFehler.setVisible(false);
		contentPane.add(lblFehler, "cell 0 3");
		contentPane.add(btnEnter, "cell 1 3");
		
		JLabel lblNewLabel_2 = new JLabel(" ");
		contentPane.add(lblNewLabel_2, "cell 0 5");
	}
	
	public void enterAktion() {
		String psw = jtfPasswort.getText();
		if(psw.equals(jtfWiederholung.getText())) {
			try {
				login.setEncryptedPsw(psw);
				JFrameLoginFenster startFenster = new JFrameLoginFenster();
				startFenster.setVisible(true);
				dispose();
			}catch(IllegalArgumentException e1) {
				lblFehler.setText("Fehler: Anforderungen nicht erfüllt!");
				lblFehler.setVisible(true);
			}catch(SQLException e2) {
				System.out.println(e2.toString());
				lblFehler.setText("Fehler: Datenbankfehler!");
				lblFehler.setVisible(true);
			}
		}else {
			lblFehler.setText("Fehler: Wiederholung falsch!");
			lblFehler.setVisible(true);
		}
	}

}
