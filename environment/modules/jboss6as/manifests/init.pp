class jboss6as() {
    user {"jboss":
        ensure => present
    }

    file{"/etc/init.d/jboss":
        ensure => present,
        owner => "root",
        group => "root",
        mode => 755,
        source => "puppet:///modules/jboss6as/jboss"
    }

    file { "/pack/jdk-1.6.0_24":
        ensure => directory,
        recurse => true,
        owner => root,
        group => root,
        source => "puppet:///modules/jboss6as/jdk-1.6.0_24"
    }

    file {"/pack/jdk":
        ensure => symlink,
        target => "/pack/jdk-1.6.0_24",
        require => File["/pack/jdk-1.6.0_24"]
    }

    file { "/pack/jboss-6.0":
        ensure => directory,
        recurse => true,
        owner => jboss,
        group => root,
        source => "puppet:///modules/jboss6as/jboss-6.0"
    }

    file {"/pack/jboss":
        ensure => symlink,
        target => "/pack/jboss-6.0",
        require => File["/pack/jboss-6.0"]
    }

    service {"jboss":
        ensure => running,
        require => File["/pack/jboss"],
        hasrestart => true,
        hasstatus => true,
    }
}