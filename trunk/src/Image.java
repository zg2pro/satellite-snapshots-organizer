
/**
 * Image est la classe representant les prises de vue
 *
 */
public class Image {

	/**
	 * L'indice de bande correspondant a l'image
	 */
	int indStrip;
	/**
	 * Sens dans lequel sont prises les bandes (true pour 1, 0 pour false)
	 */
	boolean isStripDirect;
	/**
	 * Date Tk a laquelle est prise l'image k
	 */
	int indImageTk;
	/**
	 * Duree de cette prise de vue
	 */
	int indImageDuj;
	/**
	 * Requete parente correspondant a la prise de vue
	 */
	int indImageRj;
	/**
	 * Surface couverte par la prise de vue
	 */
	int indImageSuj;

	/**
	 * Constructeur de Image pour des prises de vue a une date determinee
	 * @param indStrip
	 * @param isStripDirect
	 * @param indTk
	 * @param indDuj
	 * @param indRj
	 * @param indSuj
	 */
	public Image(int indStrip, boolean isStripDirect, 
			int indTk, int indDuj,
			int indRj, int indSuj){
		this.indStrip = indStrip;
		this.isStripDirect = isStripDirect;
		this.indImageTk = indTk;
		this.indImageDuj = indDuj;
		this.indImageRj = indRj;
		this.indImageSuj = indSuj;
	}//constructor

	/**
	 * Constructeur de Image pour des prises de vue qui ne sont pas encore ordonnancees
	 * @param indStrip
	 * @param isStripDirect
	 */
	public Image(int indStrip, boolean isStripDirect){
		this.indStrip = indStrip;
		this.isStripDirect = isStripDirect;
	}//constructor
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ("Bande: " + indStrip + 
				"  Sens: " + isStripDirect + " Date: " + indImageTk
		);
	}//toString
	
	
}//class
