# Runtime Trust Evaluation (RTE)
Adding a Runtime Trust Evaluation (RTE) to be part of the service mesh to evaluate participating microservices at runtime. 

To do the same experiment and use RTE for your projects, please make sure to deploy RTE after you deploy your application to your cluster. Also make sure prometheus runs on port 9090 or you can change the port and IP in the code and deploy your container.


To do the same expirenemtn as the reserach paper, please follow the folliwing steps: 
1. Install kubernetes (minikupbe) on a Ububtue server. Your can follow the stpes in this URL: https://minikube.sigs.k8s.io/docs/start/
2. Install Istio in your cluster. You can follow the setpes in this URL: https://istio.io/latest/docs/setup/install/
3. Install the Google Demo app (Online Boutique) form this URL: https://github.com/GoogleCloudPlatform/microservices-demo
4. Install Promethusus. You can follow the stpes in this URL: https://istio.io/latest/docs/ops/integrations/prometheus/
5. Install the RTE commpeotns as foiund the folder /RTE which contins the followiong: 
  1. RTE depploeyement.
  2. Emailservice deployment to play the role of malcisou services. 
  3. Envoy filtuer to captre outboubd for each sidecar incldude the malsoouc emailsservice. 
  


Helpfull commands: 
- To show the logs for sidecar do this: XxXXXXX
- To show the logs for a pod like RTE do this please: XXXXXXXXX

