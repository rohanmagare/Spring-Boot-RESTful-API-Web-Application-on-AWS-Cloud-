#!/bin/sh

#sudo ./awslogs-agent-setup.py -n -r us-east-1 -c awslogs.conf
#echo $?
#sleep 3
#echo "Command Ran"
sudo systemctl stop awslogs.service
sudo mv /home/ubuntu/awslogs.conf /var/awslogs/etc/awslogs.conf
sudo systemctl restart awslogs.service
sudo systemctl enable awslogs.service
