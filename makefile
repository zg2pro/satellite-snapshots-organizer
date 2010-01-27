# ROADEF Challenge 2003
# makefile
# author Matthieu Lombard Matthieu.Lombard@cert.fr
# main class = SolutionCheck

compile : Sources/SolutionCheck.java
	javac -sourcepath Sources/ -d Classes/ Sources/SolutionCheck.java
doc :
	javadoc -sourcepath Sources/*.java \
	-d Html \
	-windowtitle 'Solution' \
	-link http://java.sun.com/products/jdk/1.2/docs/api/
