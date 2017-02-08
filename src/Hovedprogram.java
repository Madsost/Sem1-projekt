import java.io.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Kontrolklasse, der har til ansvar at styre slagets gang.
 * <p>
 * Klassen har metoder til at slette sessions-filen, gemme til den, konvertere
 * m�linger fra Sensor-klassen til tal, samt afg�re om en test er i gang.
 * 
 * @author Gruppe 6
 */
public class Hovedprogram {
	public int counter = 0;
	public int sampleTime = 5;
	public int sampleSize;
	public ArrayList<Double> data;
	// public ArrayList<Double> data200;
	public ArrayList<String> input;

	/**
	 * Denne metode h�ndterer at konvertere de modtagne m�linger i <code>input</code>-listen til tal og gemme m�lingerne i en anden liste kaldet <code>data</code>.
	 * <p>
	 * Metoden fors�ger at konvertere hver streng i listen til et tal. Hvis ordet ikke er et tal springes m�lingen over.
	 * 
	 */
	public void konverter() {
		for (String p : input) {						/*2A*/
			p = p.trim();
			try {
				Double.parseDouble(p);					/*2B*/
			} catch (NumberFormatException e) {
				continue;
			}
			data.add(Double.parseDouble(p));			/*2C*/
		}
	}

	/**
	 * Denne metode sletter de to filer, der anvendes i programmet. Det g�res ved at oprette filerne og s� slette dem efterf�lgende.
	 * <p>
	 * Herefter skrives der en dato i toppen af Maalinger.txt. 
	 */
	public void sletFil() {
		File f = new File("R� data.txt");
		File f1 = new File("Maalinger.txt");
		if (f.exists())									
			f.delete();
		if (f1.exists())								
			f1.delete();
		Date dato = new Date();
		try {
		FileWriter fil = new FileWriter("Maalinger.txt", true);
		PrintWriter ud = new PrintWriter(fil);
		ud.println(dato+":");
		ud.close();
	} catch (IOException e1) {
		e1.printStackTrace();}
	}

	/** 
	 * Sp�rger brugeren om der skal k�res med et sensorobjekt af klassen Sensor eller Testsensor.
	 * @return sand hvis brugeren trykker OK, ellers returnerer den falsk. 
	 */
	public boolean testsensor() {
		String spm = "K�res med test-sensor?";
		String svar = javax.swing.JOptionPane.showInputDialog(spm, "ja");
		if (svar != null && svar.equals("ja"))
			return true;
		else
			return false;
	}

	/** 
	 * Modtager den liste, som skal gemmes i filen. S� oprettes filen og metoden genneml�ber listen og for hvert element i listen gemmer den tallet til filen. 
	 * <p>
	 * Sidst lukkes filen. 
	 * @param liste (ArrayList af flydende heltal)
	 */
	public void gemListeTilFil(ArrayList<Double> liste) {
		try {
			FileWriter fil = new FileWriter("R� data.txt", true);
			PrintWriter ud = new PrintWriter(fil);
			for (int i = 0; i < liste.size(); i++) {	/*3A*/
				ud.println(liste.get(i));
			}
			ud.close();
			System.out.println("Skrevet til fil");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/** 
	 *  Modtager den puls, som skal gemmes i filen. S� oprettes filen og metoden konverterer den modtagne puls til et tal med 2 decimaler og gemmer dem til filen. 
	 * <p>
	 * Sidst lukkes filen. 
	 * @param puls (den m�lte puls)
	 */
	public void gemTilFil(double puls) {
		try {
			FileWriter fil = new FileWriter("Maalinger.txt", true);
			PrintWriter ud = new PrintWriter(fil);
			int temp1 = (int) puls;
			int temp2 = (int) (puls * 100) % 100;
			String skriver = "Tid " + (counter * sampleSize / 200) + "s: " + temp1 + "." + temp2 + " bpm";
			ud.println(skriver);
			ud.close();
			System.out.println("Skrevet til fil");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Hovedmetoden. Heri ligger hovedprogrammets logik. Kaldes fra denne klasses <code>main</code>-metode.
	 */
	public void koer() {
		// Slet fil
		sletFil();
		sampleSize = 1000;
		sampleTime = 5;

		// Initialiser de n�dvendige objekter
		Sensor s = new Sensor();
		Pulsberegner p = new Pulsberegner(sampleSize, sampleTime);

		// tjekker om programmet skal k�red med testsensor
		boolean testsensor = testsensor();
		if (testsensor)										/*1A*/
			s = new TestSensor();
		if (!testsensor) {
			System.out.println("Ops�tter sensoren...");
			s.opsaet();
			System.out.println("Renser f�rste m�ling...");
			s.rens();
		}
		// Venter i 5 sekunder ved at kalde hentMaalinger uden at gemme
		System.out.println("Vent:");
		s.hentMaalinger(sampleSize);
		System.out.println("\nBegynder l�kke!");
		for (int i = 0; i < 24; i++) {
			input = new ArrayList<>();

			System.out.println("Henter m�linger...");
			input = s.hentMaalinger(sampleSize);

			data = new ArrayList<>();

			// Konverter m�lingerne til double
			System.out.println("\nKonverterer...");
			konverter();									/*2*/

			// Trimmer st�rrelsen ned til 1000 m�linger
			data.subList(sampleSize, data.size()).clear();

			// Gem m�linger
			System.out.println("Gemmer datas�t til fil...");
			gemListeTilFil(data);							/*3*/
			System.out.println("Beregner puls...");
			double puls = p.beregnPulsFIR(data);
			if (puls == 0)
				System.out.println("D�rlig m�ling!");
			else {
				gemTilFil(puls);							
				System.out.println("Pulsen beregnes til: " + puls + "\n");
			}
			counter++;
		}
	}

	public static void main(String[] args) {
		Hovedprogram monitor = new Hovedprogram();
		monitor.koer();
	}
}
