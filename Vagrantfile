# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant::Config.run do |config|
  config.vm.box = "lucid64"
  config.vm.box_url="http://files.vagrantup.com/lucid64.box"

  containerport = (ENV['CONTAINERPORT'] || 8080).to_i

  config.vm.provision :puppet, :module_path => "environment/modules" do |puppet|
    puppet.manifests_path = "environment/manifests"
    puppet.manifest_file = "site.pp"
  end

  config.vm.define :app do |config|
    config.vm.customize ["modifyvm", :id, "--memory", 2048]
    config.vm.forward_port 3306, 3306
    config.vm.host_name = "app.stamdata.nsi.test"
  end
end
