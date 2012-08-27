define jboss7as::importermodule( $importername = '') {
	include "jboss7as::importerdirectories"

	if $importername == '' {
        $modulename = $title
      } else {
        $modulename = $importername
      }

	File {
		ensure => present,
    	owner => "jboss",
		group => "jboss",
	}

	# NSP-Util Slalog configuration
	file {"/pack/jboss/domain/slalog-conf/nspslalog-${modulename}.properties":
		content => template("jboss7as/nspslalog-importer.properties"),
	}

	file {"/pack/jboss/domain/slalog-conf/log4j-nspslalog-${modulename}.properties":
		content => template("jboss7as/log4j-nspslalog-importer.properties"),
	}

	# JBoss module
   	file {["/pack/jboss/modules/sdm4/config/${modulename}", "/pack/jboss/modules/sdm4/config/${modulename}/main"]:
        ensure => directory,
        owner => "jboss",
        require => File["/pack/jboss"],
    }

	file {"/pack/jboss/modules/sdm4/config/${modulename}/main/log4j.properties":
		content => template("jboss7as/log4j-importer.properties"),
		require => File["/pack/jboss/modules/sdm4/config/${modulename}/main"],
	}

	file {"/pack/jboss/modules/sdm4/config/${modulename}/main/module.xml":
		content => template("jboss7as/sdm-importer-module.xml"),
		require => File["/pack/jboss/modules/sdm4/config/${modulename}/main"],
	}


}