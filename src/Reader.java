import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe principale : utilisation des classe de lecture et de calcul
 * @author gregory, yoann
 *
 */
public class Reader {

	/**
	 * tableau des instances, il contiendra toutes les instances du repertoire
	 */
	Instance[] theInstances;
	
	/**
	 * Constructeur de Reader pour une liste d'instance, 
	 * calculs a la chaine en accordant 5 minutes par instance
	 * @param neighborhoodModeOn
	 */
	public Reader (boolean neighborhoodModeOn){
		File fDirectory = new File("./Instances" );
		String[] strFilenames = fDirectory.list();
		theInstances = new Instance[strFilenames.length];
		int i = 0;
		for (String it : strFilenames){
			theInstances[i] = new Instance("./Instances/"+it, 633, 0.05, 2.5);
			//ecriture des resultats dans le repertoire Solutions
			Util.writeSolutionInFile(
					(new Solution(theInstances[i++], neighborhoodModeOn)).finalVector, 
					it.replaceAll("instance_", ""));
		}//for
	}//constructor
	
	/**
	 * Constructeur du Reader pour une unique instance
	 * @param strInstanceNumber
	 * @param neighborhoodModeOn
	 */
	public Reader (String strInstanceNumber, boolean neighborhoodModeOn){
			theInstances = new Instance[1];
			theInstances[0] = new Instance("./Instances/instance_"+strInstanceNumber, 633, 0.05, 2.5);
			Util.writeSolutionInFile(
					(new Solution(theInstances[0], neighborhoodModeOn)).finalVector, 
					strInstanceNumber);
	}//constructor 2
	
	/**
	 * methode d'execution du programme
	 * @param args
	 */
	public static void main (String [] args){
		try {
			new Reader(args[0], true);
		} catch (ArrayIndexOutOfBoundsException e){
			new Reader (true);
		} catch (Exception e){
			System.err.println(e.getMessage());
		}//catch
	}//main
	
}//class
