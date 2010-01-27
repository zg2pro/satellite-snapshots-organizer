import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

/**
 * Lors d'une insertion en milieu de vecteur, il faut redimmensionner toutes
 * les bandes correspondantes aux prises de vue qui sont posterieures a l'insertion.
 * A la fin de la transformation il est necessaire de diposer a la fois du vecteur redimmensionne
 * ainsi que des transitions redimmensionnees pour pouvoir poursuivre les tests d'insertion
 */
class resizingTool {
	Vector<Image> theVector;
	Map<Transition, Strip> theMap;
	
	/**
	 * Constructeur de l'outil
	 * @param theVector
	 * @param theMap
	 */
	public resizingTool(Vector<Image> theVector, Map<Transition, Strip> theMap) {
		this.theVector = theVector;
		this.theMap = theMap;
	}//constructor
}//class resizingTool

/**
 * Classe de calcul des solutions possibles ainsi que du choix optimal en gain
 * 
 */
public class Solution {

	/**
	 * cette variable permet de savoir si l'execution a pu se derouler entierement
	 * ou si elle a du etre coupee parce que trop longue
	 */
	protected boolean disposedTooEarly = true;
	
	/**
	 * le vecteur de la solution retenue : celle dont le gain est maximal
	 */
	public Vector<Image> finalVector;
	
	/**
	 * vecteur contenant les prises de vue qui n'ont ete inserees dans aucune solution
	 */
	protected Vector<Image> theStillNotUsedStrips;
	
	/**
	 * liste de vecteurs contenant les differentes solutions
	 */
	protected Vector<Vector<Image>> theListOfSolutionsToCompare;
	
	/**
	 * transitions possibles de l'instance
	 */
	protected Transition[] instanceTransitions;
	
	/**
	 * toutes les bandes disponibles de l'instance
	 */
	protected Strip[] instanceStrips;
	
	/**
	 * toutes les requetes de l'instance
	 */
	protected Set<Request> instanceRequests;
	
	/**
	 * Nombre de bandes dans l'instance
	 */
	protected int indNbStrips;
	
	/**
	 * Date de mi-temps de l'instance
	 */
	protected int middleStripsDates;
	
	/**
	 * Arbre de toutes les transitions avec pour valeur 
	 * les bandes correspondant a la premiere bande de la transition
	 */
	protected Map<Transition, Strip> mapOfTransitionsSortedByFirstStrip;

	/**
	 * Arbre de toutes les transitions avec pour valeur 
	 * les bandes correspondant a la seconde bande de la transition
	 */
	protected Map<Transition, Strip> mapOfTransitionsSortedBySecondStrip;
	
	/**
	 * Mode d'execution des calculs : 
	 * la methode voisinage donnant des resultats peu exhaustifs, 
	 * la methode de reprise des calculs pour chaque nouvelle bande peut etre utile
	 */
	protected boolean neighborhoodModeOn;
	

