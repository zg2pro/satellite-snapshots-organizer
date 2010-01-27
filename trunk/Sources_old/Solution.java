import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

class resizingTool {
	Vector<Image> theVector;
	Map<Transition, Strip> theMap;
	
	public resizingTool(Vector<Image> theVector, Map<Transition, Strip> theMap) {
		this.theVector = theVector;
		this.theMap = theMap;
	}//constructor
}//class resizingTool

public class Solution {

	private boolean disposeTooEarly = true;
	public Vector<Image> finalLastSolution;
	Vector<Image> theStillNotUsedStrips;
	Vector<Vector<Image>> theListOfSolutionsToCompare;
	Transition[] allTransitions;
	Strip[] allStrips;
	Set<Request> theReqs;
	int indNbStrips;
	Map<Transition, Strip> TransitionsSortedByFirst;
	Map<Transition, Strip> TransitionsSortedBySecond;
	

	public void displayListOfSolutions(){
		for (int i=0; i<theListOfSolutionsToCompare.size(); i++){
			System.out.println("-------A list------- : "+i+" "+theListOfSolutionsToCompare.elementAt(i));
		}
	}//displayListOfSolutions
	
	public void init(){
		System.out.println("Initiating research tools...");
		
		Arrays.sort(allTransitions,  new Comparator() { 
            public int compare(Object o1, Object o2) {
            	return (((Transition)o1).indFirstStrip - ((Transition)o2).indFirstStrip);
            }
        });
		Arrays.sort(allStrips);
		TransitionsSortedByFirst = new TreeMap<Transition, Strip>();
		for(int i = 0; i<allTransitions.length ; i++){
			int zz = i / ((indNbStrips - 1) * 4);
			TransitionsSortedByFirst.put(allTransitions[i], allStrips[zz]);
		}//for
		
		Arrays.sort(allTransitions,  new Comparator() { 
            public int compare(Object o1, Object o2) {
            	return (((Transition)o1).indSecondStrip - ((Transition)o2).indSecondStrip);
            }
        });
		TransitionsSortedBySecond = new TreeMap<Transition, Strip>();
		for(int i = 0; i<allTransitions.length ; i++){
			int zz = i / ((indNbStrips - 1) * 4);
			TransitionsSortedBySecond.put(allTransitions[i], allStrips[zz]);
		}//for
		
		Strip initialStrip = allStrips[0];
		//int beginTime = initialStrip.getTMin0();
		Image initialImage = new Image(
				initialStrip.getInd(), 
				false, 
				initialStrip.getTMin0(), 
				initialStrip.getDuj(), 
				initialStrip.getRj(),
				initialStrip.getSuj()/*, 
				initialStrip.getRelativeGainOnRequest()
		*/	);
		Vector<Image> initialVector = new Vector<Image>();
		initialVector.setSize(1);
		initialVector.set(0, initialImage);
		theStillNotUsedStrips = new Vector<Image>();
		theStillNotUsedStrips.add(new Image(initialStrip.getInd(), true));
		for (int i = 1; i<allStrips.length; i++){
			theStillNotUsedStrips.add(new Image(allStrips[i].getInd(), false));
			theStillNotUsedStrips.add(new Image(allStrips[i].getInd(), true));
		}//for
		theListOfSolutionsToCompare = new Vector<Vector<Image>>();
	
		//redimmensionner dans TransitionsSortedByFirst, TransitionsSortedBySecond
		Map <Transition, Strip> tmpTransitionsForInsertionBefore = new TreeMap<Transition, Strip>();
		Map <Transition, Strip> tmpTransitionsForInsertionAfter = new TreeMap<Transition, Strip>();
		tmpTransitionsForInsertionBefore.putAll(TransitionsSortedByFirst);
		tmpTransitionsForInsertionAfter.putAll(TransitionsSortedBySecond);
		/*
		if (initialStrip.getTwj() != 0){
			for (Transition transitionsRemaining : TransitionsSortedByFirst.keySet())
				if ( ((initialStrip.getTwj() == transitionsRemaining.indFirstStrip) 
						&& (transitionsRemaining.isFirstStripDirect != false) )
						|| ((initialStrip.getTwj() == transitionsRemaining.indSecondStrip) 
								&& (transitionsRemaining.isSecondStripDirect != false))){
					tmpTransitionsForInsertionBefore.remove(transitionsRemaining);
				}//if
		}//si il y a une jumelle, on degage celles qui sont dans le mauvais sens
		
		if (initialStrip.getTwj() != 0){
			for (Transition transitionsRemaining : TransitionsSortedBySecond.keySet())
				if ( ((initialStrip.getTwj() == transitionsRemaining.indFirstStrip) 
						&& (transitionsRemaining.isFirstStripDirect != false) )
						|| ((initialStrip.getTwj() == transitionsRemaining.indSecondStrip) 
								&& (transitionsRemaining.isSecondStripDirect != false))){
					tmpTransitionsForInsertionAfter.remove(transitionsRemaining);
				}//if
		}//si il y a une jumelle, on degage celles qui sont dans le mauvais sens
	*/
		
		// On pourra rien inserer avant cet element puisqu'on le cale
		//tout a gauche.
		Set<Transition> transitionsToRemove = new HashSet<Transition>();
		for (Transition t : tmpTransitionsForInsertionBefore.keySet()){
			if (t.indSecondStrip == initialStrip.getInd())
				transitionsToRemove.add(t);
		}
		for (Transition t : transitionsToRemove){
			tmpTransitionsForInsertionBefore.remove(t);
		}
		
		//on reinserera pas cet element puisqu'il y est deja :
		transitionsToRemove = new HashSet<Transition>();
		for (Transition t : tmpTransitionsForInsertionAfter.keySet()){
			if (t.indSecondStrip == initialStrip.getInd())
				transitionsToRemove.add(t);
		}
		for (Transition t : transitionsToRemove){
			tmpTransitionsForInsertionAfter.remove(t);
		}
	
		//le redimmensionnement
		for (Transition t : tmpTransitionsForInsertionAfter.keySet()){
			if (tmpTransitionsForInsertionAfter.get(t).getInd() == initialStrip.getInd()
					&& !t.isFirstStripDirect)
				tmpTransitionsForInsertionAfter.get(t).TMax0 = initialStrip.getTMin0();
		}
			
		System.out.println("initial still not used strips: ");
		for (Image img : theStillNotUsedStrips)
			System.out.println("||"+img+"||");
		System.out.println("Searching solution...");
		
		fillSolutionsToCompare(initialVector, 0,
				tmpTransitionsForInsertionBefore, tmpTransitionsForInsertionAfter);
	
		
		
	}//init
	
