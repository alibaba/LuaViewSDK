#!/user/bin/python
#coding:utf-8
#/**********************
# filename:FYReadFile.py
# owner: fangying
# tel: 15810128006
#***********************/

import sys, os

sys.path.append("/Users/fangying/Documents/workspace/python-project/framework")

import FYLog, FYFindFile

version = '1.0';

#/**********************
# readlines_in_file : 按行读取指定文件的内容
# file_path: 文件路径
# 返回值: 文件内容的按行数组
#***********************/
def readlines_in_file(file_path):

    FYLog.fy_log(file_path)

    if not os.path.exists(file_path):
        print file_path
        print '返回false'
        return False

    input   = open(file_path)
    lines   = input.readlines()
    input.close()

    print lines

    return lines


def replace_line_in_file(file_path, search_key, replace_line_data):

    filelines = readlines_in_file(file_path)

    print type(filelines)
    if not isinstance(filelines, list):
        FYLog.fy_log("获得文件行内容失败")
        return False

    output  = open(file_path,'w');

    for line in filelines:

        print "jj"
        if not line:
            break
        if search_key in line:
            FYLog.fy_log(search_key + 'start')
            output.write(replace_line_data)
        else:
            output.write(line)

    output.close()
    FYLog.fy_log("helloworld--start")