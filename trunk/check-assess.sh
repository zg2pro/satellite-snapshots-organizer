#! /bin/sh

#----------------------------------------------------------------------------------------#
#------------------                ROADEF Challenge 2003               ------------------#
#----------------------------------------------------------------------------------------#


#------------------------------------------File------------------------------------------#
# verify.sh
# author Matthieu Lombard Matthieu.Lombard@cert.fr
#----------------------------------------------------------------------------------------#


#---------------------------------------Variables----------------------------------------#
#mainClass = SolutionCheck
#path variables
#instancePath
IP=./Instances
#constantPath
CP=./Constants
#solutionPath
SP=./Solutions
#results path
RP=./Results
#constant file
CF=$CP/constants
#----------------------------------------------------------------------------------------#


#--------------------------- RUNNING THE PROGRAM ON INSTANCES --------------------------#

#------------         2 instances containing less than 10 strips            ------------#
# instance 2_9_36 containing 2 strips
IF=$IP/instance_2_9_36
SF=$SP/solution_2_9_36
RF=$RP/result_2_9_36
java -classpath Classes/ SolutionCheck $IF $SF $CF $RF
# instance 2_9_66 containing 7 strips
IF=$IP/instance_2_9_66
SF=$SP/solution_2_9_66
RF=$RP/result_2_9_66
java -classpath Classes/ SolutionCheck $IF $SF $CF $RF


#------------          2 instances containing around 25 strips              ------------#
# instance 2_9_170 containing 25 strips
IF=$IP/instance_2_9_170
SF=$SP/solution_2_9_170
RF=$RP/result_2_9_170
java -classpath Classes/ SolutionCheck $IF $SF $CF $RF
# instance 3_8_155 containing 28 strips
IF=$IP/instance_3_8_155
SF=$SP/solution_3_8_155
RF=$RP/result_3_8_155
java -classpath Classes/ SolutionCheck $IF $SF $CF $RF


#------------         2 instances containing around 100 strips              ------------#
# instance 2_13_111 containing 106 strips
IF=$IP/instance_2_13_111
SF=$SP/solution_2_13_111
RF=$RP/result_2_13_111
java -classpath Classes/ SolutionCheck $IF $SF $CF $RF
# instance 4_17_186 containing 147 strips
IF=$IP/instance_4_17_186
SF=$SP/solution_4_17_186
RF=$RP/result_4_17_186
java -classpath Classes/ SolutionCheck $IF $SF $CF $RF

#------------         2 instances containing around 300 strips              ------------#
# instance 2_15_170 containing 295 strips
IF=$IP/instance_2_15_170
SF=$SP/solution_2_15_170
RF=$RP/result_2_15_170
java -classpath Classes/ SolutionCheck $IF $SF $CF $RF
# instance 3_25_22 containing 342 strips
IF=$IP/instance_3_25_22
SF=$SP/solution_3_25_22
RF=$RP/result_3_25_22
java -classpath Classes/ SolutionCheck $IF $SF $CF $RF

#------------         2 instances containing around 500 strips              ------------#
# instance 2_26_96 containing 483 strips
IF=$IP/instance_2_26_96
SF=$SP/solution_2_26_96
RF=$RP/result_2_26_96
java -classpath Classes/ SolutionCheck $IF $SF $CF $RF

# instance 2_27_22 containing 534 strips
IF=$IP/instance_2_27_22
SF=$SP/solution_2_27_22
RF=$RP/result_2_27_22
java -classpath Classes/ SolutionCheck $IF $SF $CF $RF
