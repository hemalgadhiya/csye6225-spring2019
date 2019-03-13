# # Create AWS CFN Stack - wrapper for `aws cloudformation create-stack`
# #Example Usage:
# [ $# -eq 0 ] && { echo -e "\nUsage `basename $0` <stack name> <CFN template file> <JSON parameters file>\n\nExample:\n`basename $0` mystackname MyCfn.template stackParams.json stackTags.json\n"; exit 1; }

# #Inputs

# stackName=${1?param missing - Stack Name}
# templateFile=${2?param missing - Template File}
# paramsFile=${3?param missing - Json Parameters file}
# #tagsFile=${4?param missing - Json Tags file}

# if [ $# -gt 3 ]; then
# 	echo 1>&2 "$0: too many arguments"
# 	exit 1

# fi

#!/bin/bash
#*****************************
#    AWS VPC & Public Subnet Retrieve Shell Script
#*****************************

aws cloudformation list-stacks --stack-status-filter CREATE_COMPLETE||CREATE_IN_PROGRESS||REVIEW_IN_PROGRESS||DELETE_IN_PROGRESS||DELETE_FAILED||UPDATE_IN_PROGRESS||DELETE_COMPLETE

echo "Enter the stack you want to create"
read Stack_Name

# Dhanisha, Here I'm ASSUMING that $1 is the STACK NAME
# $vpc would be the actual name of the VPC created.
# Retrieving VPC Name


# PART 1 - FINDING VPC NAME AND VPC ID
# echo -e "\n"
# echo "**RETRIEVE VPC NAME**"
# vpc="$1-csye6225-vpc-1"
# vpcname=$(aws ec2 describe-vpcs \
# 	--query "Vpcs[?Tags[?Key=='Name']|[?Value=='$vpc']].Tags[0].Value" \
# 	--output text)
# echo $vpcname

# Retrieving VPC ID
#echo -e "\n"
#echo "**SHOW VPC ID**"
#aws ec2 describe-vpcs

#echo "These are the VPC IDs you have!"
#echo "Which one do you want!"
#read vpc_id
#echo "VPC ID is '$vpc_id'"

# PART 2 - FINDING SUBNETS
#echo -e "\n"
#echo "**SHOW SUBNETS**"
#aws ec2 describe-subnets

#echo "These are the Subnets you have!"
#echo "Which one do you want!"
#read PUBLIC_SUBNET_CHOSEN
#echo "You chose '$PUBLIC_SUBNET_CHOSEN'."

# PART 4 - VPC NAME, VPC ID and PUBLIC SUBNET DISPLAY
#echo -e "\n"
#echo "VPC Name is '$vpcname', VPC ID is '$vpc_id' and the public subnet you chose is '$PUBLIC_SUBNET_CHOSEN'."


echo "Displaying all keys!"
aws ec2 describe-key-pairs
echo -e "\n"
echo "Choose 1 Key which you want to use!"
read KEY_CHOSEN

echo "Displaying AMI!"
aws ec2 describe-images --owners self --query 'Images[*].{ID:ImageId}'
echo -e "\n"
echo "Enter AMI ID"
read amiId

#echo -e "\n"
#echo "Enter S3 Bucket Name ie your domain Name, csye6225-fall2018-huskyid.me"
#read S3_Bucket

#echo -e "\n"
#echo "Please enter DBSubnet Group Name."
#read DBSubnet_Group

#Functions
#-------------------------------------------------------------------------------
# Retrieve the status of a cfn stack
# Args:
# $1  The name of the stack
#-------------------------------------------------------------------------------

getStackStatus() {
	aws cloudformation describe-stacks \
		--stack-name $Stack_Name \
		--query Stacks[].StackStatus \
		--output text
}

#-------------------------------------------------------------------------------
# Waits for a stack to reach a given status. If the stack ever reports any
# status other thatn *_IN_PROGRESS we will return failure status, as all other
# statuses that are not the one we are waiting for are considered terminal
# Args:
# $1  Stack name
# $2  The stack status to wait for
#-------------------------------------------------------------------------------

waitForState() {
	local status

	status=$(getStackStatus $1)

	while [[ "$status" != "$2" ]]; do

		echo "Waiting for stack $1 to obtain status $2 - Current status: $status"
		# If the status is not one of the "_IN_PROGRESS" status' then consider

		# this an error
		if [[ "$status" != *"_IN_PROGRESS"* ]]; then
			exitWithErrorMessage "Unexpected status '$status'"
		fi

		status=$(getStackStatus $1)
		sleep 30

	done

	echo "Stack $1 obtained $2 status"

}


exitWithErrorMessage() {
	echo "ERROR: $1"
	exit 1
}
#-------------------------------------------------------------------------------
# Returns a file URL for the given path
#
# Args:
# $1  Path
#-------------------------------------------------------------------------------

dir_var=$(pwd)
# echo "Current Directory is '$dir_var'"
file_dir_var="file://$dir_var/application.json"

#Create Stack

aws cloudformation create-stack \
	--stack-name $Stack_Name  \
	--template-body $file_dir_var \
	--parameters ParameterKey="keyname",ParameterValue=$KEY_CHOSEN ParameterKey="AmiId",ParameterValue=$amiId ParameterKey="NameTag",ParameterValue="ec2" \
	--disable-rollback

# aws cloudformation create-stack \
# 	--stack-name $1 \
# 	--template-url $2 \
# 	--parameters $3 \
# 	--disable-rollback


if ! [ "$?" = "0" ]; then

	exitWithErrorMessage "Cannot create stack ${Stack_Name}!"

fi

#Wait for completion
waitForState ${Stack_Name} "CREATE_COMPLETE"
