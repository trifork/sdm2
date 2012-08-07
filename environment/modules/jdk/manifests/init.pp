class jdk() {
    file {"/tmp/jdk.tar.gz":
        ensure => present,
        source => "puppet:///modules/jdk/jdk.tar.gz"
    }
    exec {"unpack-jdk":
        command => "tar xvzf /tmp/jdk.tar.gz",
        cwd => "/pack",
        unless => "test -d /pack/jdk-1.6.0_24"
    }
    file {"/pack/jdk":
        ensure => symlink,
        target => "/pack/jdk-1.6.0_24",
        require => Exec["unpack-jdk"]
    }

    file { "/etc/profile.d/sdm_java.sh":
        ensure => present,
        content => 'JAVA_HOME=/pack/jdk
PATH=${PATH}:${JAVA_HOME}/bin',
    }
}