package nl.detoren.ijsco.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.detoren.ijsco.ui.Mainscreen;

public class Toernooi {

	private String _beschrijving;
	
	private Date _datum;
	
	private String _vereniging;
	
	private String _locatie;
	
	private String _plaats;
	
	private List<WedstrijdUitslag> _uitslagen = new ArrayList<WedstrijdUitslag>();

	private final static Logger logger = Logger.getLogger(Mainscreen.class.getName());
	
	public Toernooi() {
		_beschrijving = "";
		_datum = new Date();
		_vereniging = "";
		_locatie = "";
		_plaats = "";
	}

	public Toernooi(String beschrijving, Date datum, String vereniging, String locatie, String plaats) {
		_beschrijving = beschrijving;
		_datum = datum;
		_vereniging = vereniging;
		_locatie = locatie;
		_plaats = plaats;
	}
	
	
// region getters
	public String getBeschrijving() {
		return _beschrijving;
	}
	
	public Date getDatum() {
		return _datum;
	}
	
	public String getVereniging() {
		return _vereniging;
	}
	
	public String getLocatie() {
		return _locatie;
	}
	
	public String getPlaats() {
		return _plaats;
	}
//endregion getters
	
//region setters
	
	public void setBeschrijving(String beschrijving) {
		_beschrijving = beschrijving;
	}

	public void setDatum(Date datum) {
		_datum = datum;
	}
	
	public void setVereniging(String vereniging) {
		_vereniging = vereniging;
	}

	public void setLocatie(String locatie) {
		_locatie = locatie;
	}

	public void setPlaats(String plaats) {
		_plaats = plaats;
	}

	public void wisUitslagen() {
		_uitslagen.clear();
	}
	
	public void addUitslag(WedstrijdUitslag uitslag) {
		logger.log(Level.INFO, "resultaat = " + uitslag.resultaat);
		_uitslagen.add(uitslag);
		logger.log(Level.INFO, "resultaat = " + _uitslagen.get(_uitslagen.size()-1).resultaat);
		
	}

//endregion setters
	
//region methods
	
	public String toString() {
		String result = "";
		result += "Toernooi : " + _beschrijving + "\r\n";
		result += "Datum : " + _datum + "\r\n";
		result += "Organiserende vereniging : " + _vereniging+ "\r\n";
		result += "Locatie : " + _locatie+ "\r\n";
		result += "Plaats : " + _plaats+ "\r\n";
		return result;
	}
	
//endregion methode
}
