#!/usr/bin/python

#-----------------------------------------------------------------
# @author: ianjuch
# @file: config.py
# @description: crawler config file
#-----------------------------------------------------------------

#special_filetypes=".csv"

#stats_to_collect="date","time","size"

# TODO: path currently relative to test_config directory, does this make sense?
directories_to_crawl=["../test_source/"]
schedule="0/30 * * * * ?" #cron schedule
data_mover_location="ec2-user@54.69.1.154:~/cs286A/dataMover/kafka/incoming"
