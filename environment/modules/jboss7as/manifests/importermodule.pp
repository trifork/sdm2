define jboss7as::importermodule( $importername ) {
	include "jboss7as::importerdirectories"

	File {
		ensure => present,
    	owner => "jboss",
		group => "jboss",
	}

	# NSP-Util Slalog configuration
	file {"/pack/jboss/domain/slalog-conf/nspslalog-${importername}.properties":
		content => template("jboss7as/nspslalog-importer.properties"),
	}

	file {"/pack/jboss/domain/slalog-conf/log4j-nspslalog-${importername}.properties":
		content => template("jboss7as/log4j-nspslalog-importer.properties"),
	}

	# JBoss module
   	file {["/pack/jboss/modules/sdm4/config/${importername}", "/pack/jboss/modules/sdm4/config/${importername}/main"]:
        ensure => directory,
        owner => "jboss",
        require => File["/pack/jboss"],
    }

	file {"/pack/jboss/modules/sdm4/config/${importername}/main/log4j.properties":
		content => template("jboss7as/log4j-importer.properties"),
		require => File["/pack/jboss/modules/sdm4/config/${importername}/main"],
	}

	file {"/pack/jboss/modules/sdm4/config/${importername}/main/module.xml":
		content => template("jboss7as/sdm-importer-module.xml"),
		require => File["/pack/jboss/modules/sdm4/config/${importername}/main"],
	}


}