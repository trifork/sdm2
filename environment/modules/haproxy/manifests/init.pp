class haproxy() {
	package { "haproxy":
        ensure => installed,
	}

	file { "/etc/default/haproxy":
        ensure => present,
        require => Package["haproxy"],
        content => "ENABLED=1"
	}

	file { "/etc/haproxy/haproxy.cfg":
        ensure => present,
        content => template('haproxy/haproxy.conf'),
        require => Package["haproxy"],
        notify => Service["haproxy"]
	}

	service { "haproxy":
        ensure => running,
        require => [Package["haproxy"], File["/etc/default/haproxy"]],
        hasrestart => true,
        hasstatus => true
	}
			
}