	/**
	 * affichage de la liste des solutions possibles
	 */
	public void displayListOfSolutions(){
		for (int i=0; i<theListOfSolutionsToCompare.size(); i++)
			System.out.println("---Un parcours possible--- : "+i+" "+theListOfSolutionsToCompare.elementAt(i));
	}//displayListOfSolutions
	
	
	/**
	 * creation d'une nouvelle solution a partir d'une prise de vue pas encore utilisee
	 * @param indexInListOfSolutions est le nombre de solutions deja generees
	 * @return fillSolutions(ce nouveau vecteur);
	 */
	protected boolean createNewVector(int indexInListOfSolutions){
		System.out.println("still not used strips: ");
		for (Image img : theStillNotUsedStrips)
			System.out.println("||"+img+"||");
		
		//Recherche de la premiere bande toujours absente dans les solutions
		Strip theNewInitialStrip = instanceStrips[0];
		for (Strip s : instanceStrips)
			if (s.getInd() == theStillNotUsedStrips.get(0).indStrip){
				theNewInitialStrip = s;
				break;
			}//if
		
		Vector<Image> theNewInitialVector = new Vector<Image>();
		theNewInitialVector.setSize(1);
		//date Tk a laquelle on inserera la prise de vue
		int indTk = 0;
		//isAtLeft indique si on collera la prise de vue au debut ou a la fin
		//de la fenetre de temps possible du debut de cette prise
		boolean isAtLeft = true;
		if (theStillNotUsedStrips.get(0).isStripDirect){
			//selon le sens de la prise de vue on utilise les dates 1 ou 0
			if (theNewInitialStrip.getTMin1() < middleStripsDates) 
				indTk  = theNewInitialStrip.getTMin1();
			else {
				//pour une bande tardive
				indTk =  theNewInitialStrip.getTMax1();
				isAtLeft = false;
			}//else
		} else {
			if (theNewInitialStrip.getTMin0() < middleStripsDates) 
				indTk  = theNewInitialStrip.getTMin0();
			else {
				indTk =  theNewInitialStrip.getTMax0();
				isAtLeft = false;
			}//else
		}//else
		
		//instanciation de la prise de vue
		Image newInitialImage = new Image(theNewInitialStrip.getInd(), 
				theStillNotUsedStrips.get(0).isStripDirect, 
				indTk, 
				theNewInitialStrip.getDuj(),
				theNewInitialStrip.getRj(),
				theNewInitialStrip.getSuj()
				);
		
		//insertion de la prise de vue dans un vecteur ne contenant qu'elle
		theNewInitialVector.set(0, newInitialImage);
		
		//redimmensionner dans TransitionsSortedByFirst, TransitionsSortedBySecond
		Map <Transition, Strip> tmpTransitionsForInsertionBefore = new TreeMap<Transition, Strip>();
		Map <Transition, Strip> tmpTransitionsForInsertionAfter = new TreeMap<Transition, Strip>();
		tmpTransitionsForInsertionBefore.putAll(mapOfTransitionsSortedByFirstStrip);
		tmpTransitionsForInsertionAfter.putAll(mapOfTransitionsSortedBySecondStrip);
		if (isAtLeft){
			for (Transition t : tmpTransitionsForInsertionAfter.keySet()){
				if (tmpTransitionsForInsertionAfter.get(t).getInd() == theNewInitialStrip.getInd())
					if (theStillNotUsedStrips.get(0).isStripDirect)
						tmpTransitionsForInsertionAfter.get(t).indTMax1 = indTk;
					else tmpTransitionsForInsertionAfter.get(t).indTMax0 = indTk;
			}//for
		}//pour une bande tot
			
		//si on colle tout a droite il n'y a pas de redimmensionnement
		

		//si la bande est a gauche on ne pourra rien inserer avant
		Set<Transition> transitionsToRemove = new HashSet<Transition>();
		if (isAtLeft){
			for (Transition t : tmpTransitionsForInsertionBefore.keySet()){
				if (t.indSecondStrip == theNewInitialStrip.getInd())
					transitionsToRemove.add(t);
			}//for
		}//if
		
		//la suppression doit se faire en deux temps : on ne peut pas redimmensionner une liste
		//en cours de parcours
		for (Transition t : transitionsToRemove)
			tmpTransitionsForInsertionBefore.remove(t);
		
		
		// On reinsere pas l'element a droite non plus
		transitionsToRemove = new HashSet<Transition>();
		for (Transition t : tmpTransitionsForInsertionAfter.keySet()){
			if (t.indSecondStrip == theNewInitialStrip.getInd())
				transitionsToRemove.add(t);
		}//for
		for (Transition t : transitionsToRemove)
			tmpTransitionsForInsertionAfter.remove(t);

		//suppression de la bande dont on vient de se servir des bandes dont on ne s'est pas encore servies
		theStillNotUsedStrips.remove(0);
		
		//nouveau calcul des bandes que l'on pourra coller a la nouvelle prise de vue
		System.out.println("Searching another solution...: "+indexInListOfSolutions);
		return fillSolutionsToCompare(theNewInitialVector, indexInListOfSolutions,
				tmpTransitionsForInsertionBefore, tmpTransitionsForInsertionAfter, false);

	}//createNewVector
	
	/**
	 * Initialisation des valeurs de calcul
	 */
	public void init(){
		System.out.println("Initiating research tools...");
		
		//Tri selon le premier indice de la transition
		Arrays.sort(instanceTransitions,  new Comparator() { 
            public int compare(Object o1, Object o2) {
            	return (((Transition)o1).indFirstStrip - ((Transition)o2).indFirstStrip);
            }
        });
		Arrays.sort(instanceStrips);
		mapOfTransitionsSortedByFirstStrip = new TreeMap<Transition, Strip>();
		//on remplit la map en fonction des indices en profitant du tri
		for(int i = 0; i<instanceTransitions.length ; i++){
			int zz = i / ((indNbStrips - 1) * 4);
			mapOfTransitionsSortedByFirstStrip.put(instanceTransitions[i], instanceStrips[zz]);
		}//for
		
		//operation inverse pour la seconde map
		Arrays.sort(instanceTransitions,  new Comparator() { 
            public int compare(Object o1, Object o2) {
            	return (((Transition)o1).indSecondStrip - ((Transition)o2).indSecondStrip);
            }
        });
		mapOfTransitionsSortedBySecondStrip = new TreeMap<Transition, Strip>();
		for(int i = 0; i<instanceTransitions.length ; i++){
			int zz = i / ((indNbStrips - 1) * 4);
			mapOfTransitionsSortedBySecondStrip.put(instanceTransitions[i], instanceStrips[zz]);
		}//for

		//creation du tableau des bandes toujours a utiliser
		theStillNotUsedStrips = new Vector<Image>();
		//theStillNotUsedStrips.add(new Image(allStrips[0].getInd(), true));
		for (int i = 0; i<instanceStrips.length; i++){
			theStillNotUsedStrips.add(new Image(instanceStrips[i].getInd(), false));
			theStillNotUsedStrips.add(new Image(instanceStrips[i].getInd(), true));
		}//for

		//instanciation du vecteur general des solutions
		theListOfSolutionsToCompare = new Vector<Vector<Image>>();
		
		//ajout d'un premier vecteur a ces solutions
		createNewVector(0);
		
	}//init

