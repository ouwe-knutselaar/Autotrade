# Autotrade

# Prerequisites
Runs under javav 1.8 or higher

Place the `/src/main/resources/autotrade.properties` in a directory in  
linux:   `$HOME/.autotrade/autotrade.properties`  
windows: `%HOMEPATH%/.autotrade/autotrade.properties`  

Place in this file the Bitvavo API keys

# Compile
compile with maven:
`mvn clean package`

# Run
To run
go in the 'target' directory and run:  
`java -jar AutoTrade-1.0-SNAPSHOT.jar`

# Monitoring
The activity can be viewed via http://127.0.0.1:8090

