# csye6225-fall2017

* Yogita Jain jain.yo@husky.neu.edu 001643815
* Rohan Magare magare.r@husky.neu.edu 001231457
* Pratiksha Shetty shetty.pr@husky.neu.edu 001643697
* Ritesh Gupta gupta.rite@husky.neu.edu 001280361

## Instructions to create an EC2 instance   
   * Navigate to script directory and run the script launch-ec2-instance.sh. The script uses the record.json file for DNS record.

     `./launch-ec2-instance.sh`

### Instruction to delete an EC2 instance
   * Navigate to script directory and run the below command

        `./terminate-ec2-instance.sh <Instance ID>`

        First it will `disable` termination protection on `EC2 instance` and then delete the instance along with the resources