	/**
	 * A chaque ajout d'une prise de vue dans un vecteur, certaines transitions deviennent impossibles
	 * il est alors possible de les eliminer
	 * @param theTransitionsForInsertionNearCurrentImage est la map concernee dans laquelle 
	 * on va supprimer certaines possibilites 
	 * @param t est la transition correspondant au dernier ajout de la prise de vue
	 * @param s est la bande correspondante a l'ajout de prise de vue
	 * @return la map reduite aux elements possiles (ce qui permet de gagner du temps dans les parcours)
	 */
	protected Map<Transition, Strip> extractingTransitionsToRemove(
			Map<Transition, Strip> theTransitionsForInsertionNearCurrentImage, 
			Transition t, Strip s){
		//pour savoir quel type de map on traite
		boolean stripIsFirst = false;
		if (s.getInd() == t.indFirstStrip) stripIsFirst = true;
		
		Set<Transition> tmpCopyKeySet = new HashSet();
	
		//on elimine toutes les transitions deja presentes dans le vecteur
		for (Transition transitionsRemaining : theTransitionsForInsertionNearCurrentImage.keySet())
			if (!(t.indFirstStrip == transitionsRemaining.indFirstStrip
					&& transitionsRemaining.indSecondStrip == t.indSecondStrip))
				tmpCopyKeySet.add(transitionsRemaining);
		
		Map<Transition, Strip> toReturn = new TreeMap<Transition, Strip>();
		
		for (Transition aTransition : tmpCopyKeySet)
			toReturn.put(aTransition, theTransitionsForInsertionNearCurrentImage.get(aTransition));
		
		return toReturn;
	}//extractingRemovable
	

