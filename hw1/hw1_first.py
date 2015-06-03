#!/usr/bin/env python

import sys
import csv
import re

flag_inBody = False
str_title = "null"
str_author = "null"
str_releaseDate = "null"
str_ebookID = "null"
str_language = "null"
list_inBody = []

csv.field_size_limit(sys.maxint)

ebooks_outfile = open('ebook.csv', "wb")
ebooks_writer = csv.writer(ebooks_outfile)
ebooks_writer.writerow(["title", "author", "release_date", "ebook_id", "language", "body"])

tokens_outfile = open('tokens.csv', "wb")
tokens_writer = csv.writer(tokens_outfile)
tokens_writer.writerow(["ebook_id", "token"])

with open(sys.argv[1],"r") as ebooks_infile:

  for readLine in ebooks_infile:
    if flag_inBody == False:
      if len(readLine) >= 34 and readLine[:34] == "*** START OF THE PROJECT GUTENBERG":
        flag_inBody = True
      elif len(readLine) >= 6 and readLine[:6] == "Title:":
        temp_title = readLine[6:len(readLine)-1].strip()
        str_title = temp_title
      elif len(readLine) >= 7 and readLine[:7] == "Author:":
        temp_author = readLine[7:len(readLine)-1].strip()
        str_author = temp_author

      elif len(readLine) >= 13 and readLine[:13] == "Release Date:":
        num_endOfDate = readLine.find('[')
        num_beginOfID = readLine.find('#')
        num_endOfID = readLine.find(']')
        
        str_releaseDate = readLine[13:num_endOfDate].strip()
        str_ebookID = readLine[num_beginOfID+1:num_endOfID].strip()

      elif len(readLine) >= 9 and readLine[:9] == "Language:":
        temp_language = readLine[9:len(readLine)-1].strip()
        str_language = temp_language

    elif flag_inBody == True:
      if len(readLine) >= 33 and readLine[:32] == "*** END OF THE PROJECT GUTENBERG":
        body = "".join(list_inBody)
        ebooks_writer.writerow([str_title, str_author, str_releaseDate, str_ebookID, str_language, body])
        flag_inBody = False
        str_title = "null"
        str_author = "null"
        str_releaseDate = "null"
        str_ebookID = "null"
        str_language = "null"
        list_inBody = []
      else:
        list_inBody.append(readLine)

        #add the appropriate "ebook_id,token" to tokens.csv
        line_onlyAlphaAndSpaces = re.sub('[^a-zA-Z]', " ", readLine)
        list_onlyAlpha = line_onlyAlphaAndSpaces.split()
        if len(list_onlyAlpha) > 0:
          for i in range (0, len(list_onlyAlpha)):
            tokens_writer.writerow([str_ebookID,list_onlyAlpha[i].lower()])

ebooks_outfile.close()
tokens_outfile.close()
