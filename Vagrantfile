# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant::Config.run do |config|
  config.vm.box = "lucid64"
  config.vm.box_url="http://files.vagrantup.com/lucid64.box"
  #config.vm.boot_mode = :gui # useful when the guest does not manage to get an IP via DHCP, meaning vagrant cannot ssh to the box. Fix by login to the console as vagrant/vagrant and run sudo /etc/init.d/networking restart
  config.vm.customize ["modifyvm", :id, "--rtcuseutc", "on"] # seems to fix https://github.com/mitchellh/vagrant/issues/391
  containerport = (ENV['CONTAINERPORT'] || 8080).to_i
  mysqlport = (ENV['MYSQLPORT'] || 3307).to_i

  config.vm.provision :puppet, :module_path => "environment/modules" do |puppet|
    puppet.manifests_path = "environment/manifests"
    puppet.manifest_file = "site.pp"
    puppet.options = "--verbose --debug"
  end

  config.vm.define :app do |config|
    config.vm.customize ["modifyvm", :id, "--memory", 2048]
    config.vm.forward_port 8080, containerport
    config.vm.forward_port 3306, mysqlport
    config.vm.host_name = "app.stamdata.nsi.test"
  end
end