	/**
	 * Redimmensionnement des fenetres : apres l'ajout d'un element, il est essentiel 
	 * de reduire les fenetres de temps dans lesquels il est possible d'inserer les prochaines prises de vue.
	 * @param theTransitionsForInsertionNearCurrentImage
	 * @param theVector
	 * @param indRecursionInit
	 * @param delayToRescale
	 * @param isFirstRecursion
	 * @return la map et le vecteur
	 */
	protected resizingTool reScalingStrips (
			Map<Transition, Strip> theTransitionsForInsertionNearCurrentImage, 
			Vector<Image> theVector, 
			int indRecursionInit,
			int delayToRescale, 
			boolean isFirstRecursion){
			
			//condition de fin:
			if (indRecursionInit == theVector.size())
				return  (new resizingTool(theVector, theTransitionsForInsertionNearCurrentImage));
			
			//la recursion:
			Image currentImage = theVector.elementAt(indRecursionInit);
			for (Transition t : theTransitionsForInsertionNearCurrentImage.keySet()){
				if (currentImage.indStrip == theTransitionsForInsertionNearCurrentImage.get(t).getInd()){
					Strip theStripToRescale = theTransitionsForInsertionNearCurrentImage.get(t); 
					if (currentImage.isStripDirect){
						//on utilise les 1
						if ((theStripToRescale.indTMax1 - delayToRescale) < theStripToRescale.indTMin1){
							//si le delai est trop grand, on ne recule pas la prise de vue completement
							delayToRescale = theStripToRescale.indTMax1 - theStripToRescale.indTMin1;
							theTransitionsForInsertionNearCurrentImage.get(t).indTMax1 = theStripToRescale.indTMin1;
							if (!isFirstRecursion) theVector.elementAt(indRecursionInit).indImageTk = currentImage.indImageTk - delayToRescale;
						} else {
							theTransitionsForInsertionNearCurrentImage.get(t).indTMax1 = theStripToRescale.indTMax1 - delayToRescale;
							if (!isFirstRecursion) theVector.elementAt(indRecursionInit).indImageTk = currentImage.indImageTk - delayToRescale;
						}//else
					} else {
						//on utilise les 0	
						if ((theStripToRescale.indTMax0 - delayToRescale) < theStripToRescale.indTMin0){
							delayToRescale = theStripToRescale.indTMax0 - theStripToRescale.indTMin0;
							theTransitionsForInsertionNearCurrentImage.get(t).indTMax0 = theStripToRescale.indTMin0;
							if (!isFirstRecursion) theVector.elementAt(indRecursionInit).indImageTk = currentImage.indImageTk - delayToRescale;
						} else {
							theTransitionsForInsertionNearCurrentImage.get(t).indTMax0 = theStripToRescale.indTMax0 - delayToRescale;
							if (!isFirstRecursion) theVector.elementAt(indRecursionInit).indImageTk = currentImage.indImageTk - delayToRescale;
						}//else
					}//else
				}//if strip = image 
				//(les strips de transition dans le mauvais sens ne doivent plus y etre)
			}//for each transition		
			return  reScalingStrips(
					theTransitionsForInsertionNearCurrentImage, 
					theVector, 
					indRecursionInit + 1, 
					delayToRescale,
					false
				);
	}//RescalingStrips
	
	
	/**
	 * fonction remaplacant createNewVector : au lieu de creer un nouveau vecteur a partir de la prise de vue
	 * manquante, on essaie d'inserer cette prise de vue dans une des parties d'un vecteur voisin
	 * @param indexInListOfSolutions
	 */
	protected void neighborGeneration (int indexInListOfSolutions){
		
		//on recupere un element qui n'est pas present
		Image stillNotUsedImage = new Image(theStillNotUsedStrips.firstElement().indStrip, 
				theStillNotUsedStrips.firstElement().isStripDirect);
		
		System.out.println("tentative de voisinage avec l'image: "+stillNotUsedImage);
		
		//date logique a laquelle la prise de vue devrait etre inseree.
		int averageTk = 0;
		for (Strip s : instanceStrips)
			if (s.getInd() == stillNotUsedImage.indStrip){
					if (stillNotUsedImage.isStripDirect) averageTk = (s.indTMax1 + s.indTMin1) / 2;
					else averageTk = (s.indTMax0 + s.indTMin0) / 2;
					break;
				}//if
		
		
		//recherche du plus proche voisin dans les solutions deja etablies
		Vector<Image> theNewVector;
		int shortestTime = Integer.MAX_VALUE;
		int shortestTimeIndexInList = -1;
		int shortestTimeIndexInVector = -1;
		for (int i = 0; i<theListOfSolutionsToCompare.size(); i++){
			for (int j=0; j< theListOfSolutionsToCompare.elementAt(i).size(); j++){
					int thisTime = Math.abs(theListOfSolutionsToCompare.elementAt(i).elementAt(j).indImageTk - averageTk);
					if (shortestTime > thisTime) {
						shortestTime = thisTime;
						shortestTimeIndexInList = i;
						shortestTimeIndexInVector = j;
					}//if
			}//for
		}//for
		
		if (shortestTimeIndexInVector > -1){

		//il faut savoir si l'element n'existe pas deja dans la liste en sens inverse
		int indElementAlreadyInList = -1;
		for (int j=0; j<theListOfSolutionsToCompare.elementAt(shortestTimeIndexInList).size(); j++){
			if (theListOfSolutionsToCompare.elementAt(shortestTimeIndexInList).elementAt(j).indStrip == stillNotUsedImage.indStrip)
				indElementAlreadyInList = j;
		}//for
		
		
		//creation de la partie du vecteur
		theNewVector = new Vector<Image>();
		theNewVector.setSize(0);
		if (indElementAlreadyInList > -1){
			for (int j=0; j<theListOfSolutionsToCompare.elementAt(shortestTimeIndexInList).size(); j++){
				if (j != indElementAlreadyInList){
					int index = j; 
					if (j > indElementAlreadyInList) index--;
					theNewVector.insertElementAt(theListOfSolutionsToCompare.elementAt(shortestTimeIndexInList).elementAt(j), index);
				}//if
			}//for
		} else {
			for (int j=0; j<theListOfSolutionsToCompare.elementAt(shortestTimeIndexInList).size(); j++){
				if (j != shortestTimeIndexInVector){
					int index = j; 
					if (j > shortestTimeIndexInVector) index--; 
					theNewVector.insertElementAt(theListOfSolutionsToCompare.elementAt(shortestTimeIndexInList).elementAt(j), index);
				}//if
			}//for
		}
		

		indexInListOfSolutions++;
		//lorsque toutes parties n'ont pas suffit, on utilise la methode de creation
		if (theNewVector.isEmpty() || theNewVector ==  null){
			System.out.println("la methode du voisinage n'a pas ete efficace :" +
					" generation d'un nouveau vecteur a partir de l'element");
			createNewVector(indexInListOfSolutions);
		}//if

		//on va creer des parties du vecteur tant qu'il n'est pas possible d'inserer la prise de vue.
		boolean theImageIsNotYetInserted = true;
		while (theImageIsNotYetInserted && !theNewVector.isEmpty()){
			

			System.out.println("---Partie de vecteur cree--- :  "+theNewVector);
			
			//recuperation de la map des transitions possibles
			Map<Transition, Strip> tmpBefore =  new TreeMap<Transition, Strip>();
			Map<Transition, Strip> tmpAfter =  new TreeMap<Transition, Strip>();
			for (Image img : theStillNotUsedStrips){
				
			for (int i = 0; i<theNewVector.size(); i++){
				for (Transition t : mapOfTransitionsSortedByFirstStrip.keySet()){
					if ((theNewVector.elementAt(i).indStrip == t.indSecondStrip 
							&& theNewVector.elementAt(i).isStripDirect == t.isSecondStripDirect
							&& img.indStrip == t.indFirstStrip
							&& img.isStripDirect == t.isFirstStripDirect)  )
						tmpBefore.put(t, mapOfTransitionsSortedByFirstStrip.get(t));
				}//for
					for (Transition t : mapOfTransitionsSortedBySecondStrip.keySet()){
						if ((theNewVector.elementAt(i).indStrip == t.indFirstStrip 
								&& theNewVector.elementAt(i).isStripDirect == t.isFirstStripDirect
								&& img.indStrip == t.indSecondStrip
								&& img.isStripDirect == t.isSecondStripDirect)  )
							tmpAfter.put(t, mapOfTransitionsSortedBySecondStrip.get(t));
					}//for
			}//for vector
			
			}//for theStillNotUsedStrips
	

			
			fillSolutionsToCompare(theNewVector, indexInListOfSolutions, tmpBefore, tmpAfter, true);
			
			//si l'image que l'on veut inserer l'a ete, elle a du disparaitre de la liste des elements a inserer
			try {
				theImageIsNotYetInserted = (stillNotUsedImage.indStrip == theStillNotUsedStrips.firstElement().indStrip
						&& stillNotUsedImage.isStripDirect == theStillNotUsedStrips.firstElement().isStripDirect);
			} catch (NoSuchElementException e) {
				theImageIsNotYetInserted = false;
			} catch (Exception e){
				System.err.println(e.getMessage());
			}//catch
				
				
			//preparation de la prochaine boucle : creation d'une nouvelle partie
			if (theImageIsNotYetInserted){
				
				//On va devoir reprendre l'iteration
				shortestTime = Integer.MAX_VALUE;
				shortestTimeIndexInList = theListOfSolutionsToCompare.size() - 1;
				shortestTimeIndexInVector = -1;
				for (int j=0; j< theListOfSolutionsToCompare.elementAt(shortestTimeIndexInList).size(); j++){
							int thisTime = Math.abs(theListOfSolutionsToCompare.elementAt(shortestTimeIndexInList).elementAt(j).indImageTk - averageTk);
							if (shortestTime > thisTime) {
								shortestTime = thisTime;
								shortestTimeIndexInVector = j;
						}//if
					}//for
					Vector<Image> tmpCopyOfNVector = new Vector<Image>();
					for (int j=0; j<theNewVector.size(); j++){
						if (j != shortestTimeIndexInVector){
							int index = j; if (j > shortestTimeIndexInVector) index--;
							tmpCopyOfNVector.insertElementAt(theNewVector.elementAt(j), index);
						}//if
					}//for
					theNewVector = tmpCopyOfNVector;
					
					//lorsque toutes parties n'ont pas suffit, on utilise la methode de creation
					if (theNewVector.isEmpty() || theNewVector ==  null){
						System.out.println("la methode du voisinage n'a pas ete efficace :" +
								" generation d'un nouveau vecteur a partir de l'element");
						createNewVector(indexInListOfSolutions);
					}//if
			}//if	
		}//while
		
		}//if
		
	}//neighborhoodGeneration
	
