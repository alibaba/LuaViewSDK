Pod::Spec.new do |s|


  s.name         = "lua"
  s.version  = "5.1.4"
  s.summary      = "lua"

  s.description  = <<-DESC
                   lua
                   commit:99e3debe93ea9c31d5729ffc3e6b59d0d69be6c4
                   DESC

  s.homepage     = "http://gitlab.alibaba-inc.com/junzhan/ariderlog"
  s.license = {
    :type => 'Copyright',
    :text => <<-LICENSE
           Alibaba-INC copyright
    LICENSE
  }

  s.author       = { "君展" => "junzhan.yzw@taobao.com" }

  s.platform     = :ios

  s.ios.deployment_target = '5.0'

  s.source = {:git=> 'git@gitlab.alibaba-inc.com:junzhan/ariderlog.git' ,:tag=>'1.1.0-SNAPSHOT'}

  s.source_files = 'lua/*.{h,m,c}'
  s.requires_arc = false
end