import java.io.File;
import java.util.HashSet;
import java.util.Set;


public class Reader {

	Instance[] theInstances;
	public Reader (){
		File directory = new File("./Instances" );
		String[] files = directory.list();
		theInstances = new Instance[files.length];
		int i = 0;
		for (String it : files){
			theInstances[i] = new Instance("./Instances/"+it, 633, 0.05, 2.5);
			Util.writeSolutionInFile(
					(new Solution(theInstances[i++])).finalLastSolution, 
					it.replaceAll("instance_", ""));
		}//for
	}//constructor
	
	public Reader (String num){
			theInstances = new Instance[1];
			theInstances[0] = new Instance("./Instances/instance_"+num, 633, 0.05, 2.5);
			Util.writeSolutionInFile(
					(new Solution(theInstances[0])).finalLastSolution, 
					num);
	}//constructor 2
	
	
	public static void main (String [] args){
		new Reader();
	}//main
	
}//class