	private Map<Transition, Strip> extractingTransitionsToRemove(
			Map<Transition, Strip> theTransitionsForInsertionNearCurrentImage, 
			Transition t, Strip s){
		
		boolean stripIsFirst = false;
		if (s.getInd() == t.indFirstStrip) stripIsFirst = true;
		
		Set<Transition> tmpCopyKeySet = new HashSet();
	
		for (Transition transitionsRemaining : theTransitionsForInsertionNearCurrentImage.keySet()){
			if (!(t.indFirstStrip == transitionsRemaining.indFirstStrip
					&& transitionsRemaining.indSecondStrip == t.indSecondStrip))
				tmpCopyKeySet.add(transitionsRemaining);
		/*	if (s.getTwj() != 0){
				if (stripIsFirst){
					if (!( (s.getTwj() == transitionsRemaining.indFirstStrip) 
							&& (transitionsRemaining.isFirstStripDirect != t.isFirstStripDirect) ))
						tmpCopyKeySet.add(transitionsRemaining);
				} else {
					if (!( (s.getTwj() == transitionsRemaining.indSecondStrip) 
							&& (transitionsRemaining.isSecondStripDirect != t.isSecondStripDirect) ))
						tmpCopyKeySet.add(transitionsRemaining);//theTransitionsForInsertionNearCurrentImage.remove(transitionsRemaining);
				}//else
			}//si il y a une jumelle, on degage celles qui sont dans le mauvais sens
	*/	}//for each map elem
		
		Map<Transition, Strip> toReturn = new TreeMap<Transition, Strip>();
		for (Transition aTransition : tmpCopyKeySet)
			toReturn.put(aTransition, theTransitionsForInsertionNearCurrentImage.get(aTransition));
		return toReturn;
	}//extractingRemovable
	

