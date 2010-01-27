import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
ind:73 Gi:40000 Si:130.293 Ti:0 Sti:0
*/
public class Request {

	/**
	 * l'index
	 */
	private int ind;
	
	/** le type 
	* false = valeur 0, true = valeur 2 : polygone */
	private boolean Ti;
	
	/** le caractere (mono ou stereo) 
	 * mono : 0 stereo : 1 */
	private boolean Sti;
	
	/** surface a photographier en dizaines d'ares = 
	 * 1000km² pour eviter les virgules */
	private int Si;
	
	/** gain par km² */
	private int Gi;
	
	/** liste des bandes */
	private Set Bi;
	
	public Request (int ind, int Gi, float Si, int Ti, int Sti){
		this.ind = ind;
		this.Gi = Gi;
		this.Si = (int)(1000*Si);
		this.Ti = false;
		if (Ti == 2) this.Ti = true;
		this.Sti = false;
		if (Sti == 1) this.Sti = true;
	}//constructor

	public int getInd() {
		return ind;
	}

	public boolean isTi() {
		return Ti;
	}

	public boolean isSti() {
		return Sti;
	}

	public int getSi() {
		return Si;
	}

	public int getGi() {
		return Gi;
	}

	public Set getBi() {
		return Bi;
	}
	
}//Request
