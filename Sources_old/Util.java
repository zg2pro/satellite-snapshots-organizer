import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Vector;


public class Util {

	private static Transition[] swap (Transition[] tab, int a, int b){
		Transition c = tab[a];
		tab[a] = tab[b];
		tab[b] = c;
		return tab;
	}
	
	public static List<Transition> shuffle (Set aSet){
		List<Transition> theSetAsAShuffledList = new ArrayList<Transition>();
		theSetAsAShuffledList.addAll(aSet);
		Collections.reverse(theSetAsAShuffledList);
		return theSetAsAShuffledList;
	}//shuffle
	
	public static void writeSolutionInFile (Vector<Image> theVector, String num){
	    PrintWriter aPW;
	    try {
	    	FileOutputStream theFOS = new FileOutputStream("mySolutions/solution_"+num);
	    	
		    aPW = new PrintWriter(theFOS);
	    	aPW.println(num.replaceAll("_", " "));
	    	if (theVector != null){
			    aPW.println(theVector.size());
			    for (Image img : theVector){
			    	if (img.isStripDirect) aPW.println(img.indStrip+" "+1);
			    	else aPW.println(img.indStrip+" "+0);
			    }
	    	}//if vecteur pas vide
		    aPW.flush();
		    aPW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   

	}
	
}//class
