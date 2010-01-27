
public class Transition implements Comparable<Transition> {

	int indexInList;
	
	int indFirstStrip;
	boolean isFirstStripDirect;
	
	int indSecondStrip;
	boolean isSecondStripDirect;
	
	int indMilliseconds;
	
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
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return indFirstStrip+"["+isFirstStripDirect+"]->-"
		+indSecondStrip+"["+isSecondStripDirect+"] : "+indMilliseconds;
	}

	

}//class
