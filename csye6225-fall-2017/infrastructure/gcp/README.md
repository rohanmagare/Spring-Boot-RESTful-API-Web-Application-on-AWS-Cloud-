Creating a VM in Gcloud

## Assumption 

a) A project is already created in GCP.
b) Billing account is attached to the project.
c) Google Cloud SDK is installed.

The below command will create a VM in GCP

`gcloud compute instances create vm1 --image-family ubuntu-1604-lts --image-project ubuntu-os-cloud --boot-disk-size 250GB --boot-disk-type  pd-standard --zone us-east4-a --machine-type n1-standard-1  --description 'Creating a new instance from gcloud'`

To stop VM/instance

`gcloud compute instances stop vm1 --zone us-east4-a`

where vm1 is the instance name

To delete an instance

`gcloud compute instances delete vm1 --zone us-east4-a`



