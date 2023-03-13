# Runtime Trust Evaluation (RTE)
To add the Runtime Trust Evaluation (RTE) component to be part of your service mesh to evaluate participating microservices at runtime, please follow the below steps: 

Please note: to do the same experiment and use RTE for your projects, please make sure to deploy RTE after you deploy your application to your cluster to allow RTE to do the evaluation process. Also make sure prometheus runs on port 9090 or you can change the port and IP in the code and deploy your container. The time that RTE runs is defined by default in 60 min. You can change this in RTE YAML deployment file. 

1. Install kubernetes. We installed (minikube) on a Ubuntu server. You can follow the steps as shown in this URL: https://minikube.sigs.k8s.io/docs/start/ Then run the this command: **minikube start --cpus=4 --memory 4096 --disk-size 32g**
2. Install Istio in your cluster. You can follow the steps as shown in this this URL: https://istio.io/latest/docs/setup/install/ We did install a DEMO profile for the experiment. Please make sure the Istio sidecars injection get enabled by running this command: **kubectl label namespace default istio-injection=enabled**
3. Install the Google Demo app (Online Boutique). You can do this by download the the Online Boutique for the github repository on this URL: https://github.com/GoogleCloudPlatform/microservices-demo Then you can follow as we did by going to the release folder > then run the command: **kubectl apply -f .**
4. Install Prometheus then run the dashboard. You can follow the steps in this URL: https://istio.io/latest/docs/ops/integrations/prometheus/ Make sure the default port is 9090. If you want change this, please make sure to specify this in the RTE code then push your new container image for deployment. Then run the prometheus dashboard at port 9090 by executing this command: **istioctl dashboard prometheus**
5. Install the RTE component as found in the folder here /RTE directory here which contains all the YAML deployment needed as shown below. Please run this command inside the folder: **kubectl apply -f .**
  * RTE deployment. This YAML file includes a call to an image pre-built for DEMO purpose to be used. In the YAML file, you can set the threasholdTimeInMin to allow RTE to collect telemetry after this threshold time. By default it is set to 60min.
  * Envoy filter YAML to capture outbound traffic for each sidecar include for all microservices.
6. **To teste the RTE and only after thresholdTime time passed**, please Remove the emailservice (the legit one) to allow for the malicious service to take place in the next steps. To remove the email service, please run this rommand: **kubectl remove deployment email service.**
7. **To teste the RTE and only after thresholdTime time passed**, install the Emailservice malicious deployment YAML in the /MaliciousEmailService directory to place the new malicious services. Nothing has changed in this code expect to make calls to other services that the email service should not or expect to call at all. To install this, go to /MaliciousEmailService directory then run: **kubectl apply -f .**
  


Helpfull commands: 
- To show the logs for a sidecar, please run this: **kubectl logs pod POD_NAME -c istio-proxy**
- To show the logs for a pod like RTE, please run this: **kubectl logs pod POD_NAME -n istio-system** 

