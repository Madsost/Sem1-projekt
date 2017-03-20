import java.util.ArrayList;

/**
 * Klasse som st�r for pulsberegningen. Derudover implementerer klassen et FIR filter.
 * <p>
 * Den prim�re metode er <code>beregnPulsFIR</code>, som returnerer et gennemsnit af de seneste 3 m�linger.
 * @author Gruppe 6
 *
 */
public class Pulsberegner {
	private double middelVal;
	private int sampleSize, sampleTime, length, count;
	private double[] delayLine, impulseResponse;
	private double skalar;
	private ArrayList<Double> pulsmålinger = new ArrayList<>();
	private double[] inddata;
	
	// Koefficienterne er fundet vha. http://t-filter.engineerjs.com/
	private double[] coefs = new double[]{-0.001967541047829245, -0.0016966240792510331, -0.002338377604830948,
			-0.0030568678271867935, -0.003824828088454247, -0.004605780250561608, -0.005352228028976562,
			-0.006007060315444569, -0.006507915977359299, -0.006787073678098569, -0.006771262109917609,
			-0.006391105993108435, -0.00558469116676042, -0.004290240583129744, -0.002468140066018767,
			-0.00008644112941448361, 0.002864109591129335, 0.00637272419252925, 0.01040562170739782,
			0.014906885951350117, 0.01979717385220548, 0.02497651831451001, 0.030328006086320237,
			0.035719093481015346, 0.04100777150314689, 0.04604877491838462, 0.05069598950094379,
			0.05481191898514311, 0.05827122790015025, 0.06096631815004387, 0.06281206500963983, 0.0637502063435781,
			0.0637502063435781, 0.06281206500963983, 0.06096631815004387, 0.05827122790015025, 0.05481191898514311,
			0.05069598950094379, 0.04604877491838462, 0.04100777150314689, 0.035719093481015346,
			0.030328006086320237, 0.02497651831451001, 0.01979717385220548, 0.014906885951350117,
			0.01040562170739782, 0.00637272419252925, 0.002864109591129335, -0.00008644112941448361,
			-0.002468140066018767, -0.004290240583129744, -0.00558469116676042, -0.006391105993108435,
			-0.006771262109917609, -0.006787073678098569, -0.006507915977359299, -0.006007060315444569,
			-0.005352228028976562, -0.004605780250561608, -0.003824828088454247, -0.0030568678271867935,
			-0.002338377604830948, -0.0016966240792510331, -0.001967541047829245};

	/**
	 * Hovedkonstruktør.<p>
	 * Modtager en sampletid og en samplestørrelse, som objektet skal arbejde videre med.
	 * @param sampleSize Antallet af samples der ses p� af gangen
	 * @param sampleTime Afstanden mellem hver måling
	 */
	public Pulsberegner(int sampleSize, int sampleTime) {
		this.sampleSize = sampleSize;
		this.sampleTime = sampleTime;
	}
	
	/**
	 * Denne metode modtager en input-sample, k�rer den igennem et FIR-filter og returnerer den filtrerede sample.
	 * <p>
	 * Ved 64 koefficienter forskyder denne metode signalet 64 pladser.
	 * <p> 
	 * Fra http://ptolemy.eecs.berkeley.edu/eecs20/week12/implementation.html
	 * 
	 * @param inputSample Den m�ling der skal filtreres
	 * @author Berkeley, ECCS
	 * @return den filtrerede m�ling.
	 */
	public double getOutputSample(double inputSample) {
		delayLine[count] = inputSample;
		double result = 0.0;
		int index = count;
		for (int i = 0; i < length; i++) {
			result += impulseResponse[i] * delayLine[index--];
			if (index < 0)
				index = length - 1;
		}
		if (++count >= length)
			count = 0;
		return result;
	}

	/**
	 * Returnerer summen af koefficienterne. Bruges til at skalere m�lingerne tilbage til det forrige system. 
	 * 
	 * @param koefficienter Et array af simpel type. 
	 * @return summen af koefficienterne, der anvendes af FIR filteret. 
	 */
	public double skalering(double[] koefficienter) {
		double sum = 0;
		for (double tal : koefficienter) {
			sum += tal;
		}
		return sum;
	}
	
	/**
	 * Metode, der ops�tter FIR-filteret.
	 * 
	 * Frit efter konstrukt�r i Berkeleys implementering
	 * @author Berkeley, ECCS
	 */
	public void opsaetFIR(){
		length = coefs.length;
		impulseResponse = coefs;
		delayLine = new double[length];
		skalar = skalering(coefs);
	}

