#!/usr/bin/env ruby

SDK_DIR = 'lv-sdk'
SRC_DIR = 'lv-src'
PUBLIC_REPO = 'https://github.com/alibaba/LuaViewSDK.git'

def exec_init(name)
    if Dir.exist? name then
        `rm -rf #{name}`
    end
    Dir.mkdir(name)
    Dir.chdir(name)

    Dir.mkdir(SRC_DIR)
    File.open("#{SRC_DIR}/main.lua", "w") { |f|
        f.write('print ("hello world!")')
    }

    `git clone #{PUBLIC_REPO} #{SDK_DIR}`
    
    require "./#{SDK_DIR}/cli/generators/xcode"

    Generator::Xcode.generate(name)
end

def local_update(path)
	dest = SDK_DIR

	if Dir.exist? dest then
		`rm -rf #{dest}/*`
	else
		Dir.mkdir dest
	end

	`cp -R #{path}/* #{dest}`
end

def remote_update(path)
	dest = SDK_DIR
	if path == nil then
		path = PUBLIC_REPO
	end

	`rm -rf #{dest}`

	`git clone #{path} #{dest}`
end

command = ARGV[0]
params = ARGV[1..-1]

INIT_USAGE = 'init <ProjectName>'

case command
when 'commands' then puts INIT_USAGE
when 'init' then
    proj_name = params[0]
    if proj_name == nil then
        puts "Usage: lv-cli #{INIT_USAGE}"
    else
        exec_init params[0]
    end
when 'update' then
	local = false
	path = nil
	if params[0] == '-l' then
		local = true
		path = params[1]
		if path == nil then
			puts 'Error: need specify sdk path'
			exit
		end
	else
		path = params[0]
	end

	if local then
		local_update(path)
	else
		remote_update(path)
	end
else puts 'Usage: lv-cli <command> [<args>]'
end
