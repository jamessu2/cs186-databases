#!/usr/bin/env python

import sys
import csv

list_names = []

with open("popular_names.txt","r") as popular_names_infile:

  for readName in popular_names_infile:
    list_names.append(readName.strip().lower())


flag_header = False
flag_firstLine = False

token_counts_outfile = open('token_counts.csv', "wb")
token_counts_writer = csv.writer(token_counts_outfile)
token_counts_writer.writerow(["token", "count"])

name_counts_outfile = open('name_counts.csv', "wb")
name_counts_writer = csv.writer(name_counts_outfile)
name_counts_writer.writerow(["token", "count"])

with open(sys.argv[1],"r") as tokens_infile:

  prevLine = ""
  count = 0

  for nextLine in tokens_infile:
    if flag_header == False:
      flag_header = True

    elif flag_firstLine == False:
      flag_firstLine = True
      count = 1

    else:
      if prevLine == nextLine:
        count = count + 1

      elif prevLine != nextLine and prevLine.strip().split(',')[1] == nextLine.strip().split(',')[1]:
        count = count + 1

      elif prevLine != nextLine:
        prevLine_tokenValue = prevLine.strip().split(',')[1]
        token_counts_writer.writerow([prevLine_tokenValue, count])
        for i in range(0,len(list_names)):
          if prevLine_tokenValue == list_names[i]:
            name_counts_writer.writerow([prevLine_tokenValue, count])
            break

        count = 1

    prevLine = nextLine

  prevLine_tokenValue = prevLine.strip().split(',')[1]
  token_counts_writer.writerow([prevLine_tokenValue, count])
  for i in range(0,len(list_names)):
    if prevLine_tokenValue == list_names[i]:
      name_counts_writer.writerow([prevLine_tokenValue, count])
      break

token_counts_outfile.close()
name_counts_outfile.close()
