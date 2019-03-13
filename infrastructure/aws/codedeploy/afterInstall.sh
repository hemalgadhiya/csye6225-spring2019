#!/bin/bash

sudo systemctl stop tomcat.service

sudo rm -rf *

sudo chown tomcat:tomcat /var/lib/tomcat/webapps/ROOT.war
