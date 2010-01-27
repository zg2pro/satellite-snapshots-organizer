
|---------------------------------------------------------------------|
|--          R O A D E F     C h a l l e n g e  2 0 0 3             --|
|--                                                                 --|
|---------------------------------------------------------------------|

/*********************************************************************/
--- HOW TO GET THE CHALLENGE DATA AND PROGRAMS
--------------------------------------------------------------------

  Unix installation:
  ---------------------
     Unzip the file Challenge.zip into a directory named Challenge
     and that's all!

/*********************************************************************/
-- CHALLENGE DIRECTORIES AND FILES
--------------------------------------------------------------------


  The <Challenge> directory contains the following directories : 
  --------------------------------------------------------------

  * the <Formats> directory, containing the description of the instance,
    solution and result formats
  
  * the <Instances> directory, where are located all the files describing 
    the instances

  * the <Solutions> directory, where are located the files describing, for
    each instance, a solution which has been obtained with one our solving
    algorithms
  
  * the <Results> directory, where are located the files describing 
    the results of the solution checking program applied to the each solution
  
  * the <Constants> directory, where is located the file describing the 
    constants of the problem    
  
  * the <Classes> directory, containing the Java classes of the solution 
    checking program
  
  * the <Sources> directory, containing the Java source code of the 
    solution checking program  
    
  * the <Html> directory, containing the Java documentation of the 
    solution checking program  
  

  The <Challenge> directory contains the following files : 
  --------------------------------------------------------------

  * this <ReadMe.txt> file

  * a make file <makefile>
  
  * a solution checking shell for UNIX OS <check-assess.sh>
  
  * a solution checking bat file for Windows OS <check-assess.bat>
  

  The file tree of the <Challenge> directory is the following :
  -------------------------------------------------------------
  \-- Challenge
        makefile
        check-assess.sh
        check-assess.bat
        ReadMe.txt
    \-- Classes
          Constants.class
          GeoPoint.class
          Image.class
          InstanceProblem.class
          Request.class
          Solution.class
          SolutionCheck.class
          Strip.class
    \-- Constants
          constants
    \-- Formats
          instance-format.txt
          instance-example_2_9_36
          result-format.txt
          result-example_2_9_36
          solution-format.txt
          solution-example_2_9_36
    \-- Html
          allclasses-frame.html
          Constants.html
          deprecated-list.html
          GeoPoint.html
          help-doc.html
          Image.html
          index-all.html
          index.html
          InstanceProblem.html
          overview-tree.html
          package-list
          packages.html
          Request.html
          serialized-form.html
          Solution.html
          SolutionCheck.html
          Strip.html
          stylesheet.css
    \-- Instances
          instance_2_9_36
          instance_2_9_66
          instance_2_9_170
          instance_3_8_155
          instance_2_13_111
          instance_4_17_186
          instance_2_15_170
          instance_3_25_22
          instance_2_26_96
          instance_2_27_22
    \-- Results
          result_2_9_36
          result_2_9_66
          result_2_9_170
          result_3_8_155
          result_2_13_111
          result_4_17_186
          result_2_15_170
          result_3_25_22
          result_2_26_96
          result_2_27_22
    \-- Solutions
          solution_2_9_36
          solution_2_9_66
          solution_2_9_170
          solution_3_8_155
          solution_2_13_111
          solution_4_17_186
          solution_2_15_170
          solution_3_25_22
          solution_2_26_96
          solution_2_27_22
    \-- Sources
          Constants.java
          GeoPoint.java
          Image.java
          InstanceProblem.java
          Request.java
          Solution.java
          SolutionCheck.java
          Strip.java
          

/*********************************************************************/
-- THE INSTANCES OF THE CHALLENGE
--------------------------------------------------------------------

  The <Instances> dircetory contains 10 files, each associated with one
instance.
  The instances <instance_2_9_36> and <instance_2_9_66> are very small 
(respectively 2 and 7 strips). They can be used for program debugging, but
do not belong to the challenge.
  The challenge involves the other 8 instances : 
  
  * the instances <instance_2_9_170> and <instance_3_8_155> contain around
    25 strips (respectively 25 and 28)

  * the instances <instance_2_13_111> and <instance_4_17_186> contain around
    100 strips (respectively 106 and 147)
    
  * the instances <instance_2_15_170> and <instance_3_25_22> contain around
    300 strips (respectively 295 and 342)
          
  * the instances <instance_2_26_96> and <instance_2_27_22> contain around
    500 strips (respectively 483 and 534)


/*********************************************************************/  
--  HOW TO RUN THE SOLUTION CHECKING PROGRAM
--------------------------------------------------------------------

  In order to use the solution checking program, a Java Development Kit 
  (jdk) the version number of which is equal to or greater than 1.3.1 must 
  be installed.
  
  Before running the program, you must insure that the directory from where
  you run it respects the file tree described above.
 
  To run the program :
  --------------------
          1. Open a Terminal window 
          2. Set the SolutionCheck directory to be the current directory
          3. Just type the command :
          
               check-assess.sh  under UNIX OS
               check-assess.bat under Windows OS     
            
            You should see an output like that:

    
    --------------------------------------------------------------------------------------------------
    --                 Challenge ROADEF 2003 -  Solution  Checking Program                            --
    ----------------------------------------------------------------------------------------------------
 
    Checking file existency ...
    All files exist!
 
    Reading instance file ./Instances/instance_2_27_22 ...
    Instance : {data set= 2; grid = 27; track = 22; Nr=375; Ns=534}
 
    Reading constants file ./Constants/constants ...
    Constants : {Hs=633000.0; Vr=0.05; Dmin=2.5}
 
    Reading solution file ./Solutions/solution_2_27_22 ...
    Solution : {data set= 2; grid = 27; track = 22; Na=35}
               Feasible; g= 615164930
 
    Writing solution checking results in file ./Results/result_2_27_22 ...
    File ./Results/result_2_27_22 generated
    
    
/*********************************************************************/  
--  QUESTIONS ??
--------------------------------------------------------------------

  For any question about the installation or the use of this program, mail to

      Matthieu.Lombard@cert.fr
   or Gerard.Verfaillie@cert.fr
      
