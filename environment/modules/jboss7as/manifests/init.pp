class jboss7as() {
    user {"jboss":
        ensure => present,
        home => "/pack/jboss",
    }

    exec {"echo jboss:bosshy|sudo chpasswd": }

    file {"jboss-init-script":
        ensure => present,
        owner => "root",
        group => "root",
        mode => 755,
        path => "/etc/init.d/jboss-as",
        source => "puppet:///modules/jboss7as/jboss-as",
    }

    file {"/tmp/jboss-as-7.1.1.Final.tar.gz":
        ensure => present,
        source => "puppet:///modules/jboss7as/jboss-as-7.1.1.Final.tar.gz"
    }

    file {"/pack/jboss-as-7.1.1.Final":
        ensure => directory,
        owner => jboss,
        group => jboss,
    }

    exec {"unpack-jboss":
        command => "tar xvzf /tmp/jboss-as-7.1.1.Final.tar.gz",
        cwd => "/pack",
        unless => "test -d /pack/jboss-as-7.1.1.Final/bin",
        user => "jboss",
        require => File["/pack/jboss-as-7.1.1.Final"]
    }

    file {"/pack/jboss":
        ensure => symlink,
        target => "/pack/jboss-as-7.1.1.Final",
        require => [File["/pack/jboss-as-7.1.1.Final"], Exec["unpack-jboss"]]
    }

    file {"/pack/jboss/standalone/configuration/mgmt-users.properties":
        ensure => present,
        owner => "jboss",
        source => "puppet:///modules/jboss7as/mgmt-users.properties",
        require => File["/pack/jboss"]
    }

    file {"/pack/jboss/standalone/configuration/standalone.xml":
        ensure => present,
        owner => "jboss",
        source => "puppet:///modules/jboss7as/standalone.xml",
        require => File["/pack/jboss"]
    }

    file {"/home/vagrant/.bash_history":
        ensure => present,
        source => "puppet:///modules/jboss7as/.bash_history",
        owner => "vagrant",
    }

    service {"jboss":
        ensure => running,
        name => "jboss-as",
        require => [Exec["unpack-jboss"], Class["jdk"], File["jboss-init-script"]],
    }

	include "jboss7as::mysqlmodule"

	jboss7as::importermodule { "cprimporter": }

	jboss7as::importermodule { "sorrelationimporter": }

	jboss7as::importermodule { "sorimporter": }
	
	jboss7as::importermodule { "bemyndigelseimporter": }
	
	jboss7as::importermodule { "doseringimporter": }

	jboss7as::importermodule { "autorisationimporter": }
	
	jboss7as::importermodule { "sikredeimporter": }

	jboss7as::importermodule { "sksimporter": }

	jboss7as::importermodule { "takstimporter": }
	
	jboss7as::importermodule { "yderimporter": }
}