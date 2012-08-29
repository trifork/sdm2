mysql -uroot -ppapkasse
sudo cp /vagrant/cprimporter/target/cprimporter-1.0-SNAPSHOT.war /pack/jboss/standalone/deployments/
less +F /pack/jboss/standalone/log/server.log
less +F /pack/jboss/standalone/log/cprimporter.log
cd /pack/jboss/standalone/log/
/pack/jboss/bin/jboss-cli.sh -c --user=sdmadmin --password=trifork
sudo service jboss-as restart
