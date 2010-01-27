
public class Transition implements Comparable<Transition> {

	/**
	 * les transitions doivent etre toutes differentes pour pouvoir etre recensees dans une map
	 * il leur faut donc un identifiant
	 */
	int indexInList;
	
	/**
	 * la description de la premiere bande de la transition
	 */
	int indFirstStrip; boolean isFirstStripDirect;
	
	/**
	 * description de la seconde bande
	 */
	int indSecondStrip; boolean isSecondStripDirect;
	
	/**
	 * la duree de passage de la premiere bande a la seconde
	 */
	int indMilliseconds;
	
	/**
	 * Constructeur des transitions
	 * @param indFirstStrip
	 * @param isFirstStripDirect
	 * @param indSecondStrip
	 * @param isSecondStripDirect
	 * @param indMilliseconds
	 * @param indexInList
	 */
	public Transition(int indFirstStrip, boolean isFirstStripDirect, 
			int indSecondStrip, boolean isSecondStripDirect, int indMilliseconds, int indexInList){
		this.indFirstStrip = indFirstStrip;
		this.isFirstStripDirect = isFirstStripDirect;
		this.indSecondStrip = indSecondStrip;
		this.isSecondStripDirect = isSecondStripDirect;
		this.indMilliseconds = indMilliseconds;
		this.indexInList = indexInList;
	}//constructor

	@Override
	public int compareTo(Transition o) {
		// TODO Auto-generated method stub
		return this.indexInList - o.indexInList;
	}//compareTo

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return indFirstStrip+"["+isFirstStripDirect+"]->-"
		+indSecondStrip+"["+isSecondStripDirect+"] : "+indMilliseconds;
	}//toString

	
}//class
