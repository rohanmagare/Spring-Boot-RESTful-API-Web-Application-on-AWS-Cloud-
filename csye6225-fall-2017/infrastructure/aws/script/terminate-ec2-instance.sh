#!/bin/bash

#Yogita Jain, jain.yo@husky.neu.edu, 001643815
#Rohan Magare, magare.r@husky.neu.edu, 001231457
#Pratiksha Shetty, shetty.pr@husky.neu.edu, 001643697
#Ritesh Gupta, gupta.rite@husky.neu.edu, 001280361

set -e

##Check if enough arguements are passed
if [ $# -lt 1 ]; then
  echo 1>&2 "$0: Instance Id not provided"
  exit 2
elif [ $# -gt 1 ]; then
  echo 1>&2 "$0: Too many Arguments"
  exit 2
fi

##Disabling instance termination protection
aws ec2 modify-instance-attribute --instance-id $1 --no-disable-api-termination

##Fetching Security Group Id
sg_id=`aws ec2 describe-instances --instance-ids $1 | grep SECURITYGROUPS | awk '{print $2}'`
#echo $sg_id

##Terminating the instance
aws ec2 terminate-instances --instance-ids $1

echo "Waiting for the instance to get terminated"

##Waiting for the instance to get terminated
aws ec2 wait instance-terminated --instance-ids $1

echo "EC2 instance deleted!"

##Deleting the security group
aws ec2 delete-security-group --group-id "$sg_id"

echo "Security group deleted!"
