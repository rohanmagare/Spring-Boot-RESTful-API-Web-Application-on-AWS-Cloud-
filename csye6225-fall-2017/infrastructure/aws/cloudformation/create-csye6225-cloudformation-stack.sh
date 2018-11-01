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
elif [ $# -gt 1 ]; then
  echo 1>&2 "$0: Too many Arguments"
  exit 2
fi

# add stack name to the security group
#sed -i "s/STACK_NAME/$1/" ec2-parameters.json


##Creating Stack
aws cloudformation create-stack --stack-name "$1" --template-body file://create-ec2-stack.yml --parameters file://ec2-parameters.json
aws cloudformation wait stack-create-complete --stack-name $1
echo "stack $1 is created"
