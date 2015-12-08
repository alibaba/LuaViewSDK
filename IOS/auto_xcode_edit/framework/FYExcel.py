#!/user/bin/python
#/**********************
# filename:FYExcel.py
# owner: fangying
# tel: 15810128006
#***********************/

import os

import xlrd
import xlwt
import random
from xlutils.copy import copy

from sys import argv

version = '1.0';

def model_name():
    return __name__ #return this model name


def read(excel_path):
    xls_handler=xlrd.open_workbook(excel_path)
    try:
        sheet1_obj=xls_handler.sheet_by_name("sheet1")
    except:
        print("not find xlsx file")
        return

    print "sheet have %d line, %d col." % (sheet1_obj.nrows, sheet1_obj.ncols);


    for row in range(sheet1_obj.nrows):
        for col in range(sheet1_obj.ncols):
            try:

                #print row," ",col , "  "  ,print sheet1_obj.cell(row,col)

                if sheet1_obj.cell(row, col) != None:
                    temp=sheet1_obj.cell(row, col).value
                    print(type(temp))
                    print(temp)
            except Exception, e:
                break


def create(excel_path, sheet_name):
    file = xlwt.Workbook()
    sheet= file.add_sheet(sheet_name)
    file.save(excel_path)
    print "Done -- create " + excel_path


def write(excel_path, sheet_name, data):
    print os.path.exists(excel_path)


    if os.path.exists(excel_path):
       os.remove(excel_path)

    #create a new xls
    file = xlwt.Workbook()

    #create a new sheet
    sheet = file.add_sheet(sheet_name)

    #write data
    index = 0
    for one in data:
        if index == 0:
            sheet.write(0, 0, 'class name')
            sheet.write(0, 1, 'owner')
            sheet.write(0, 2, 'team name')
            sheet.write(0, 3, 'leader')
        sheet.write(index + 1, 0, one)
        index = index + 1

    file.save(excel_path)


