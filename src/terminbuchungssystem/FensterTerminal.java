package terminbuchungssystem;

import java.sql.SQLException;

import javax.swing.JFrame;

/** Enhält Statische Methoden für den logischen Aufbau der Benutzeroberfläche.
 *
 * @author Nicholas Kappauf
 */
public class FensterTerminal {

	/**
	 * Statische Methode als Logikverzweigung zur Erzeugung des nächsten Frames, je nachdem ob ein Nutzer bereits ein
	 * eigenes Passwort festgelegt hat, erfolgreich Daten hinterlegt hat und je nachdem welche Rolle dem Nutzer
	 * zugeordnet ist.
	 */
	public static void naechsterJframe(Nutzer nutzer) {
		if (nutzer.getLogin().isNeukonto()) {
			JFrameNeuesPasswort framePsw = new JFrameNeuesPasswort(nutzer.getLogin());
			framePsw.setVisible(true);
		}else if(!nutzer.isValideDaten()) {
			JframeNeueNutzerDaten frameDaten = new JframeNeueNutzerDaten(nutzer);
			frameDaten.setVisible(true);
		}else {
			switch (nutzer.getLogin().getRolle()) {
				case 1 -> {
					JFrameZugangAdministrator frameAdmin;
					try {
						frameAdmin = new JFrameZugangAdministrator((Admin) nutzer);
						frameAdmin.setVisible(true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				case 2 -> {
					JFrameZugangArzt frameArzt;
					frameArzt = new JFrameZugangArzt((Arzt) nutzer);
					frameArzt.setVisible(true);
				}
				case 3 -> {
					JFrameZugangSprechstundenhilfe frameHilfe;
					frameHilfe = new JFrameZugangSprechstundenhilfe((Sprechstundenhilfe) nutzer);
					frameHilfe.setVisible(true);
				}
			}
		}
	}
}