	/**
	 * Quand un vecteur solution est cree il est rempli a travers cette procedure recursive
	 * @param initialVector est le vecteur de taille 1 au depart, s'élargissant au fur et a mesure de la recursion
	 * @param indexInListOfSolutions
	 * @param theTransitionsForInsertionBeforeCurrentImage
	 * @param theTransitionsForInsertionAfterCurrentImage
	 * @param onlyTryInsertion est utile a la gestion du voisinage
	 * @return true
	 */
	public boolean fillSolutionsToCompare (Vector<Image> initialVector, int indexInListOfSolutions,
			Map<Transition, Strip> theTransitionsForInsertionBeforeCurrentImage, 
			Map<Transition, Strip> theTransitionsForInsertionAfterCurrentImage,
			boolean onlyTryInsertion){
		
		
		//On parcourt le vecteur en cours de remplissage de gauche a droite pour trouver des espaces
		for (int i = 0; i < initialVector.size(); i++){
			
			if (i == 0){
			//insertion en tete
			Image currentImage = initialVector.elementAt(0);
				for (Transition t : theTransitionsForInsertionBeforeCurrentImage.keySet()) {
					if (t.indSecondStrip == currentImage.indStrip
							&& t.isSecondStripDirect == currentImage.isStripDirect){
						//je devrais pas etre oblige de faire ca logiquement mais pourtant, c'est un mystere...
						boolean cancel = false;
						//pour pas remettre un element qui y est deja
						for(Image img : initialVector) 
							if(img.indStrip == t.indFirstStrip){
							cancel = true;
							break;
						}//if
						Strip theStripOfT = theTransitionsForInsertionBeforeCurrentImage.get(t);
						int min = 0, max = 0;
						if (t.isFirstStripDirect){
							min = theStripOfT.getTMin1();
							max = theStripOfT.getTMax1();
						} else {
							min = theStripOfT.getTMin0();
							max = theStripOfT.getTMax0();
						}//else
						int Tk = currentImage.indImageTk - (theStripOfT.getDuj() + t.indMilliseconds);
						//si la contrainte est verifiee
						if(Tk >= min && Tk <= max && !cancel){
							initialVector.insertElementAt( 
									new Image(
											t.indFirstStrip,
											t.isFirstStripDirect,
											Tk,
											theStripOfT.getDuj(),
											theStripOfT.getRj(),
											theStripOfT.getSuj()
									), 0);
							
							System.out.println("insertion de "+t.indFirstStrip+" dans "+initialVector);
							
							//suppression de la bande utilisee de la liste des non utilisees
							int j = -1;
							for (Image anImage : theStillNotUsedStrips)
								if (anImage.indStrip == t.indFirstStrip
										&& anImage.isStripDirect == t.isFirstStripDirect){
									j = theStillNotUsedStrips.indexOf(anImage);
									break;
							}//if
							if (j >= 0) theStillNotUsedStrips.removeElementAt(j);
							
							Map<Transition, Strip> tmpTransitionsForInsertionBefore = extractingTransitionsToRemove(theTransitionsForInsertionBeforeCurrentImage, t, theStripOfT);
							Map<Transition, Strip> tmpTransitionsForInsertionAfter = extractingTransitionsToRemove(theTransitionsForInsertionAfterCurrentImage, t, theStripOfT);
							
							//ensuite redimmensionnement des bandes
							resizingTool rs = reScalingStrips(tmpTransitionsForInsertionBefore, initialVector, 0, (max - Tk), true);
							tmpTransitionsForInsertionBefore = rs.theMap;
							rs = reScalingStrips(tmpTransitionsForInsertionAfter, initialVector, 0, (max - Tk), true);
							
							return fillSolutionsToCompare(rs.theVector, indexInListOfSolutions, tmpTransitionsForInsertionBefore, rs.theMap, false);
							
						}//if contrainte OK
					}//si une transition est trouvee
				}//for transitions
			}//if tete
					if (i == (initialVector.size() - 1)){
						//insertion en queue
						Image currentImage = initialVector.elementAt(i);
						for (Transition t : theTransitionsForInsertionAfterCurrentImage.keySet()) {
					//en remplacant la boucle ci-dessus par celle ci-dessus on inverse le sens de traitement des bandes
							//	List<Transition> tab = Util.shuffle(theTransitionsForInsertionAfterCurrentImage.keySet());
							//	for (Transition t : tab) {
							if (t.indFirstStrip == currentImage.indStrip 
									&& t.isFirstStripDirect == currentImage.isStripDirect){
								boolean cancel = false;
								//pour pas remettre une prise de vue qui y est deja
								for(Image img : initialVector) 
									if(img.indStrip == t.indSecondStrip){
										cancel = true;
										break;
									}//if
								Strip theStripOfT = theTransitionsForInsertionAfterCurrentImage.get(t);
								int min = 0, max = 0;
								if (t.isSecondStripDirect){
									min = theStripOfT.getTMin1();
									max = theStripOfT.getTMax1();
								} else {
									min = theStripOfT.getTMin0();
									max = theStripOfT.getTMax0();
								}//else
								int Tk = currentImage.indImageTk + currentImage.indImageDuj + t.indMilliseconds;
								//si contrainte verifiee
								if(Tk >= min && Tk <= max && !cancel){
									initialVector.insertElementAt( 
												new Image(
														t.indSecondStrip,
														t.isSecondStripDirect,
														Tk,
														theStripOfT.getDuj(),
														theStripOfT.getRj(),
														theStripOfT.getSuj()
												), initialVector.size());

									System.out.println("insertion de "+t.indSecondStrip+" dans "+initialVector);
									
										//suppression de la bande utilisee de la liste des non utilisees
										int j = -1;
										for (Image anImage : theStillNotUsedStrips)
											if (anImage.indStrip == t.indSecondStrip
													&& anImage.isStripDirect == t.isSecondStripDirect){
												j = theStillNotUsedStrips.indexOf(anImage);
												break;
										}
										if (j >= 0) theStillNotUsedStrips.removeElementAt(j);
										
										//a tester = est-ce moins long de parcourir toutes les transitions chaque fois plutot que d'effectuer
										//des suppressions au fur et a mesure
									//	Map<Transition, Strip> tmpTransitionsForInsertionBefore = theTransitionsForInsertionBeforeCurrentImage;
									//	Map<Transition, Strip> tmpTransitionsForInsertionAfter = theTransitionsForInsertionAfterCurrentImage;
										Map<Transition, Strip> tmpTransitionsForInsertionBefore = extractingTransitionsToRemove(theTransitionsForInsertionBeforeCurrentImage, t, theStripOfT);
										Map<Transition, Strip> tmpTransitionsForInsertionAfter = extractingTransitionsToRemove(theTransitionsForInsertionAfterCurrentImage, t, theStripOfT);
										
										//ensuite redimmensionnement des bandes
										resizingTool rs = reScalingStrips(tmpTransitionsForInsertionBefore, initialVector, (i + 1), (max - Tk), true);
										tmpTransitionsForInsertionBefore = rs.theMap;
										rs = reScalingStrips(tmpTransitionsForInsertionAfter, initialVector, (i + 1), (max - Tk), true);
										
										return fillSolutionsToCompare(rs.theVector, indexInListOfSolutions, tmpTransitionsForInsertionBefore, rs.theMap, false);
									
								}//if contrainte OK
							}//if
						}//boucle sur toutes les transitions

					} else {
						
						if (i > 0){
						//insertion entre deux
							Image leftImage = initialVector.elementAt(i-1);
							Image rightImage = initialVector.elementAt(i);
							for (Transition t1 : theTransitionsForInsertionBeforeCurrentImage.keySet()) {
							for (Transition t2 : theTransitionsForInsertionAfterCurrentImage.keySet()) {
								if (t1.indFirstStrip == leftImage.indStrip 
										&& t2.indSecondStrip == rightImage.indStrip
										&& t1.indFirstStrip == t2.indSecondStrip
										&& t1.isFirstStripDirect == leftImage.isStripDirect
										&& t2.isSecondStripDirect == rightImage.isStripDirect){
									boolean cancel = false;
									//pour ne pas remettre une prise de vue qui y est deja
									for(Image img : initialVector)
										if(img.indStrip == t1.indSecondStrip){
											cancel = true;
											break;
										}//if
									Strip theStripOfT = theTransitionsForInsertionBeforeCurrentImage.get(t2);
									//System.out.println("insertion au milieu  "+t1 + " "+t2 +" "+theStripOfT);
									int min = 0, max = 0;
									if (t1.isSecondStripDirect){
										min = theStripOfT.getTMin1();
										max = theStripOfT.getTMax1();
									} else {
										min = theStripOfT.getTMin0();
										max = theStripOfT.getTMax0();
									}//else
									int Tk1 = leftImage.indImageTk + leftImage.indImageDuj + t1.indMilliseconds;
									int Tk2 = rightImage.indImageTk - (t2.indMilliseconds + theStripOfT.getDuj());
									if(!cancel 
											&& (Tk1 >= min && Tk1 <= max) 
											&& (Tk2 >= min && Tk2 <= max) 
											&& (Tk1 < Tk2) ){
										initialVector.insertElementAt( 
												new Image(
														t1.indSecondStrip,
														t1.isSecondStripDirect,
														Tk1,
														theStripOfT.getDuj(),
														theStripOfT.getRj(),
														theStripOfT.getSuj()
											), i);

										System.out.println("insertion de "+t1.indSecondStrip+" dans "+initialVector);
										
										//retrait de l'image utilisee de la liste des non utilisees
										int j = -1;
										for (Image anImage : theStillNotUsedStrips)
											if (anImage.indStrip == t1.indSecondStrip
													&& anImage.isStripDirect == t1.isSecondStripDirect){
												j = theStillNotUsedStrips.indexOf(anImage);
												break;
										}
										if (j >= 0) theStillNotUsedStrips.removeElementAt(j);
										
										Map<Transition, Strip> tmpTransitionsForInsertionBefore = extractingTransitionsToRemove(theTransitionsForInsertionBeforeCurrentImage, t1, theStripOfT);
										Map<Transition, Strip> tmpTransitionsForInsertionAfter = extractingTransitionsToRemove(theTransitionsForInsertionAfterCurrentImage, t1, theStripOfT);
										tmpTransitionsForInsertionBefore = extractingTransitionsToRemove(theTransitionsForInsertionBeforeCurrentImage, t2, theStripOfT);
										tmpTransitionsForInsertionAfter = extractingTransitionsToRemove(theTransitionsForInsertionAfterCurrentImage, t2, theStripOfT);
										
										//ensuite redimmensionnement des bandes
										resizingTool rs = reScalingStrips(tmpTransitionsForInsertionBefore, initialVector, (i + 1), (Tk2 - Tk1), true);
										tmpTransitionsForInsertionBefore = rs.theMap;
										rs = reScalingStrips(tmpTransitionsForInsertionAfter, initialVector, (i + 1), (Tk2 - Tk1), true);
										tmpTransitionsForInsertionAfter = rs.theMap;
										//le vecteur doit etre redimmensionne une seule fois.
										initialVector = rs.theVector;
										
										return fillSolutionsToCompare(initialVector, indexInListOfSolutions, tmpTransitionsForInsertionBefore, tmpTransitionsForInsertionAfter, false);
											
									}//if insertion possible
								}//if transition is correct for images
							}//for right transitions
							}//for left transitions
							
						}//if between two images
				}//else
			}//for
		//une fois le vecteur complet on l'ajoute a la liste des solutions
		theListOfSolutionsToCompare.setSize(indexInListOfSolutions + 1);
		theListOfSolutionsToCompare.set(indexInListOfSolutions, initialVector);
		
		if (!onlyTryInsertion){
			//tant qu'il reste des trucs pas essayes 
			//on reprend depuis le debut pour generer de nouvelles listes	
			if (neighborhoodModeOn){
				if (!theStillNotUsedStrips.isEmpty()){
						neighborGeneration(indexInListOfSolutions);
				}//if
			} else {
				if (!theStillNotUsedStrips.isEmpty()){
							indexInListOfSolutions++;
							createNewVector(indexInListOfSolutions);
				}//if still unused strips
			}//else
		}//if
		return true;
		
	}//fillSolutionsToCompare
	
	
	/**
	 * fonction recursive calculant les gains d'un vecteur
	 * @param theVectorWhereStillGainsAreToCompute
	 * @param gain est le gain deja calcule des premiers elements, a ajouter au gain des elements suivants
	 * @return le gain
	 */
	protected int getGain (Vector<Image> theVectorWhereStillGainsAreToCompute, int gain){
		//si il reste des elements dans le vecteur pas encore traites
		if (!theVectorWhereStillGainsAreToCompute.isEmpty()){
			int Rj = theVectorWhereStillGainsAreToCompute.elementAt(0).indImageRj;
			Vector<Image> nextImagesToRemove = new Vector<Image>();
			nextImagesToRemove.setSize(0);
			int Si = 0; int Gi = 0;
			for (Request r : instanceRequests) 
				if (r.getInd() == Rj) {
					Si = r.getSi();
					Gi = r.getGi();
					break;
				}//if
			int Suj = 0;
			for (Image img : theVectorWhereStillGainsAreToCompute){
				if (img.indImageRj == Rj){
					Suj += img.indImageSuj;
					nextImagesToRemove.insertElementAt(img, 0);
				}//if
			}//for
			//proportion de surface photographiee
			int Fri = (100*Suj) / Si;
			int PFri = 100;
			//pour le dernier morceau : P(x) = 2x - 100 
			if (Fri <= 100 && Fri >= 70) PFri = 2*Fri - 100;
			else {
				//P(x) = x - 100
				if (Fri < 70 && Fri >= 40) PFri = Fri - 30;
				//P(x) = 0,25 x
				else PFri = Fri / 4;
			}//else
			gain += Gi * PFri;
			theVectorWhereStillGainsAreToCompute.removeAll(nextImagesToRemove);
			return getGain(theVectorWhereStillGainsAreToCompute, gain);
		}//condition de recursion
		//condition d'arret
		else return (gain / 100);
	}//getGain
	
