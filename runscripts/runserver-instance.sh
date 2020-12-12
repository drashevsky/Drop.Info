# Run inside screen or nohup

while :
do
	pkill -f 'java'
	for f in *.class; do CLASSPATH=$f:$CLASSPATH; done
	for f in *.jar; do CLASSPATH=$f:$CLASSPATH; done
	nohup java -classpath "$CLASSPATH" Server &
	sleep 10m
done
