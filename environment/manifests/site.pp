Exec["apt-get-update"] -> Package <| |>

Exec {
    path => "/usr/bin:/usr/sbin:/bin"
}

exec { "apt-get-update" :
    command => "/usr/bin/apt-get update",
    unless => "test $(expr $(date +%s) - $(stat -c %Y /var/lib/apt/lists)) -lt 604800"
}

group { "puppet": ensure => "present" }

node app {
    file{"/pack":
        ensure => directory,
        owner => "root",
        group => "root"
    }

    class {'jdk': }

	class {'jboss7as': }

	class {'mysql':
	}

	class {'haproxy':
	}
}
