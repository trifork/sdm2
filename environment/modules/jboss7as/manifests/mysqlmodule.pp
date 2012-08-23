class jboss7as::mysqlmodule {
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
}