	private resizingTool reScalingStrips (
			Map<Transition, Strip> theTransitionsForInsertionNearCurrentImage, 
			Vector<Image> theVector, 
			int iterationInit,
			int delayToRescale, 
			boolean firstRecursion){
			
			//condition de fin:
			if (iterationInit == theVector.size()){
				resizingTool toReturn = new resizingTool(theVector, theTransitionsForInsertionNearCurrentImage);
				return  toReturn;
			}
			
			//la recursion:
			Image currentImage = theVector.elementAt(iterationInit);
			for (Transition t : theTransitionsForInsertionNearCurrentImage.keySet()){
				if (currentImage.indStrip == theTransitionsForInsertionNearCurrentImage.get(t).getInd()){
					Strip theStripToRescale = theTransitionsForInsertionNearCurrentImage.get(t); 
					if (currentImage.isStripDirect){
						//on utilise les 1
						if ((theStripToRescale.TMax1 - delayToRescale) < theStripToRescale.TMin1){
							delayToRescale = theStripToRescale.TMax1 - theStripToRescale.TMin1;
							theTransitionsForInsertionNearCurrentImage.get(t).TMax1 = theStripToRescale.TMin1;
							if (!firstRecursion) theVector.elementAt(iterationInit).indDate = currentImage.indDate - (theStripToRescale.TMax1 - theStripToRescale.TMin1);
						} else {
							theTransitionsForInsertionNearCurrentImage.get(t).TMax1 = theStripToRescale.TMax1 - delayToRescale;
							if (!firstRecursion) theVector.elementAt(iterationInit).indDate = currentImage.indDate - delayToRescale;
						}//else
					} else {
						//on utilise les 0	
						if ((theStripToRescale.TMax0 - delayToRescale) < theStripToRescale.TMin0){
							delayToRescale = theStripToRescale.TMax0 - theStripToRescale.TMin0;
							theTransitionsForInsertionNearCurrentImage.get(t).TMax0 = theStripToRescale.TMin0;
							if (!firstRecursion) theVector.elementAt(iterationInit).indDate = currentImage.indDate - (theStripToRescale.TMax0 - theStripToRescale.TMin0);
						} else {
							theTransitionsForInsertionNearCurrentImage.get(t).TMax0 = theStripToRescale.TMax0 - delayToRescale;
							if (!firstRecursion) theVector.elementAt(iterationInit).indDate = currentImage.indDate - delayToRescale;
						}//else
					}//else
				}//if strip = image 
				//(les strips de transition dans le mauvais sens ne doivent plus y etre)
			}//for each transition		toReturn = 
			resizingTool toReturn = reScalingStrips(
					theTransitionsForInsertionNearCurrentImage, 
					theVector, 
					iterationInit + 1, 
					delayToRescale,
					false
				);
			return toReturn;
	}//RescalingStrips
	
	
	public boolean fillSolutionsToCompare (Vector<Image> initialVector, int indexInListOfSolutions,
			Map<Transition, Strip> theTransitionsForInsertionBeforeCurrentImage, 
			Map<Transition, Strip> theTransitionsForInsertionAfterCurrentImage){
		
		for (int i = 0; i < initialVector.size(); i++){
				//Pas d'insertion en tete possible puisque on bouche le tmin tmax
				//d'entree en calant le premier tout a gauche
				if (i == (initialVector.size() - 1)){
						//insertion en queue
						Image currentImage = initialVector.elementAt(i);
						for (Transition t : theTransitionsForInsertionAfterCurrentImage.keySet()) {
					//	List<Transition> tab = Util.shuffle(theTransitionsForInsertionAfterCurrentImage.keySet());
					//	for (Transition t : tab) {
							if (t.indFirstStrip == currentImage.indStrip 
									&& t.isFirstStripDirect == currentImage.isStripDirect){
								boolean cancel = false;
								for(Image img : initialVector) if(img.indStrip == t.indSecondStrip) cancel = true;
								Strip theStripOfT = theTransitionsForInsertionAfterCurrentImage.get(t);
								int min = 0, max = 0;
								if (t.isSecondStripDirect){
									min = theStripOfT.getTMin1();
									max = theStripOfT.getTMax1();
								} else {
									min = theStripOfT.getTMin0();
									max = theStripOfT.getTMax0();
								}//else
								int Tk = currentImage.indDate + currentImage.indLasting + t.indMilliseconds;
								//System.out.println("contrainte: "+min+" < "+Tk+" < "+max);
								if(Tk >= min && Tk <= max && !cancel){
									initialVector.insertElementAt( 
												new Image(
														t.indSecondStrip,
														t.isSecondStripDirect,
														Tk,
														theStripOfT.getDuj(),
														theStripOfT.getRj(),
														theStripOfT.getSuj()/*,
														theStripOfT.getRelativeGainOnRequest()*/
												), initialVector.size());
									
										//suppression de la bande utilisee de la liste des non utilisees
										int j = -1;
										for (Image anImage : theStillNotUsedStrips)
											if (anImage.indStrip == t.indSecondStrip
													&& anImage.isStripDirect == t.isSecondStripDirect){
												j = theStillNotUsedStrips.indexOf(anImage);
												break;
										}
										if (j >= 0) theStillNotUsedStrips.removeElementAt(j);
										
									//	Map<Transition, Strip> tmpTransitionsForInsertionBefore = theTransitionsForInsertionBeforeCurrentImage;
									//	Map<Transition, Strip> tmpTransitionsForInsertionAfter = theTransitionsForInsertionAfterCurrentImage;
										Map<Transition, Strip> tmpTransitionsForInsertionBefore = extractingTransitionsToRemove(theTransitionsForInsertionBeforeCurrentImage, t, theStripOfT);
										Map<Transition, Strip> tmpTransitionsForInsertionAfter = extractingTransitionsToRemove(theTransitionsForInsertionAfterCurrentImage, t, theStripOfT);
										
										//ensuite redimmensionnement des bandes
										resizingTool rs = reScalingStrips(tmpTransitionsForInsertionBefore, initialVector, (i + 1), (max - Tk), true);
										tmpTransitionsForInsertionBefore = rs.theMap;
										rs = reScalingStrips(tmpTransitionsForInsertionAfter, initialVector, (i + 1), (max - Tk), true);
										tmpTransitionsForInsertionAfter = rs.theMap;
										//le vecteur doit etre redimmensionne une seule fois.
										initialVector = rs.theVector;
										
										return fillSolutionsToCompare(initialVector, indexInListOfSolutions, tmpTransitionsForInsertionBefore, tmpTransitionsForInsertionAfter);
									
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
										&& t1.indSecondStrip == t2.indFirstStrip){
									boolean cancel = false;
									for(Image img : initialVector) 
										if(img.indStrip == t1.indSecondStrip) 
											cancel = true;
									Strip theStripOfT = theTransitionsForInsertionAfterCurrentImage.get(t1);
									int min = 0, max = 0;
									if (t1.isSecondStripDirect){
										min = theStripOfT.getTMin1();
										max = theStripOfT.getTMax1();
									} else {
										min = theStripOfT.getTMin0();
										max = theStripOfT.getTMax0();
									}//else
									int Tk1 = leftImage.indDate + leftImage.indLasting + t1.indMilliseconds;
									int Tk2 = rightImage.indDate - (t2.indMilliseconds + theStripOfT.getDuj());
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
														theStripOfT.getSuj()/*
														theStripOfT.getRelativeGainOnRequest()
												*/), i);
										
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
										
										return fillSolutionsToCompare(initialVector, indexInListOfSolutions, tmpTransitionsForInsertionBefore, tmpTransitionsForInsertionAfter);
											
									}//if insertion possible
								}//if transition is correct for images
							}//for right transitions
							}//for left transitions
							
						}//if between two images
				}//else
			}//for
		
		theListOfSolutionsToCompare.setSize(indexInListOfSolutions + 1);
		theListOfSolutionsToCompare.set(indexInListOfSolutions, initialVector);
		System.out.println("list of solutions: (turn "+indexInListOfSolutions+" )");
		displayListOfSolutions();
		indexInListOfSolutions++;
		
			//tant qu'il reste des trucs pas essayes 
			//on reprend depuis le debut pour generer de nouvelles listes
			if (!theStillNotUsedStrips.isEmpty()){
				
				Strip theNewInitialStrip = allStrips[0];
				for (Strip s : allStrips){
					if (s.getInd() == theStillNotUsedStrips.get(0).indStrip)
						theNewInitialStrip = s;
				}
				Vector<Image> theNewInitialVector = new Vector<Image>();
				theNewInitialVector.setSize(1);
				int date = 0;
				if (theStillNotUsedStrips.get(0).isStripDirect){
					date  = theNewInitialStrip.getTMin1();
				} else {
					date  = theNewInitialStrip.getTMin0();
				}
				Image newInitialImage = new Image(theNewInitialStrip.getInd(), 
						theStillNotUsedStrips.get(0).isStripDirect, 
						date, 
						theNewInitialStrip.getDuj(),
						theNewInitialStrip.getRj(),
						theNewInitialStrip.getSuj()
						/*
						theNewInitialStrip.getRelativeGainOnRequest()
				*/	);
				theNewInitialVector.set(0, newInitialImage);
				//redimmensionner dans TransitionsSortedByFirst, TransitionsSortedBySecond
				Map <Transition, Strip> tmpTransitionsForInsertionBefore = new TreeMap<Transition, Strip>();
				Map <Transition, Strip> tmpTransitionsForInsertionAfter = new TreeMap<Transition, Strip>();
				tmpTransitionsForInsertionBefore.putAll(TransitionsSortedByFirst);
				tmpTransitionsForInsertionAfter.putAll(TransitionsSortedBySecond);

				for (Transition t : tmpTransitionsForInsertionAfter.keySet()){
					if (tmpTransitionsForInsertionAfter.get(t).getInd() == theNewInitialStrip.getInd())
						if (theStillNotUsedStrips.get(0).isStripDirect)
							tmpTransitionsForInsertionAfter.get(t).TMax1 = date;
						else tmpTransitionsForInsertionAfter.get(t).TMax0 = date;
				}
				
				// On pourra rien inserer avant cet element puisqu'on le cale
				//tout a gauche.
				Set<Transition> transitionsToRemove = new HashSet<Transition>();
				for (Transition t : tmpTransitionsForInsertionBefore.keySet()){
					if (t.indSecondStrip == theNewInitialStrip.getInd())
						transitionsToRemove.add(t);
				}
				for (Transition t : transitionsToRemove)
					tmpTransitionsForInsertionBefore.remove(t);
				// On reinsere pas l'element a droite non plus
				transitionsToRemove = new HashSet<Transition>();
				for (Transition t : tmpTransitionsForInsertionAfter.keySet()){
					if (t.indSecondStrip == theNewInitialStrip.getInd())
						transitionsToRemove.add(t);
				}
				for (Transition t : transitionsToRemove)
					tmpTransitionsForInsertionAfter.remove(t);

				System.out.println("still not used strips: ");
				for (Image img : theStillNotUsedStrips)
					System.out.println("||"+img+"||");
				
				theStillNotUsedStrips.remove(0);
				
				System.out.println("Searching another solution...: "+indexInListOfSolutions);
				return fillSolutionsToCompare(theNewInitialVector, indexInListOfSolutions,
						tmpTransitionsForInsertionBefore, tmpTransitionsForInsertionAfter);
			}//if still unused strips
		
		return true;
		
	}//fillSolutionsToCompare
	
	
	/**
	 * Completely useless since no gain are negative
	 */
	/*
	public void extendsListToAllPartsOfList(){
	}//extendsListToAllPartsOfList
	*/
	
	private int getGain (Vector<Image> theVectorWhereStillGainsAreToCompute, int gain){
		if (!theVectorWhereStillGainsAreToCompute.isEmpty()){
			int Rj = theVectorWhereStillGainsAreToCompute.elementAt(0).Rj;
			Vector<Image> nextImagesToRemove = new Vector<Image>();
			nextImagesToRemove.setSize(0);
			int Si = 0; int Gi = 0;
			for (Request r : theReqs) 
				if (r.getInd() == Rj) {
					Si = r.getSi();
					Gi = r.getGi();
					break;
				}
			int Suj = 0;
			for (Image img : theVectorWhereStillGainsAreToCompute){
				if (img.Rj == Rj){
					Suj += img.Suj;
					nextImagesToRemove.insertElementAt(img, 0);
				}
			}
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
		}
		else return (gain / 100);
	}
	
	public Vector<Image> findBestSolution(){
		System.out.println("Finding out best solution...");
		if (!theListOfSolutionsToCompare.isEmpty()){
			Vector<Image> finalVector = theListOfSolutionsToCompare.elementAt(0);
			Vector<Image> finalVectorForPresentation = new Vector<Image>();
			for (Image img : finalVector){
				finalVectorForPresentation.insertElementAt(new Image(
						img.indStrip,
						img.isStripDirect
				), finalVectorForPresentation.size());
			}
			int finalGain = getGain(finalVector, 0);
			System.out.println("gain: 0  "+finalGain);
			int finalSolution = 0;
			for (int i = 1; i<theListOfSolutionsToCompare.size(); i++){
				//System.out.println("cur="+i);
				Vector<Image> curVector = theListOfSolutionsToCompare.elementAt(i);
				Vector<Image> currentVectorForPresentation = new Vector<Image>();
				for (Image img : curVector){
					currentVectorForPresentation.insertElementAt(new Image(
							img.indStrip,
							img.isStripDirect
					), currentVectorForPresentation.size());
				}
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
		}
	}//findBestSolution
	

	public Solution(Instance anInstance){
		System.out.println("Computing solution...");
		allTransitions = anInstance.computeDistances();
		allStrips = anInstance.getStrips();
		theReqs = anInstance.getReqs();
		indNbStrips = anInstance.getNbBd();
		Thread thread1 = new Thread(new Runnable() {
			@Override
			public void run() {
				long time = System.currentTimeMillis();
				init();	
				time = (System.currentTimeMillis() - time) / 1000;
				System.out.println("TEMPS D'EXECUTION : "+(time/60) + "m"+(time%60)+"s.");	
				disposeTooEarly = false;
				finalLastSolution = findBestSolution();
			//	Util.writeSolutionInFile(finalLastSolution);
			//	System.exit(0);
			}
		});
		thread1.setPriority(Thread.MAX_PRIORITY);
		thread1.start();
		try {
			int time = 0;
			while (disposeTooEarly && time < 360000){
				//6 minutes :
				Thread.sleep(1000);
				time += 1000;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread1.stop();
		if (disposeTooEarly){
			System.out.println("EXECUTION INTERROMPUE");
			displayListOfSolutions();
			finalLastSolution = findBestSolution();
		//	Util.writeSolutionInFile(finalLastSolution);
		//	System.exit(0);	
		}	
	}//constructor
	
}//class
