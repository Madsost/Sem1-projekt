import java.util.ArrayList;

/**
 * Simulering af en pulsm�ler
 * <p>
 * Anvender en sinus og cosinus-funktion til at simulere en m�ling
 * 
 * @author Gruppe 6.
 */

public class TestSensor extends Sensor {

	private boolean test = false;
	private double count = 2;
	private ArrayList<String> m�linger;
	private double hastighed = 1;

	/**
	 *  Pulsfunktion - indeholder den funktion, som simulerer en puls.<p>
	 *  Modtager et tal, som bruges som variablen i funktionen.
	 *  
	 * @param x 	en x-v�rdi. Det er denne parameter, som skal inkementeres
	 * @return 		y-v�rdien for parameteren.
	 */
	public double pulsfunktion(double x) {
		double funktion = -1 * Math.sin(hastighed*(8 * x)) + 1.5 * Math.cos(hastighed*(4 * x)) + 2;
		return funktion;
	}
	
	/**
	 * S�tter hastigheden p� den simulerede puls. Hastighed er sat til 1.0 fra start.
	 * @param a
	 */
	public void saetHastighed(double a){
		hastighed = a;
	}

	/**
	 * svarer til getValues i Sensor-klassen. Venter 5ms for at matche arduinoen
	 */
	@Override
	public ArrayList<String> hentMaalinger(int sample_size) {
		m�linger = new ArrayList<>();
		int point = 0;
		for (int i = 0; i < sample_size; i++) {
			double resultat = pulsfunktion(count);
			if (test)
				System.out.println(resultat);
			// t�ller 1/10 op for hvert kald
			count += 0.01;
			String temp = "" + resultat;
			m�linger.add(temp);
			if (m�linger.size() - point >= 200) {
				System.out.print(".");
				point = (m�linger.size() / 200) * 200;
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (test)
			System.out.println(count);
		return m�linger;
	}
}
