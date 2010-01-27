import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;


public class Instance {

	/**
	 * nombre de requetes de l'instance
	 */
	protected int nbReq;
    /**
     * nombre de bandes de l'instance
     */
	protected int nbBd;
    /**
     * date moyenne des prises de vue de cette instance (repere de temps)
     */
	protected int indMiddleTMin;
    
    /**
     * encapsulation de la donnee nbBd
     * @return nombre de bandes de l'instance
     */
    public int getNbBd() {
		return nbBd;
	}//getNbBd

	/**
     * Ces trois valeurs se trouvaient dans le fichier
     * constants 
     * altitude en km
     * Vr en milliemes de radians par seconde (vitesse de rotation du satellite)
     * DMin est en milliemes de seconde (temps minimal de mouvement du satellite)
     */
    protected int indAltitude; protected double indVr;
 protected double indDMin;
    
    /**
     * Liste sans doublons des requetes de l'instance
     */
    protected Set<Request> setRequests;
	/**
	 * encapsulation de Reqs
	 * @return liste des requetes de l'instance
	 */
	public Set<Request> getSetRequests() {
		return setRequests;
	}//getReqs

	/**
	 * Liste des bandes de l'instance : ce type de tableau permet un acces direct a la bande cherchee
	 */
	protected Strip[] tabStrips;
	/**
	 * encapsulation de strips
	 * @return liste des bandes de l'instance
	 */
	public Strip[] getTabStrips() {
		return tabStrips;
	}//getTabStrips


	/**
	 * Constructeur d'une instance, initialisation de toutes les donnees
	 * @param url
	 * @param altitude
	 * @param Vr
	 * @param DMin
	 */
	public Instance(String url, int altitude, double Vr, double DMin){
		System.out.println("Reading File...");
		//ajout des constantes
		this.indAltitude = altitude;
		this.indVr = Vr;
		this.indDMin = DMin;
		setRequests = new HashSet<Request>();
		System.out.println("fichier : "+url);
		try{
			//lecture du fichier
			InputStream ips=new FileInputStream(url); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			//la premiere ligne n'est pas interessante
			br.readLine();
			//seconde ligne = nb de req et nd de bandes
			String line = br.readLine();
			StringTokenizer st = new StringTokenizer(line, " ");
	        nbReq = Integer.parseInt(st.nextToken());
	        nbBd = Integer.parseInt(st.nextToken());
	        tabStrips = new Strip[nbBd];
	        //la ligne 3 n'est pas interessante non plus
			br.readLine();
			for (int i=0; i<nbReq; i++){
				st = new StringTokenizer(br.readLine(), " ");
				setRequests.add(new Request(
						Integer.parseInt(st.nextToken()), 
						Integer.parseInt(st.nextToken()), 
						Float.parseFloat(st.nextToken()),  
						Integer.parseInt(st.nextToken()),  
						Integer.parseInt(st.nextToken())
						));
			}//for
			for (int i=0; i<nbBd; i++){
				st = new StringTokenizer(br.readLine(), " ");
				int ind = Integer.parseInt(st.nextToken());
				int Rj = Integer.parseInt(st.nextToken());
				st.nextToken();
				int Twj = Integer.parseInt(st.nextToken());
				br.readLine();
				st = new StringTokenizer(br.readLine(), " ");
				Float Suj = Float.parseFloat(st.nextToken());
				Float Duj = Float.parseFloat(st.nextToken());
				st = new StringTokenizer(br.readLine(), " ");
				int X0 = Integer.parseInt(st.nextToken());
				int Y0 = Integer.parseInt(st.nextToken());
				float Te0 = Float.parseFloat(st.nextToken());
				float Tl0 = Float.parseFloat(st.nextToken());
				st = new StringTokenizer(br.readLine(), " ");
				tabStrips[i] = new Strip(ind, Rj, Twj, Suj, Duj,
						X0, Y0, Te0, Tl0,
						Integer.parseInt(st.nextToken()), 
						Integer.parseInt(st.nextToken()), 
						Float.parseFloat(st.nextToken()), 
						Float.parseFloat(st.nextToken())
						);
			}//for
			br.close(); 
		} catch (Exception e){
			System.out.println(e.toString());
		}//catch

	}//constructor

	/**
	 * Pour chaque paire de bandes il y a 4 transitions possibles
	 *  Ce tableau contient les distances entre toute les bandes :
	 * cellule 0 = index de la premiere bande
	 * cellule 1 = 0 ou 1 : sens direct ou indirect
	 * cellule 2 = index de la seconde bande
	 * cellule 3 = 0 ou 1 : sens direct ou indirect
	 * cellule 4 = distance en milliers de km
	 * @return toutes les transitions possibles entre toutes les bandes de l'instance
	 */
	public Transition[] computeDistances(){
		System.out.println("Computing Transitions...");
		Transition[] instanceTransitions = new Transition[nbBd*(nbBd-1)*4];
		//i sera l'indice de la cellule du tableau
		int i=0;
		for (Strip aStrip : tabStrips){
			for (Strip anotherStrip : tabStrips)
				if (aStrip != anotherStrip){
					double dist = Math.sqrt(Math.pow(anotherStrip.getX0() - aStrip.getX0(), 2) + Math.pow(aStrip.getY0() - anotherStrip.getY0(), 2));
					dist = 2*(Math.atan(dist/((double)2000*indAltitude)));
					instanceTransitions[i] = new Transition(aStrip.getInd(), false, anotherStrip.getInd(), false, (int)((indDMin + (dist / indVr))*1000), i);
					i++;
					dist = Math.sqrt(Math.pow(anotherStrip.getX1() - aStrip.getX0(), 2) + Math.pow(aStrip.getY0() - anotherStrip.getY1(), 2));
					dist = 2*(Math.atan(dist/((double)2000*indAltitude)));
					instanceTransitions[i] = new Transition(aStrip.getInd(), false, anotherStrip.getInd(), true, (int)((indDMin + (dist / indVr))*1000), i);
					i++;
					dist = Math.sqrt(Math.pow(anotherStrip.getX0() - aStrip.getX1(), 2) + Math.pow(aStrip.getY1() - anotherStrip.getY0(), 2));
					dist = 2*(Math.atan(dist/((double)2000*indAltitude)));
					instanceTransitions[i] = new Transition(aStrip.getInd(), true, anotherStrip.getInd(), false, (int)((indDMin + (dist / indVr))*1000), i);
					i++;
					dist = Math.sqrt(Math.pow(anotherStrip.getX1() - aStrip.getX1(), 2) + Math.pow(aStrip.getY1() - anotherStrip.getY1(), 2));
					dist = 2*(Math.atan(dist/((double)2000*indAltitude)));
					instanceTransitions[i] = new Transition(aStrip.getInd(), true, anotherStrip.getInd(), true, (int)((indDMin + (dist / indVr))*1000), i);
					i++;
				}//i != j
			//calcul de indMiddleTMin au fur et a mesure en utilisant des coefficients 
			//pour eviter de depasser la taille maximale des int 
			int divider = ((2*i) / ((nbBd-1) * 4));
			indMiddleTMin = ((indMiddleTMin * (divider - 2)) + (aStrip.indTMin0 + aStrip.indTMin1)) / divider;
		}//for
		return instanceTransitions;
	}//computeDistances
	

}//class
