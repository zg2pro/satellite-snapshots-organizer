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

    int nbReq;
    int nbBd;
    
    public int getNbBd() {
		return nbBd;
	}

	/**
     * Ces trois valeurs se trouvaient dans le fichier
     * constants 
     * altitude en km
     * Vr en milliemes de radians par seconde
     * DMin est en milliemes de seconde
     */
    int altitude;
    double Vr, DMin;
    
	Set<Request> Reqs;
	public Set<Request> getReqs() {
		return Reqs;
	}

	Strip[] strips;
	public Strip[] getStrips() {
		return strips;
	}

	 
	

	public Instance(String url, int altitude, double Vr, double DMin){
		System.out.println("Reading File...");
		this.altitude = altitude;
		this.Vr = Vr;
		this.DMin = DMin;
		Reqs = new HashSet<Request>();
		//Strips = new HashSet<Strip>();
		System.out.println("fichier : "+url);
		try{
			InputStream ips=new FileInputStream(url); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			//la premiere ligne on s'en fout
			br.readLine();
			//seconde ligne = nb de req et nd de bandes
			String line = br.readLine();
			StringTokenizer st = new StringTokenizer(line, " ");
	        nbReq = Integer.parseInt(st.nextToken());
	        nbBd = Integer.parseInt(st.nextToken());
	        strips = new Strip[nbBd];
	        //on se fout encore de la ligne 3
			br.readLine();
			for (int i=0; i<nbReq; i++){
				st = new StringTokenizer(br.readLine(), " ");
				Reqs.add(new Request(
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
				strips[i] = new Strip(ind, Rj, Twj, Suj, Duj,
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
	
	public Transition[] computeDistances(){
		System.out.println("Computing Transitions...");
		/**
		 * Ce tableau contient les distances entre toute les bandes :
		 * ligne 0 = index de la premiere bande
		 * ligne 1 = 0 ou 1 : sens direct ou indirect
		 * ligne 2 = index de la seconde bande
		 * ligne 3 = 0 ou 1 : sens direct ou indirect
		 * ligne 4 = distance en milliers de km
		 */
		Transition[] allTransitions = new Transition[nbBd*(nbBd-1)*4];
		//allTransitions.setSize();
		int i=0;
		for (Strip aStrip : strips)
			for (Strip anotherStrip : strips)
				if (aStrip != anotherStrip){
					double dist = Math.sqrt(Math.pow(anotherStrip.getX0() - aStrip.getX0(), 2) + Math.pow(aStrip.getY0() - anotherStrip.getY0(), 2));
					dist = 2*(Math.atan(dist/((double)2000*altitude)));
					allTransitions[i] = new Transition(aStrip.getInd(), false, anotherStrip.getInd(), false, (int)((DMin + (dist / Vr))*1000), i);
					i++;
					dist = Math.sqrt(Math.pow(anotherStrip.getX1() - aStrip.getX0(), 2) + Math.pow(aStrip.getY0() - anotherStrip.getY1(), 2));
					dist = 2*(Math.atan(dist/((double)2000*altitude)));
					allTransitions[i] = new Transition(aStrip.getInd(), false, anotherStrip.getInd(), true, (int)((DMin + (dist / Vr))*1000), i);
					i++;
					dist = Math.sqrt(Math.pow(anotherStrip.getX0() - aStrip.getX1(), 2) + Math.pow(aStrip.getY1() - anotherStrip.getY0(), 2));
					dist = 2*(Math.atan(dist/((double)2000*altitude)));
					allTransitions[i] = new Transition(aStrip.getInd(), true, anotherStrip.getInd(), false, (int)((DMin + (dist / Vr))*1000), i);
					i++;
					dist = Math.sqrt(Math.pow(anotherStrip.getX1() - aStrip.getX1(), 2) + Math.pow(aStrip.getY1() - anotherStrip.getY1(), 2));
					dist = 2*(Math.atan(dist/((double)2000*altitude)));
					allTransitions[i] = new Transition(aStrip.getInd(), true, anotherStrip.getInd(), true, (int)((DMin + (dist / Vr))*1000), i);
					i++;
				}//i != j
		return allTransitions;
	}//computeDistances
	

}//class
