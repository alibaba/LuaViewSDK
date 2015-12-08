#!/user/bin/python
#/**********************
# filename:FYUtility.py
# owner: fangying
# tel: 15810128006
#***********************/

import os
import hashlib
import sys

version = '1.0';

def model_name():
    return __name__ #return this model name
    

def file_md5(file_path):
    with open(file_path, 'rb') as f:
        md5obj = hashlib.md5()
        md5obj.update(f.read())
        hash = md5obj.hexdigest()
        print(hash)
        return hash
        
def file_sha1(file_path):
    with open(file_path, 'rb') as f:
        sha1obj = hashlib.sha1()
        sha1obj.update(f.read())
        hash = sha1obj.hexdigest()
        print(hash)
        return hash
        

def get_file_md5(file)
    if not os.path.exists(file):
        hashfile = os.path.join(os.path.dirname(__file__), hashfile)
        if not os.path.exists(hashfile):
            print("cannot found file")
            print hashfile
        else
            file_md5(hashfile)
    else
        file_md5(file)

