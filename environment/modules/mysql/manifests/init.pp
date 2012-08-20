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

    file {"/etc/mysql/my.cnf":
        ensure => present,
		source => "puppet:///modules/mysql/my.cnf",
		owner => "mysql",
		group => "mysql",
		require => Package["mysql-server"],
		notify => Service["mysql"],
	}

    $mysql_password = "papkasse"

    exec { "set-mysql-root-password":
    	unless => "mysqladmin -uroot -p$mysql_password status",
    	command => "mysqladmin -uroot password $mysql_password",
    	path => ["/bin", "/usr/bin"],
    	subscribe => Service["mysql"],
    	refreshonly => true,
    }

    file {"/tmp/bootstrap.sql":
        ensure => present,
        content => template('mysql/bootstrap.sql')
    }

    exec { "bootstrap-db":
        command => "mysql -u root -p$mysql_password < /tmp/bootstrap.sql",
        require => [File["/tmp/bootstrap.sql"], Service["mysql"], Exec["set-mysql-root-password"]],
        onlyif => "test `mysql -uroot -p$mysql_password -e 'SHOW DATABASES;' | grep sdm_warehouse | wc -l` -eq 0"
    }

  exec { "enable-root-network-access":
  	path => ["/bin", "/usr/bin"],
  	command => "mysql -uroot -p$mysql_password -e \"use mysql; update user set host='%' where user='root' and host='localhost'; flush privileges;\"",
    require => [Service["mysql"], Exec["set-mysql-root-password"]]
  }
}