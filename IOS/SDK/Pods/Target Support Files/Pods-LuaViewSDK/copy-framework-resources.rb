#!/usr/bin/env ruby
# encoding: UTF-8

require 'Xcodeproj'
require 'fileutils'

Encoding.default_external = Encoding::UTF_8
Encoding.default_internal = Encoding::UTF_8

current_target_name = ENV["TARGET_NAME"]
project = Xcodeproj::Project.open(ENV["PROJECT_FILE_PATH"])

# 分析环境变量参数
def parse_args(args)
	index = 0
	arg_list = []
	while args && args.length > 0 && args.length > index
		space_index = args.index(" ", index)
		space_index = -1 if space_index.nil?
		arg = args[0..space_index]
		if arg.count("\"") % 2 == 0 && arg.count("'") % 2 == 0
			arg_list << arg.strip
			args = space_index >= 0 ? args[space_index+1..-1] : nil
			index = 0
		end
	end
	arg_list
end

def find_key(array, key)
	for dic in array
		if dic.keys[0] == key
			return dic[key]
		end
	end
end

current_target = project.targets.find { |target| target.name == current_target_name }

frameworks = current_target.frameworks_build_phases.files.map{|file| file.file_ref.nil? ? "" : File.basename(file.file_ref.path) }.select { |x| File.extname(x) == ".framework"}

FRAMEWORK_SEARCH_PATHS = []
parse_args(ENV['FRAMEWORK_SEARCH_PATHS']).each { |arg| FRAMEWORK_SEARCH_PATHS << arg.gsub("\"", "") }

for framework in frameworks
    FRAMEWORK_SEARCH_PATHS.each do |path|
        framework_path = File.join(path, framework)
        if File.directory?(framework_path)
            ignore_list = ["Info.plist", ".DS_Store"]
            if File.directory?(File.join(framework_path, "Resources"))
                list = Dir.glob(File.join(framework_path, "Resources/*"))
            else
            	#增加Resources的过滤，之前喵街引入的一库，存在Resources，但是指向的目录不存在。导致cp一个空的符号链接会报错 by兵长 20170706
                ignore_list += [File.basename(framework, '.framework'), "Headers", "PrivateHeaders", "Modules", "Versions", "_CodeSignature", "Resources"]
                list = Dir.glob(File.join(framework_path, "*"))
            end
            list.reject!{|entry| ignore_list.include?(File.basename(entry)) }
            list.each { |file| puts file}
            FileUtils.cp_r list, File.join(ENV["TARGET_BUILD_DIR"], ENV["UNLOCALIZED_RESOURCES_FOLDER_PATH"])
            break
        end
    end
end
