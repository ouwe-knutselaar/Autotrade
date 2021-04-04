echo "Create database tables"
cat mariadb.sql | mysql

mkdir -p $HOME/.autotrade/
cp autotrade.properties $HOME/.autotrade/autotrade.properties

mkdir -p /opt/autotrade
cp ../AutoTrade-1.0-SNAPSHOT.jar /opt/autotrade/AutoTrade-1.0-SNAPSHOT.jar