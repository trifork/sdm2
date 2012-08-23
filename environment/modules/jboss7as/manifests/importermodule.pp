define jboss7as::importermodule( $importername ) {
	file {"/pack/jboss/domain/slalog-conf/nspslalog-${importername}.properties":
		ensure => present,
		content => template("jboss7as/nspslalog-importer.properties"),
		owner => "jboss",
		require => File["/pack/jboss/domain/slalog-conf"]
	}

	file {"/pack/jboss/domain/slalog-conf/log4j-nspslalog-${importername}.properties":
		ensure => present,
		content => template("jboss7as/log4j-nspslalog-importer.properties"),
		owner => "jboss",
		require => File["/pack/jboss/domain/slalog-conf"]
	}
}