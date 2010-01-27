
/**
 * Representation d'une bande
* ind:106 Rj:2544 separateur:0 Twj:0
* sautdeligne:0 0
* Suj:488.181 Duj:19.70
* X0:-3166067 Y0:-286637 Te0:969.41 Tl0:1041.67
* X1:-3100574 Y1:-302416 Te1:981.66 Tl1:1048.34
*/
public class Strip implements Comparable<Strip> {

	/** ID de la bande */
	private int indStrip;
	
	/** Requete parente */
	private int indRj;
	
	/** ID de la bande jumelle, 0 sinon */
	private int indTwj;
	
	/** Surface de la bande en dizaines d'ares = 1000x km²
	 * pour eviter les virgules */
	private int indSuj; 
	
	/** duree de prise des photos de la bande */
	private int indDuj;
	

	/** debut au plus tot, debut au plus tard */
	//private int TMink, TMaxk;
	int indTMin0;
	/** debut au plus tot, debut au plus tard */
	//private int TMink, TMaxk;
	int indTMax0;
	public int getTMin0() {
		return indTMin0;
	}//getTMin0

	public int getTMax0() {
		return indTMax0;
	}//getTMax0

	public int getTMin1() {
		return indTMin1;
	}//getTMin1

	public int getTMax1() {
		return indTMax1;
	}//getTMax1

	int indTMin1;
	int indTMax1;

	public int getInd() {
		return indStrip;
	}//getInd

	public int getRj() {
		return indRj;
	}//getRj

	public int getTwj() {
		return indTwj;
	}//getTwj

	public int getSuj() {
		return indSuj;
	}//getSuj

	public int getDuj() {
		return indDuj;
	}//getDuj

	public int getX0() {
		return X0;
	}//getX0

	public int getY0() {
		return Y0;
	}//getY0

	public int getTe0() {
		return Te0;
	}//getTe0

	public int getTl0() {
		return Tl0;
	}//getTl0

	public int getX1() {
		return X1;
	}//getx1

	public int getY1() {
		return Y1;
	}//gety1

	public int getTe1() {
		return Te1;
	}//getTe1

	public int getTl1() {
		return Tl1;
	}//getTl1

	/** coordonnees de l'espace et du temps */
	private int X0;
	/** coordonnees de l'espace et du temps */
	private int Y0;
	/** coordonnees de l'espace et du temps */
	private int Te0;
	/** coordonnees de l'espace et du temps */
	private int Tl0;
	/** coordonnees de l'espace et du temps */
	private int X1;
	/** coordonnees de l'espace et du temps */
	private int Y1;
	/** coordonnees de l'espace et du temps */
	private int Te1;
	/** coordonnees de l'espace et du temps */
	private int Tl1;

	/**
	 * Constructeur de bande
	 * @param ind
	 * @param Rj
	 * @param Twj
	 * @param Suj
	 * @param Duj
	 * @param X0
	 * @param Y0
	 * @param Te0
	 * @param Tl0
	 * @param X1
	 * @param Y1
	 * @param Te1
	 * @param Tl1
	 */
	public Strip (int ind, int Rj, int Twj, 
			float Suj, float Duj, int X0, int Y0, 
			float Te0, float Tl0, int X1, int Y1, float Te1, float Tl1){
		this.indStrip = ind;
		this.indRj = Rj;
		this.indTwj = Twj;
		this.indSuj = (int)(1000*Suj);
		this.indDuj = (int)(1000*Duj);
		this.X0 = X0;
		this.Y0 = Y0;
		this.Te0 = (int)(1000*Te0);
		this.Tl0 = (int)(1000*Tl0);
		this.X1 = X1;
		this.Y1 = Y1;
		this.Te1 = (int)(1000*Te1);
		this.Tl1 = (int)(1000*Tl1);
		indTMin0 = Math.max(this.Te1, this.Te0 - this.indDuj);
		indTMax0 = Math.max(this.Tl1, this.Tl0 - this.indDuj);
		indTMin1 = Math.max(this.Te0, this.Te1 - this.indDuj);
		indTMax1 = Math.max(this.Tl0, this.Tl1 - this.indDuj);
		//indTMin0 = this.Te0;
		//indTMax0 = this.Tl0;
		//indTMin1 = this.Te1;
		//indTMax1 = this.Tl1;
	}//constructor

	@Override
	public int compareTo(Strip other) {
		// TODO Auto-generated method stub
		return indStrip - other.getInd();
	}//compareTo

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "indice: " + indStrip + "-duree: " + indDuj;
	}//toString
	
}//Strip
