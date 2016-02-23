#!/usr/bin/env ruby

SDK_DIR = 'lv-sdk'
SRC_DIR = 'lv-src'

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

#    `git clone https://github.com/alibaba/LuaViewSDK.git lv-sdk`
    `cp -R ~/WorkSpace/LuaViewSDK lv-sdk`
    
    require "./lv-sdk/cli/generators/xcode"

    Generator::Xcode.generate(name)
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
else puts 'Usage: lv-cli <command> [<args>]'
end
