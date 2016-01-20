require 'erb'

module Generator

    class Xcode

        REL_PATH = "lv-sdk/cli/generators/xcode/template"

        class << self

            def render(src, target)
                renderer = ERB.new(File.read(src))
                tf = File.open(target, "w")
                tf.write(renderer.result(binding))

                tf.close

                File.delete(src) unless src == target
            end

            def generate(name)
                @proj_name = name

                Dir.mkdir('ios')
                `cp -R #{REL_PATH}/app ios/#{name}`

                proj = "ios/#{name}.xcodeproj"
                `cp -R #{REL_PATH}/xcodeproj #{proj}`

                pbx = "#{proj}/project.pbxproj"
                render(pbx, pbx)
                scheme = "#{proj}/xcshareddata/_xcscheme"
                render(scheme, scheme)

                ut = "ios/#{name}Tests"
                `cp -R #{REL_PATH}/tests #{ut}`
                render("#{ut}/Tests.m", "#{ut}/#{name}Tests.m")
            end

        end

    end

end
