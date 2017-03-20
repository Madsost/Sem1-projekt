import java.util.ArrayList;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * Klasse til kommunikation med Arduinoen og derigennem sensoren.
 * <p>
 * Indhenter m�linger fra en serielt tilsluttet Arduino, der m�ler hvert 5.
 * ms.
 * <p>
 * Indeholder en metode til at hente m�linger, til at rense den f�rste m�ling og
 * en ops�tningsmetode. Returnerer en liste med sample_size st�rrelse med
 * m�linger.
 * @author Gruppe 6. 
 */

public class Sensor {

	private SerialPort serialPort;
	private ArrayList<String> målinger;
	private String buffer = "";
	private boolean test = false;

	/**
	 * ops�tter den serielle port og melder at data terminalen er klar til at
	 * modtage data.<p>
	 * Anvender JSSC biblioteket. Kr�ver at der kun er tilsluttet �n seriel
	 *  forbindelse til programmet, da det antages at der kun er en port i brug.<p>
	 * Returnerer intet.
	 */
	public void opsaet() {
		try {
			String[] portNames = SerialPortList.getPortNames();
			String port = portNames[0];
			this.serialPort = new SerialPort(port);
			serialPort.openPort(); // Open serial port
			serialPort.setParams(19200, 8, 1, 0); // Set params.
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			serialPort.setDTR(true); 
		} catch (ArrayIndexOutOfBoundsException b) {					/*1A*/	
			System.out.println("Der var ikke tilsluttet nogen enheder");	
			System.exit(0);
		} catch (SerialPortException ex) {
			System.out.println("Serial Port Exception: " + ex);
		}
	}

	/**
	 * Kalder m�l() med parameteren og returnerer en liste med det antal m�linger fra Arduinoen som parameteren bestemmer.
	 *  
	 * @param sampleSize det �nskede antal m�linger, som heltal.
	 * @return m�linger: en ArrayList af strenge, hvor hver plads indeholder en m�ling.
	 */
	public ArrayList<String> hentMaalinger(int sampleSize) {
		maal(sampleSize);
		return målinger;
	}

	/**
	 * Ansvarlig for hentning af m�linger. 
	 * Denne metode modtager en heltalsparameter, som bestemmer antallet af m�linger der skal hentes.
	 * Metoden best�r af en l�kke, som henter v�rdier fra den serielle port indtil parameter-v�rdien er n�et.
	 * Hvis <code>InputBuffer</code> er tom, ventes der i 75 ms.
	 * <p> 
	 * Returnerer intet, men gemmer til listen <code>m�linger</code>, som kan tilg�s med <code>hentM�linger</code>
	 * 
	 * @param sampleSize det �nskede antal m�linger, som heltal.
	 */
	public void maal(int sampleSize) {
		int point = 0;
		// instantierer listen - s�ledes gemmes kun sample_size for hvert kald
		målinger = new ArrayList<>(sampleSize);
		do {
			if (test)
				System.out.println("M�l!");
			try {
				// hvis der er m�linger p� vej
				if (serialPort.getInputBufferBytesCount() > 0) {
					buffer += serialPort.readString();
					if (test)
						System.out.println("buffer: " + buffer);
					int pos = -1;
					while ((pos = buffer.indexOf("!")) > -1) {		/*1B*/
						målinger.add(buffer.substring(0, pos));
						buffer = buffer.substring(pos + 1);
					}
				}
				// Venter p� at input-bufferen bliver fyldt igen
				else
					try {
						Thread.sleep(75);							/*1C*/
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			} catch (SerialPortException e1) {
				e1.printStackTrace();
			}
			// Udskriver et punktum for hvert 200 m�ling (ca. hvert sekund)
			if (målinger.size() - point >= 200) {
					System.out.print(".");
					point = målinger.size();
				}
			if (test) {
				System.out.println(målinger.size());
			}
			
		// st�rrelsen kan godt v�re st�rre end sampleSize!
		} while (målinger.size() <= sampleSize);
		System.out.println();
	}

	/** 
	 * Rydder den første måling, da de første målinger typisk er fejlagtige. <p>
	 * De første mølinger i <code>InputBuffer</code> aflæses, opdeles i enkelte målinger og slettes.
	 */
	public void rens() {
		try {
			// hvis der er målinger på vej
			if (serialPort.getInputBufferBytesCount() > 0) {
				buffer += serialPort.readString();
				int pos = -1;
				if ((pos = buffer.lastIndexOf("!")) > -1) {	 /*2A*/
					buffer = buffer.substring(pos + 1);
				}
			}
		} catch (SerialPortException e2) {
			e2.printStackTrace();
		}
	}
}
