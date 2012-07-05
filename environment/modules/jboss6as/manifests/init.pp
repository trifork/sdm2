class jboss6as() {
    user {"jboss":
        ensure => present,
        home => "/pack/jboss",
        password => "d82abb4db1464d4264db880b83d0c322700f1e9c"
    }

    file {"/tmp/jdk.tar.gz":
        ensure => present,
        source => "puppet:///modules/jboss6as/jdk.tar.gz"
    }
    exec {"unpack-jdk":
        command => "tar xvzf /tmp/jdk.tar.gz",
        cwd => "/pack",
        onlyif => "test ! -d /pack/jdk-1.6.0_24"
    }
    file {"/pack/jdk":
        ensure => symlink,
        target => "/pack/jdk-1.6.0_24",
        require => Exec["unpack-jdk"]
    }
    file {"/tmp/jboss.tar.gz":
        ensure => present,
        source => "puppet:///modules/jboss6as/jboss.tar.gz"
    }

    file {"/etc/init.d/jboss":
        ensure => present,
        owner => "root",
        group => "root",
        mode => 755,
        source => "puppet:///modules/jboss6as/jboss"
    }
    file {"/pack/jboss-6.0":
        ensure => directory,
        owner => jboss,
        group => jboss,
    }
    exec {"unpack-jboss":
        command => "tar xvzf /tmp/jboss.tar.gz",
        cwd => "/pack",
        onlyif => "test ! -d /pack/jboss-6.0/bin",
        user => "jboss",
        require => File["/pack/jboss-6.0"]
    }
    file {"/pack/jboss":
        ensure => symlink,
        target => "/pack/jboss-6.0",
        require => File["/pack/jboss-6.0"]
    }
    service {"jboss":
        ensure => running,
        require => Exec["unpack-jboss"],
        hasrestart => true,
        hasstatus => true,
    }
}