class jboss7as::importerdirectories {
	File {
		owner => "jboss",
		group => "jboss"
	}

	# For Jboss modules
    file { ["/pack/jboss/modules", "/pack/jboss/modules/sdm4/", "/pack/jboss/modules/sdm4/config"]:
    	ensure => directory,
    }

	# For NSP-Util SLA log config
    file { ["/pack/jboss/domain/slalog-conf"]:
    	ensure => directory,
    }

    # For the sample data dir
	file {"/pack/jboss/domain/data/sdm4":
		ensure => directory,
	}
}