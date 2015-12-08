#encoding: utf-8
#!/user/bin/python
#/**********************
# filename:FYUtility.py
# owner: fangying
# tel: 15810128006
#***********************/

import MySQLdb
import sys

version = '1.0';

def model_name():
    return __name__ #return this model name
    
def version_db():
    #open db
    db = MySQLdb.connect("localhost", "testusr", "test123", "TESTDB");
    #get cursor
    cursor = db.cursor()
    curor.execute("SELECT VERSION()")
    data = cursor.fetchone()
    db.close()
    
    
def create_db
    
def read_db():


def write_db():