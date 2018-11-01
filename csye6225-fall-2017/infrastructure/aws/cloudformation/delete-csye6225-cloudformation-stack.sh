#!/bin/bash

#Yogita Jain, jain.yo@husky.neu.edu, 001643815
#Rohan Magare, magare.r@husky.neu.edu, 001231457
#Pratiksha Shetty, shetty.pr@husky.neu.edu, 001643697
#Ritesh Gupta, gupta.rite@husky.neu.edu, 001280361


set -e
##Check if enough arguements are passed
if [ $# -lt 1 ]; then
  echo 1>&2 "$0: Stack name not provided"
  exit 2
elif [ $# -gt 2 ]; then
  echo 1>&2 "$0: Too many Arguments"
  exit 2
fi

# fetch instance id
instanceid=`aws ec2 describe-instances --query "Reservations[*].Instances[*].InstanceId" --filters "Name=tag-key,Values=aws:cloudformation:stack-name" "Name=tag-value,Values=$1" --output text`
echo "$instanceid"
## Disbale instance terminatio protection
aws ec2 modify-instance-attribute --instance-id "$instanceid" --no-disable-api-termination

# replace stack name with generic name
sed -i "s/$2/STACK_NAME/g" ec2-parameters.json

aws cloudformation delete-stack --stack-name $2
aws cloudformation wait stack-delete-complete --stack-name $2
echo "Stack $2 deleted!"
