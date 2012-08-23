class jboss7as::importerdirectories {
	File {
		owner => "jboss",
		group => "jboss"
	}

	# For Jboss modules
    file { ["/pack/jboss/modules", "/pack/jboss/modules/sdm4/", "/pack/jboss/modules/sdm4/config"]:
    	ensure => directory,
    }

    file { ["/pack/jboss/domain", "/pack/jboss/domain/slalog-conf"]:
    	ensure => directory,
    }
}