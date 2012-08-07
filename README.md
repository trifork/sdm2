# Stamdata Manager 2

## Igang

Klon repo med ```$ git clone https://github.com/trifork/sdm2.git --recursive```.
På grund af Oracle licenser må vi desværre ikke redistribuere jboss og jdk'et. Derfor skal disse lånes fra _niab_. "tar.gz" pak /pack/jdk-1.6.0_24 og /pack/jboss-6.0 til environment/modules/jboss6as/files/. -Eller få dem udleveret af _mwl at trifork dot com_

Kør
    vagrant up
det fejler (desværre) som regel med noget a la
    err: /Stage[main]/Jboss7as/Exec[deploy-sample]/returns: change from notrun to 0 failed: /pack/jboss/bin/jboss-cli.sh -c --commands='deploy --force /tmp/sdm-sample.war' --user=sdmadmin --password=trifork returned 1 instead of one of [0] at /tmp/vagrant-puppet/modules-0/jboss7as/manifests/init.pp:103

Hvis det gør det, så kør
    vagrant provision

Tjek, at der kører en sdm-sample applikation på
http://localhost:8080/sdm-sample