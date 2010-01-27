
public class Image {

	int indStrip;
	boolean isStripDirect;
	int indDate;
	int indLasting;
	int Rj;
	int Suj;

	public Image(int indStrip, boolean isStripDirect, 
			int indDate, int indLasting,
			int Rj, int Suj/*, int gain*/){
		this.indStrip = indStrip;
		this.isStripDirect = isStripDirect;
		this.indDate = indDate;
		this.indLasting = indLasting;
		this.Rj = Rj;
		this.Suj = Suj;
	}//constructor

	public Image(int indStrip, boolean isStripDirect){
		this.indStrip = indStrip;
		this.isStripDirect = isStripDirect;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ("Bande: " + indStrip + 
				"  Sens: " + isStripDirect + " Date: " + indDate
		);
	}//toString
	
	
}//class