	/**
	 * pour enlever les prises de vue stereo orphelines, ou les prises de vue
	 * stereo dont les sens ne sont pas les memes.
	 * parcours de tous les vecteurs solutions
	 */
	public void removeTwinWhenWrongDirectionOrUnique (){
		System.out.println("retrait de vues stereo orphelines: ");
		for (int v=0; v<theListOfSolutionsToCompare.size(); v++){
			Set<Image> imagesToRemove = new HashSet();
			for (int i=0; i<theListOfSolutionsToCompare.elementAt(v).size(); i++){
				for (Strip s : instanceStrips)
					if (theListOfSolutionsToCompare.elementAt(v).elementAt(i).indStrip == s.getInd() && s.getTwj() != 0){
							boolean aTwinIsFoundButUnique = true;
							for (int j=i; j<theListOfSolutionsToCompare.elementAt(v).size(); j++){
								if (theListOfSolutionsToCompare.elementAt(v).elementAt(j).indStrip == s.getTwj()){
									if (theListOfSolutionsToCompare.elementAt(v).elementAt(j).isStripDirect == theListOfSolutionsToCompare.elementAt(v).elementAt(i).isStripDirect) 
										aTwinIsFoundButUnique = false; 
								}//if
							}//for
							if (aTwinIsFoundButUnique) imagesToRemove.add(theListOfSolutionsToCompare.elementAt(v).elementAt(i));
						}//if
		}//for
			theListOfSolutionsToCompare.elementAt(v).remove(imagesToRemove);
			System.out.println("1 retrait");
		}//for
	}//removeTwinWhenWrongDirectionOrUnique
	
