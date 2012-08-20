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

    file {"/pack/jboss/modules/com/mysql":
        ensure => directory,
        owner => "jboss",
        require => File["/pack/jboss"],
    }

    file {"/pack/jboss/modules/com/mysql/main":
        ensure => directory,
        owner => "jboss",
        require => File["/pack/jboss/modules/com/mysql"],
    }

    file {"/pack/jboss/modules/com/mysql/main/mysql-connector-java-5.1.21.jar":
        ensure => present,
        source => "puppet:///modules/jboss7as/mysql-connector-java-5.1.21.jar",
        owner => "jboss",
        require => File["/pack/jboss/modules/com/mysql/main"]
    }

    file {"/pack/jboss/modules/com/mysql/main/module.xml":
        ensure => present,
        source => "puppet:///modules/jboss7as/mysql-module.xml",
        owner => "jboss",
        require => File["/pack/jboss/modules/com/mysql/main"]
    }

    file {"/pack/jboss/standalone/configuration/standalone.xml":
        ensure => present,
        owner => "jboss",
        source => "puppet:///modules/jboss7as/standalone.xml",
        require => File["/pack/jboss"]
    }

    file {["/pack/jboss/modules", "/pack/jboss/modules/sdm4", "/pack/jboss/modules/sdm4/config", "/pack/jboss/modules/sdm4/config/cpr", "/pack/jboss/modules/sdm4/config/cpr/main"]:
        ensure => directory,
        owner => "jboss",
        require => File["/pack/jboss"],
    }

    file {"/pack/jboss/modules/sdm4/config/cpr/main/module.xml":
        ensure => present,
        source => "puppet:///modules/jboss7as/sdm-cpr-module.xml",
        owner => "jboss",
        require => File["/pack/jboss/modules/sdm4/config/cpr/main"]
    }

    file {"/pack/jboss/modules/sdm4/config/cpr/main/log4j.properties":
        ensure => present,
        source => "puppet:///modules/jboss7as/log4j-cpr.properties",
        owner => "jboss",
        require => File["/pack/jboss/modules/sdm4/config/cpr/main"]
    }

    file {"/home/vagrant/.bash_history":
        ensure => present,
        source => "puppet:///modules/jboss7as/.bash_history",
        owner => "jboss",
    }

    file {"/pack/jboss/domain/data/sdm4":
        ensure => directory,
        owner => "jboss",
        require => File["/pack/jboss"],
    }

    service {"jboss":
        ensure => running,
        name => "jboss-as",
        require => [Exec["unpack-jboss"], Class["jdk"], File["jboss-init-script"]],
    }
}