	/** genneml�ber listen og udregner middelv�rdien af m�lingerne */
	public void beregnMiddelVal() {
		double sum = 0;
		for (double tal : inddata) {	/*2A*/
			sum = sum + tal;
		}
		double resultat = sum / sampleSize;
		// Til afpr�vning:
		// System.out.println(resultat);
		middelVal = resultat;
	}

	/**
	 * Metode, der beregner pulsen. Den modtager et datas�t fra hovedprogrammet, centrerer m�lingerne omkring middelv�rdien, sender s�ttet igennnem et filter
	 * og udregner pulsen herudfra.
	 * <p>
	 * Pulsen gemmes i en liste og metoden returnerer gennemsnittet af de seneste tre m�linger.
	 * 
	 * @param m�linger (ArrayList af flydende heltal)
	 * @return middelv�rdien af de tre seneste pulsberegninger
	 */
	public double beregnPulsFIR(ArrayList<Double> målinger) {

		inddata = new double[målinger.size()];

		// Konverterer til array af simpel type
		for (int i = 0; i < målinger.size(); i++) {	/*1A*/
			inddata[i] = målinger.get(i);
		}
		
		opsaetFIR();

		beregnMiddelVal();

		// Centrerer m�lingerne omkring middelv�rdien
		for (double tal : inddata) {				/*1B*/
			tal = tal - middelVal;
		}

		// Filtrerer m�lingerne 
		for (int j = 0; j < inddata.length; j++) {	/*1C*/
			inddata[j] = getOutputSample(inddata[j]);
			inddata[j] = inddata[j] / skalar;
		}

		// Til afpr�vning: 
		/*for (double tal : inddata) {
			System.out.println(tal);
		}*/

		// Udregner afvigelsen for 300 forskydelser
		double[] scores = new double[300];
		for (int j = 1; j < 301; j++) {
			double score = 0;
			int b = j;
			int a = 0;
			while (b < (inddata.length)) {			/*1D*/
				score += (inddata[a] - inddata[b]) * (inddata[a] - inddata[b]);
				b++;
				a++;
			}
			scores[j - 1] = score;
		}
		
		// til afpr�vning
		/*for(double tal : scores){
			System.out.println(tal);
		}*/
		
		
		// Finder laveste v�rdi for b�lgedale
		boolean ned = false;
		boolean bund = false;
		double min = Double.MAX_VALUE;
		for(int i = 60; i<scores.length-1;i++){		/*1E*/
			ned = (scores[i] - scores[i-1] < 0);
			if(i == scores.length) break;
			else{
				bund = (scores[i+1] - scores[i] >= 0);
				if(bund && ned){
					min = (scores[i]<min) ? scores[i] : min;
				}
			}
		}
		
		// Finder f�rste forskydning med lav nok score
		int hit = 0;
		for(int i = 60; i<scores.length-1;i++){		/*1F*/
			ned = (scores[i] - scores[i-1] < 0);
			if(i == scores.length) break;
			else{
				bund = (scores[i+1] - scores[i] >= 0);
				if(bund && ned && scores[i]<(min*1.1)){
					hit = i;
					//System.out.println(i);
					break;
				}
			}
		}
		
		// Til afpr�vning: Udskriv scores 
		/*
		 * for (int i = 0; i < scores.length; i++) { System.out.println((i + 1)
		 * + ": " + scores[i]); }
		 */
		
		try{
		double pulsgaet = 60000 / (hit * sampleTime);
		System.out.println("Bedste bud: " + pulsgaet);
		pulsmålinger.add(pulsgaet);
		} catch (ArithmeticException e1) {
			System.out.println("Divideret med 0");
			System.out.println("Bedste bud: Intet bud");
		}

		// Sikrer at vi kun gemmer de forrige 3 m�linger. 
		while (pulsmålinger.size() >= 3) {			/*1G*/
			pulsmålinger.remove(0);
		}

		// Finder gennemsnittet af denne m�ling og de forrige 2. 
		double jævnetPuls = 0;
		for (double puls : pulsmålinger) {			/*1H*/
			jævnetPuls += puls;
		}

		jævnetPuls = jævnetPuls / pulsmålinger.size();
		return jævnetPuls;
	}
}
