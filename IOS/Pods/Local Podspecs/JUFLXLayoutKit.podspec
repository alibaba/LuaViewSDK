#
# Be sure to run `pod lib lint JUFLXLayoutKit.podspec' to ensure this is a
# valid spec and remove all comments before submitting the spec.
#
# Any lines starting with a # are optional, but encouraged
#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = "JUFLXLayoutKit"
  s.version          = "0.1.1"
  s.summary          = "Flexbox builded on UIKit using Facebook css_layout"
  s.description      = <<-DESC
                       Flexbox builded on UIKit using Facebook css_layout

                       DESC
  s.homepage         = "http://gitlab.alibaba-inc.com/juhuasuanwireless/JUFLXLayoutKit"
  # s.screenshots     = "www.example.com/screenshots_1", "www.example.com/screenshots_2"
  s.license          = 'MIT'
  s.author           = { "逸言" => "kaiwei.xkw@alibaba-inc.com" }
  s.source           = { :git => "http://gitlab.alibaba-inc.com/juhuasuanwireless/JUFLXLayoutKit.git", :tag => s.version.to_s }
  # s.social_media_url = 'https://twitter.com/<TWITTER_USERNAME>'

  s.platform     = :ios, '6.0'
  s.requires_arc = true

  s.source_files = 'Pod/Classes/*{h,m,c}'
  s.resource_bundles = {
    'JUFLXLayoutKit' => ['Pod/Assets/*.png']
  }
  s.public_header_files = 'Pod/Classes/**/*.h'

  s.subspec 'LayoutExtensions' do |core|
    core.source_files = 'Pod/Classes/LayoutExtensions/*{h,m}'
  end

end
