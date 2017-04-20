
Pod::Spec.new do |s|

  s.name         = "lua"
  s.version  = "5.1.4"
  s.summary      = "lua"

  s.description  = <<-DESC
                   A longer description of lua.podspec in Markdown format.
                   * LuaView
                   DESC

  s.homepage     = "http://gitlab.alibaba-inc.com/luaview/lua"


  s.license      = { :type => 'GNU General Public Licence (GPL), Version 2.0', :file => 'LICENSE.txt' }

  s.author             = { "城西" => "xicheng.dxc@alibaba-inc.com" }

  # ――― Platform Specifics ――――――――――――――――――――――――――――――――――――――――――――――――――――――― #
  #
  #  If this Pod runs only on iOS or OS X, then specify the platform and
  #  the deployment target. You can optionally include the target after the platform.
  #

  s.platform     = :ios, "7.0"

  s.source       = { :git => "git@gitlab.alibaba-inc.com:luaview/lua.git", :tag => "2.1.1.2" }

  s.source_files  = "lua/**/*.{h,m,c}"
  # s.exclude_files = "Classes/Exclude"


  # ――― Resources ―――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――― #
  #
  #  A list of resources included with the Pod. These are copied into the
  #  target bundle with a build phase script. Anything else will be cleaned.
  #  You can preserve files from being cleaned, please don't preserve
  #  non-essential files like tests, examples and documentation.
  #

  # s.resource  = "icon.png"
  # s.resources = "Resources/*.png"

  # s.preserve_paths = "FilesToSave", "MoreFilesToSave"


  # ――― Project Linking ―――――――――――――――――――――――――――――――――――――――――――――――――――――――――― #
  #
  #  Link your library with frameworks, or libraries. Libraries do not include
  #  the lib prefix of their name.
  #

  s.frameworks = 'Foundation', 'UIKit', 'CoreGraphics'
  s.libraries = 'z'

  # ――― Project Settings ――――――――――――――――――――――――――――――――――――――――――――――――――――――――― #
  #
  #  If your library depends on compiler flags you can set them in the xcconfig hash
  #  where they will only apply to your library. If you depend on other Podspecs
  #  you can include multiple dependencies to ensure it works.

  s.requires_arc = true

end
