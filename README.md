# Runtime Trust Evaluation (RTE)
To add the Runtime Trust Evaluation (RTE) component to be part of your service mesh to evaluate participating microservices at runtime, please follow the below steps: 

Please note: to do the same experiment and use RTE for your projects, please make sure to deploy RTE after you deploy your application to your cluster to allow RTE to do the evaluation process. Also make sure prometheus runs on port 9090 or you can change the port and IP in the code and deploy your container. The time that RTE runs is defined by default in 60 min. You can change this as well in the code and push your new container image for deployment.

1. Install kubernetes. We installed (minikupbe) on a Ubuntu server. Your can follow the stpes as shown in this URL: https://minikube.sigs.k8s.io/docs/start/
2. Install Istio in your cluster. You can follow the setpes as shown in this this URL: https://istio.io/latest/docs/setup/install/ We did install a DEMO profile for the experiment.
3. Install the Google Demo app (Online Boutique). You can do this by visting the Online Boutique's github repository this URL: https://github.com/GoogleCloudPlatform/microservices-demo Then you can follow as we did by going to the release folder > then run the command: kubectl install -f .
Install Prometheus. You can follow the stpes in this URL: https://istio.io/latest/docs/ops/integrations/prometheus/ Make sure the default port is 9090. If you want change this, please make sure to specify this in the RTE code then push your new container image for deployment.
5. Remove the email service (the legit one) to allow for the malicious service to take place in the next steps. To remove the email service, please run this command: kubectl remove deployment email service. 
6. Install the RTE component as found in the folder here /RTE which contains all the YAML deployment needed as shown below. Please run this command inside the folder: kubectl install -f .
  1. RTE deployment. This YAML file includes a call to an image pre-built for DEMO purpose to be used. You can use it to test RTE. However, if made any change to RTE code, please make sure to edit the RTE YAML (image reference to be yours) file and deploy your image through run a docker build command inside the RTE folder:  docker build rte .
  2. Envoy filter YAML to capture outbound traffic for each sidecar include for all microservices.
  3. Emailservice malicious deployment YAML to place the new malicious services. Nothing has changed in this code expect to make calls to other services that the email service should not or expect to call at all.
  


Helpfull commands: 
- To show the logs for sidecar do this: kubectl logs pod POD_NAME -c istio-proxy 
- To show the logs for a pod like RTE do this please: kubectl logs pod POD_NAME -n istio-system 

