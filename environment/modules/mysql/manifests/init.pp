class mysql() {
    package { "mysql-server":
        ensure => installed
    }

    service { "mysql":
        ensure => running,
        require => Package["mysql-server"],
        hasrestart => true,
        hasstatus => true
    }

    file {"/tmp/bootstrap.sql":
        ensure => present,
        content => template('mysql/bootstrap.sql')
    }

    exec { "bootstrap-db":
        command => "mysql -u root < /tmp/bootstrap.sql",
        require => [File["/tmp/bootstrap.sql"], Service["mysql"]],
        unless => 'test `mysql -u root <<< "SHOW DATABASES;" | grep sdm-sample | wc -l` -eq 0'
    }
}