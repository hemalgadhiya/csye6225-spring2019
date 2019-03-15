#!/bin/bash

echo "Enter the stack you want to create"
read stackName

echo "enter ec2 deploy tag"
read ec2tag

echo "enter aws account number"
read accno

echo "Username for circleci"
read username

dir_var=$(pwd)
echo "Current Directory is '$dir_var'"
file_dir_var="file://$dir_var/ci-cd.json"

#create Stack
stackId=$(aws cloudformation create-stack --stack-name $stackName --template-body $file_dir_var --capabilities CAPABILITY_NAMED_IAM --parameters ParameterKey="NameTag",ParameterValue=$ec2tag ParameterKey="AccTag",ParameterValue=$accno ParameterKey="circleciusername",ParameterValue=$username --output text)

echo "Stack ID : '$stackId'"

aws cloudformation wait stack-create-complete --stack-name $stackId
echo $stackId

if [ -z $stackId ]; then
	echo 'Error. Stack creation failed !!!'
		exit 1
	else
		echo "Stack Creation Done !!!"
	fi
