#!/user/bin/python
#/**********************
# filename:FYFindFile.py
# owner: fangying
# tel: 15810128006
#***********************/

import FYLog
import os

version = '1.0';

def say_hello():
    print ('Hello world')
    
def model_name():
    return __name__ #return this model name
    
def find_file_by_postfix(dest_path, postfix):
    _file_list = []
    
    for root, subFolders, files in os.walk(dest_path):
        for f in files:
            if f.find(postfix) != -1:
               _file_list.append(os.path.join(root, f))
          
    return _file_list
        
        
def find_file(dest_path):
    _file_list = []
    
    for root, subFolders, files in os.walk(dest_path):
        for f in files:
            _file_list.append(os.path.join(root, f))
          
    return _file_list


def find_file_by_ext(dest_path, wild_card):
    _file_list = []
    exts = wild_card.split(" ")
    files= os.listdir(dest_path)

    for name in files:
        for ext in exts:
            if (name.endswith(ext)):
                _file_list.append(name)

    return _file_list


#/**********************
# recursion_find_file_by_ext : recursion file file which have the special ext
# dir: path
# file: file handler
# wild_card: ext list
# recursion:
#***********************/
def recursion_find_file_by_ext(dir, file, wild_card, recursion):
    exts = wild_card.split(" ")
    files = os.listdir(dir)

    for name in files:
        fullname = os.path.join(dir, name)

        if (os.path.isdir(fullname) and recursion) :
           recursion_new = recursion - 1
           recursion_find_file_by_ext(fullname, file, wild_card, recursion_new)
        else:
           for ext in exts:
               if (name.endswith(ext)):
                   file.write(name + " " + fullname + '\n')
                   break