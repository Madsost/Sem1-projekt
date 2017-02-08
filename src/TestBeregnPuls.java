import java.util.ArrayList;

public class TestBeregnPuls {
	public static ArrayList<Double> testListe;
	public static ArrayList<String> testListe2;
	public static int samplesize = 1000;
	public static double puls;
	public static int counter = 0;
	public static boolean TestE = false;
	public static boolean TestG1 = false;
	public static boolean TestG2 = false;
	public static boolean TestG3 = false;
	public static boolean TestA1 = false;
	public static boolean TestA2 = false;
	public static boolean TestB1 = false;
	public static boolean TestC1 = false;

	public static void main(String[] args) {
		Hovedprogram monitor = new Hovedprogram();

		testListe = new ArrayList<>();

		String svar = "A";

		monitor.sletFil();

		System.out.println("start");
		System.out.println("Klar!");

		Pulsberegner a = new Pulsberegner(1000, 5);

		if (svar.equals("A"))
			a.beregnPulsFIR(testListe);

		if (svar.equals("B")) {
			testListe.add((double) 3);
			a.beregnPulsFIR(testListe);
		}

		if (svar.equals("C")) {
			testListe.add((double) 2);
			testListe.add((double) 3);
			testListe.add((double) 4);
			a.beregnPulsFIR(testListe);
		}

		if (svar.equals("D")) {
			for (double i = 0; i < 5; i = i + 0.005) {
				testListe.add(i);
			}
		}

		if (svar.equals("E")) {
			TestE = true;
			for (double i = 0.001; i < 1; i = i + 0.001) {
				testListe.add(i);
			}
		}

		if (svar.equals("F")) {
			for (double i = 5; i > 0.001; i = i - 0.005) {
				testListe.add(i);
			}
		}

		if (svar.equals("G1")) {
			TestG1 = true;
		}

		if (svar.equals("G2")) {
			TestG2 = true;
		}

		if (svar.equals("G3")) {
			TestG3 = true;
		}

		if (svar.equals("A1")) {
			TestA1 = true;
		}
		if (svar.equals("A2")) {
			TestA2 = true;
		}

		if (svar.equals("B1")) {
			TestB1 = true;
		}

		if (svar.equals("C1")) {
			TestC1 = true;
		}

		if (svar.equals("C2")) {
			TestSensor test = new TestSensor();
			test.saetHastighed(0.8);
			testListe2 = test.hentMaalinger(1000);
			for (String p : testListe2) {
				testListe.add(Double.parseDouble(p));
			}
		}

		if (svar.equals("C3")) {
			TestSensor test = new TestSensor();
			test.saetHastighed(1.05);
			testListe2 = test.hentMaalinger(1000);
			for (String p : testListe2) {
				testListe.add(Double.parseDouble(p));
			}
		}

		if (svar.equals("C4")) {
			TestSensor test = new TestSensor();
			test.saetHastighed(1.85);
			testListe2 = test.hentMaalinger(1000);
			for (String p : testListe2) {
				testListe.add(Double.parseDouble(p));
			}
		}

		// Modtager de første målinger
		System.out.println("Vi er begyndt!");
		double t1 = System.currentTimeMillis();
		double t2 = System.currentTimeMillis();

		monitor.sletFil();
		t1 = System.currentTimeMillis();

		System.out.println("Testlisten er nu " + testListe.size() + " lang");

		puls = a.beregnPulsFIR(testListe);
		monitor.gemListeTilFil(testListe);

		if (puls > 0) {
			monitor.gemTilFil(puls);
		} else
			System.out.println("Dårlig måling - fortsÊtter");
		counter++;
		t2 = System.currentTimeMillis();
		System.out.println("Det tog: " + (t2 - t1) + "ms");
		System.out.println("Pulsen var: " + puls + " bpm");
	}
}
