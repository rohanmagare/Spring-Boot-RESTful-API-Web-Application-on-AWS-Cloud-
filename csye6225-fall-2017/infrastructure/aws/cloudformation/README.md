# csye6225-fall2017

* Yogita Jain jain.yo@husky.neu.edu 001643815
* Rohan Magare magare.r@husky.neu.edu 001231457
* Pratiksha Shetty shetty.pr@husky.neu.edu 001643697
* Ritesh Gupta gupta.rite@husky.neu.edu 001280361

### Instructions to create an EC2 instance, RDS instance, DNS record, DynamobDB, S3 bucket, Roles, Policies, Security groups using cloud formation stack

### Files
  * Parameters files: ec2-parameters.json and IAM-parameters.json
  * Templates: create-ec2-stack.yml and IAM-roles-policies-profile.yml
  * Shell scripts: create-csye6225-cloudformation-stack.sh and delete-csye6225-cloudformation-stack.sh

### Parameters
  * The parameters are added in the `ec2-parameters.json`
  * The `create-ec2-stack.yml` contains the cloud formation stack to create an EC2 instance, RDS instance, security groups EC2 and RDS instance, DNS record, DynamobDB and S3 bucket
  * The `create-csye6225-cloudformation-stack.sh` contains aws command to creates roles, policies and profles and a stack with above mentioned resources.
  * The `delete-csye6225-cloudformation-stack.sh` contains aws command to delete the stack and associated resources with it.

 Change ParameterValue for the following:
  * `KeyName`: put for EC2Keypair
  * `SubnetId`: your subnet id
  * `VpcId`: your VpcId
  * `HostedZoneId`: hosted zone id created for the domain
  * `DomainName`: domain name created in route 53
  * `CodeDepolyRoleName`: name of the role used for code deploy
  and other mentioned parameters in the `ec2-parameters.json` and `IAM-parameters.json` files.

### Instruction to create a stack
  * Navigate to cloudformation directory and run the below command

    `cd infrastructure/aws/cloudformation/`

    `./create-csye6225-cloudformation-stack.sh rolestack ec2stack`

   The above command creates two separate stacks, one for IAM roles, policies and profiles and another stack to creates other AWS resources. `The instance profile created in rolestack which will be imported in ec2stack`


### Instruction to delete a stack
   * Navigate to cloudformation directory and run the below command

        `cd infrastructure/aws/cloudformation/`

        `./delete-csye6225-cloudformation-stack.sh ec2stack rolestack`

        `NOTE: since ec2stack contains a value that is imported from rolestack template, first ec2stack template should be deleted and then the rolestack template`

        The script will `disable` termination protection on `EC2 instance` and then delete the stack along with the resources
