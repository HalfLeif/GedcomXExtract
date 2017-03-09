
assembly :
	mvn compile
	mvn package
	mvn install assembly:assembly
