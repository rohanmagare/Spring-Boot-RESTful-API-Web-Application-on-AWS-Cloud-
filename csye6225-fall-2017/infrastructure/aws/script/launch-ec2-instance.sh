#!/bin/bash

#Yogita Jain, jain.yo@husky.neu.edu, 001643815
#Rohan Magare, magare.r@husky.neu.edu, 001231457
#Pratiksha Shetty, shetty.pr@husky.neu.edu, 001643697
#Ritesh Gupta, gupta.rite@husky.neu.edu, 001280361

set -e


##Get Vpc Id
vpc_id=`aws ec2 describe-vpcs --query "Vpcs[0].VpcId" --output text`
echo "Vpc Id: " $vpc_id


##Create a security group
group_id=`aws ec2 create-security-group --group-name csye6225-fall2017-webapp --description "Csye6225" --vpc-id "$vpc_id"`
echo "Group Id: " $group_id


## Adding Rules to the Security group
`aws ec2 authorize-security-group-ingress --group-id "$group_id" --protocol tcp --port 22 --cidr 0.0.0.0/0`
`aws ec2 authorize-security-group-ingress --group-id "$group_id" --protocol tcp --port 80 --cidr 0.0.0.0/0`
`aws ec2 authorize-security-group-ingress --group-id "$group_id" --protocol tcp --port 443 --cidr 0.0.0.0/0`


##Fetch the subnet id
subnet_id=`aws ec2 describe-subnets --filters "Name=vpc-id, Values=$vpc_id" --query "Subnets[0].SubnetId" --output text`
echo "Subnet Id: " $subnet_id


##Run an ec2 Instance and Fetch Instance Id
ec2_instance_id=`aws ec2 run-instances --image-id ami-cd0f5cb6 --count 1 --instance-type t2.micro --security-group-ids "$group_id" --subnet-id "$subnet_id" --disable-api-termination --block-device-mappings "[{\"DeviceName\":\"/dev/sdf\",\"Ebs\":{\"VolumeSize\":16,\"VolumeType\":\"gp2\",\"DeleteOnTermination\":true}}]" | awk 'NR==2{print $7}'`
echo "Instance Id: " $ec2_instance_id


echo "Waiting for the instance to be up and running"
##Wait for the instance to be up and running
`aws ec2 wait instance-running --instance-ids $ec2_instance_id`


##Fetch the public IP Address of the Instance
ip=`aws ec2 describe-instances --instance-ids "$ec2_instance_id" | grep ASSOCIATION | awk 'NR==1{print $4}'`
echo "Instance Public IP: " $ip


##Parsing the input json file and deleting any other domain name
sed -i 's/"Name":.*/"Name": "",/g' record.json


##Parsing the input json file and deleting any previous IP address
sed -i 's/"Value":.*/"Value": ""/g' record.json


##Parse the input json file and send Public IP of the instance to the file
sed -i '/Value/ s/^\(.*\)\("\)/\1'$ip'\2/' record.json


##Fetch Domain Name
domain_name=`aws route53 list-hosted-zones | awk 'NR==1{print $4}'`
echo "Domain Name: " $domain_name


##Parse the input json file and send Public IP of the instance to the file
sed -i '/Name/ s/^\(.*\)\(",\)/\1'$domain_name'\2/' record.json


##Fetch Hosted Zone ID
host_id=`aws route53 list-hosted-zones | awk 'NR==1{print $3}' | cut --complement -b 1-12`
echo "Host Id: " $host_id


##Add/Update type A resource record set to the domain in the Route 53 zone with the IP of the newly launched EC2 instance
dns=`aws route53 change-resource-record-sets --hosted-zone-id "$host_id" --change-batch file://record.json`
echo $dns


echo "EC2 instance is launched!"
