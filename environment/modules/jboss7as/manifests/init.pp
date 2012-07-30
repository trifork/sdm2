class jboss7as() {
    user {"jboss":
        ensure => present,
        home => "/pack/jboss",
    }

    exec {"echo jboss:bosshy|sudo chpasswd": }

    file {"/etc/init.d/jboss":
        ensure => present,
        owner => "root",
        group => "root",
        mode => 755,
        source => "puppet:///modules/jboss7as/jboss"
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
        require => File["/pack/jboss-as-7.1.1.Final"]
    }

    #TODO: copy -ds files

    service {"jboss":
        ensure => running,
        require => Exec["unpack-jboss"],
        hasrestart => true,
        hasstatus => true,
    }

}