	/**
	 * comparaison des gains
	 * @return le vecteur final dont le gain est optimal
	 */
	public Vector<Image> findBestSolution(){
		System.out.println("Finding out best solution...");
		if (!theListOfSolutionsToCompare.isEmpty()){
			Vector<Image> finalVector = theListOfSolutionsToCompare.elementAt(0);
			Vector<Image> finalVectorForPresentation = new Vector<Image>();
			//on est oblige de recopier le vecteur car la fonction getgain l'efface
			for (Image img : finalVector){
				finalVectorForPresentation.insertElementAt(new Image(
						img.indStrip,
						img.isStripDirect
				), finalVectorForPresentation.size());
			}//for
			System.out.println("---Un parcours possible--- : 0 "+theListOfSolutionsToCompare.firstElement());
			int finalGain = getGain(finalVector, 0);
			System.out.println("gain: 0  "+finalGain);
			int finalSolution = 0;
			for (int i = 1; i<theListOfSolutionsToCompare.size(); i++){
				Vector<Image> curVector = theListOfSolutionsToCompare.elementAt(i);
				Vector<Image> currentVectorForPresentation = new Vector<Image>();
				for (Image img : curVector){
					currentVectorForPresentation.insertElementAt(new Image(
							img.indStrip,
							img.isStripDirect
					), currentVectorForPresentation.size());
				}//for
				System.out.println("---Un parcours possible--- : "+i+" "+theListOfSolutionsToCompare.elementAt(i));
				int curGain = getGain(curVector, 0);
				System.out.println("gain: "+i+"  "+curGain);
				if ((finalGain < curGain) || (finalGain == curGain && curVector.size() > finalVector.size())){
					finalGain = curGain;
					finalSolution = i;
					finalVectorForPresentation = currentVectorForPresentation;
				}//si gain meilleur
			}//for 
			System.out.println("meilleure solution: "+finalSolution);
			return finalVectorForPresentation;
		}//if no isempty
		else {
			System.out.println("fichier vide ou tros gros");
			return null;
		}//else
	}//findBestSolution
	

