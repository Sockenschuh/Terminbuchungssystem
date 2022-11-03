package terminbuchungssystem;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Zellen-spezialisierte abgeleitete Klasse von swing.table.TableCellRenderer
 * Speichert gewünschte Farbgebung und zuletzt ausgewählte Zellen.
 * Zuletzt ausgewählte Zellen werden weiter als ausgewählt eingefärbt auch,
 * wenn Tabelle nicht mehr fokussiert ist.
 *
 * @author Nicholas Kappauf
 */
class ZellenRenderer extends JLabel implements TableCellRenderer{
	private final Color augewaehltFarbe = new Color( 200, 200, 255 );
	private final Color fokusFarbe = new Color( 200, 255, 200 );
	private final Color standardFarbe = new Color( 240, 240, 240 );
	
	private int zuletztGewaehltZeile;
	private int zuletztGewaehltSpalte;
	private Boolean terminfensterModus;

	public ZellenRenderer(){
		setOpaque( true );
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
				
		Color normaleFarbe = standardFarbe;
				
		setText( null );
		setIcon( null );
		
		if(value instanceof Zelle zelle){
			this.terminfensterModus = zelle.getTerminfensterModus();
			normaleFarbe = zelle.getFarbe()==null?normaleFarbe:zelle.getFarbe();
			hasFocus = zelle.getFokussierbar() && hasFocus;
			isSelected = zelle.getAuswaehlbar() && isSelected;
			setHorizontalAlignment(zelle.getTextAusrichtung());
			setText(zelle.getText());			
		}else {
			setText( value.toString() );}		
		if( hasFocus) {			
			if (this.zuletztGewaehltSpalte != column || this.zuletztGewaehltZeile != row) {
				this.zuletztGewaehltSpalte = column;
				this.zuletztGewaehltZeile = row;
				setBackground( fokusFarbe );
				if (terminfensterModus) {					
					if(value instanceof Zelle zelle){
						zelle.switchActive(row);
					}
				}
				}
			}
		else if( isSelected) {
			setBackground( augewaehltFarbe );			
			}	else
				setBackground( normaleFarbe );	
		
		if(!table.hasFocus() && 0 == column && zuletztGewaehltZeile == row) {
			setBackground( augewaehltFarbe );
		}
		if(!table.hasFocus() && zuletztGewaehltSpalte == column && zuletztGewaehltZeile == row) {
			setBackground( fokusFarbe );		
		}
					
		return this;
	}

	public int getZuletztGewaehltZeile() {
		return zuletztGewaehltZeile;
	}

	public int getZuletztGewaehltSpalte() {
		return zuletztGewaehltSpalte;
	}		
}
