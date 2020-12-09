for f in *.class; do CLASSPATH=$f:$CLASSPATH; done
for f in *.jar; do CLASSPATH=$f:$CLASSPATH; done
java -classpath "$CLASSPATH" Server