	/**
	 * Constructeur de la representation des solutions dont la solution finale
	 * @param anInstance
	 * @param neighborhoodModeOn
	 */
	public Solution(Instance anInstance, boolean neighborhoodModeOn){
		//recuperation des donnees de l'instance
		this.neighborhoodModeOn = neighborhoodModeOn;
		System.out.println("Computing solution...");
		instanceTransitions = anInstance.computeDistances();
		instanceStrips = anInstance.getTabStrips();
		instanceRequests = anInstance.getSetRequests();
		indNbStrips = anInstance.getNbBd();
		middleStripsDates = anInstance.indMiddleTMin;
		//mesure de securite sur le temps d'execution :
		//un thread fils effectue les calculs pendant que l'autre compte le temps imparti 
		Thread thread1 = new Thread(new Runnable() {
			@Override
			public void run() {
				long time = System.currentTimeMillis();
				init();	
				time = (System.currentTimeMillis() - time) / 1000;
				System.out.println("TEMPS D'EXECUTION : "+(time/60) + "m"+(time%60)+"s.");	
				disposedTooEarly = false;
				removeTwinWhenWrongDirectionOrUnique();
				//displayListOfSolutions();
				finalVector = findBestSolution();
			}
		});
		thread1.setPriority(Thread.MAX_PRIORITY);
		thread1.start();
		try {
			int time = 0;
			while (disposedTooEarly && time < 360000){
				//6 minutes :
				Thread.sleep(1000);
				time += 1000;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread1.stop();
		if (disposedTooEarly){
			System.out.println("EXECUTION INTERROMPUE");
			removeTwinWhenWrongDirectionOrUnique();
			//displayListOfSolutions();
			finalVector = findBestSolution();
		}//if
	}//constructor
	
}//class
