
/**
* ind:106 Rj:2544 separateur:0 Twj:0
* sautdeligne:0 0
* Suj:488.181 Duj:19.70
* X0:-3166067 Y0:-286637 Te0:969.41 Tl0:1041.67
* X1:-3100574 Y1:-302416 Te1:981.66 Tl1:1048.34
*/
public class Strip implements Comparable<Strip> {

	/** ID de la bande */
	private int ind;
	
	/** Requete parente */
	private int Rj;
	
	/** ID de la bande jumelle, 0 sinon */
	private int Twj;
	
	/** Surface de la bande en dizaines d'ares = 1000x km²
	 * pour eviter les virgules */
	private int Suj; 
	
	/** duree de prise des photos de la bande */
	private int Duj;
	/*
	private int relativeGainOnRequest;
	public int getRelativeGainOnRequest() {
		return relativeGainOnRequest;
	}

	public void setRelativeGainOnRequest(int relativeGainOnRequest) {
		this.relativeGainOnRequest = relativeGainOnRequest;
	}
*/
	/** debut au plus tot, debut au plus tard */
	//private int TMink, TMaxk;
	int TMin0, TMax0;
	public int getTMin0() {
		return TMin0;
	}

	public int getTMax0() {
		return TMax0;
	}

	public int getTMin1() {
		return TMin1;
	}

	public int getTMax1() {
		return TMax1;
	}

	int TMin1, TMax1;

	public int getInd() {
		return ind;
	}

	public int getRj() {
		return Rj;
	}

	public int getTwj() {
		return Twj;
	}

	public int getSuj() {
		return Suj;
	}

	public int getDuj() {
		return Duj;
	}

	public int getX0() {
		return X0;
	}

	public int getY0() {
		return Y0;
	}

	public int getTe0() {
		return Te0;
	}

	public int getTl0() {
		return Tl0;
	}

	public int getX1() {
		return X1;
	}

	public int getY1() {
		return Y1;
	}

	public int getTe1() {
		return Te1;
	}

	public int getTl1() {
		return Tl1;
	}

	/** coordonnees de l'espace et du temps */
	private int X0, Y0, Te0, Tl0, X1, Y1, Te1, Tl1;
	
	public Strip (int ind, int Rj, int Twj, 
			float Suj, float Duj, int X0, int Y0, 
			float Te0, float Tl0, int X1, int Y1, float Te1, float Tl1){
		this.ind = ind;
		this.Rj = Rj;
		this.Twj = Twj;
		this.Suj = (int)(1000*Suj);
		this.Duj = (int)(1000*Duj);
		this.X0 = X0;
		this.Y0 = Y0;
		this.Te0 = (int)(1000*Te0);
		this.Tl0 = (int)(1000*Tl0);
		this.X1 = X1;
		this.Y1 = Y1;
		this.Te1 = (int)(1000*Te1);
		this.Tl1 = (int)(1000*Tl1);
		TMin0 = Math.max(this.Te1, this.Te0 - this.Duj);
		TMax0 = Math.max(this.Tl1, this.Tl0 - this.Duj);
		TMin1 = Math.max(this.Te0, this.Te1 - this.Duj);
		TMax1 = Math.max(this.Tl0, this.Tl1 - this.Duj);
	}//constructor

	@Override
	public int compareTo(Strip other) {
		// TODO Auto-generated method stub
		return ind - other.getInd();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "indice: " + ind + "-duree: " + Duj;
	}
	
}//Strip
