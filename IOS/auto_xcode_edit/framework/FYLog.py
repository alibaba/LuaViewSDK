#!/user/bin/python
#/**********************
# filename:FYLog.py
# owner: fangying
# tel: 15810128006
#***********************/

version = '1.0';
    
def modelName():
    return __name__ #return this model name
    
def fy_log(logStr):
    print(logStr)
    
def print_array(array):
    for item in array:
        print item
        
def print_hash(hash):
    for key, value in hash.items():
        print 'key = %s, value = %s'%(key, value)