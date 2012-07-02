# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant::Config.run do |config|
  config.vm.box = "lucid64"

  config.vm.provision :puppet, :module_path => "environment/modules" do |puppet|
    puppet.manifests_path = "environment/manifests"
    puppet.manifest_file = "site.pp"
  end

  config.vm.define :app do |config|
    config.vm.customize ["modifyvm", :id, "--memory", 2048]
    config.vm.forward_port 8080, 8080
    config.vm.network :hostonly, "192.168.33.10"
    config.vm.host_name = "app.stamdata.nsi.test"
  end
end
