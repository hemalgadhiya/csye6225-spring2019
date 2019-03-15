#!/bin/bash

cd /home/centos/webapp
sudo chown -R centos:centos /home/centos/webapp/*
sudo chmod +x webApp-0.0.1-SNAPSHOT.jar
source /etc/profile.d/envvariable.sh
kill -9 $(ps -ef|grep webApp | grep -v grep | awk '{print$2}') 
nohup java -jar webApp-0.0.1-SNAPSHOT.jar > /dev/null 2> /dev/null < /dev/null &	
