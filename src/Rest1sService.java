package com.example.RTE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Stream;

import org.apache.log4j.spi.LoggerFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;


@RestController
public class Rest1sService {

	
// This function helps to get all service name. 
public List getAllServiceName () throws IOException {
    List list = new ArrayList();
    
    // For local environment testing purpose, use the below URL
    // URL u = new URL("http://localhost:8021/api/v1/query?query=sum(istio_requests_total)%20by%20(app)");
    
    // For a production environment, call Prometheus directly to its default port 9090 to gather the sum of all istio_requests_total then 
	URL u = new URL("http://prometheus:9090/api/v1/query?query=sum(istio_requests_total)%20by%20(app)");
    try (InputStream in = u.openStream()) {
        InputStreamReader ins = new InputStreamReader(in);
        BufferedReader re = new BufferedReader(ins);
        StringBuilder json_obj = new StringBuilder();
        int c;
        while ((c = re.read()) != -1) {
        	json_obj.append((char) c);
        }
        JSONObject obj ;
    	obj = new JSONObject(json_obj.toString());
        if(obj.getString("status").trim().equalsIgnoreCase("success")) {        	
	        JSONObject obj2 = new JSONObject(obj.get("data").toString());
	        JSONArray obj3 = new JSONArray(obj2.get("result").toString());
            for (int i =0 ; i < obj3.length(); i++ ) {
                JSONObject tem1 = new JSONObject(obj3.get(i).toString());
                JSONObject temp2 = new JSONObject(tem1.get("metric").toString());               
                list.add(temp2.get("app".toString()));

            }
           // list.toString();
	       // JSONObject obj4 = new JSONObject(obj3.get(0).toString());
	       // JSONArray obj5 = new JSONArray(obj4.get("value").toString());

       }
        return list;
    }
}

// Read 
public String FinAllPossiblePaths (String  servicename) throws IOException {
		int http_flag = 0;
		int gprc_flag = 0 ;
		String http_response_in_JSON = "";
		String gPRC_response_in_JSON = "";
	    String destination_service_name_list = "";
	
		Hashtable<String, String > dictAllPossiblePaths = new Hashtable<String, String >();
		
	// Test if this service use gPRC, if Yes: return the response as JSON for later process 
	URL u_gprc = null; 
	// prometheus:9090
	
    // For local environment testing purpose, use the below URL to gather gRPC requests 
	// u_gprc = new URL ("http://localhost:8021/api/v1/query?query=istio_requests_total{destination_workload!=%22"+servicename+"%22,app=%22"+servicename+"%22,grpc_response_status=%220%22,request_protocol=%22grpc%22}"); 
	
    // For a production environment, call Prometheus directly to its default port 9090 to gather gPRCE requests and do the query. 
	u_gprc = new URL ("http://prometheus:9090/api/v1/query?query=istio_requests_total{destination_workload!=%22"+servicename+"%22,app=%22"+servicename+"%22,grpc_response_status=%220%22,request_protocol=%22grpc%22}"); 

	try (InputStream inn = u_gprc.openStream()) {
	        InputStreamReader inss = new InputStreamReader(inn);
	        BufferedReader e = new BufferedReader(inss);
	        StringBuilder json_obj_gPRC = new StringBuilder();
	        int c;
	        while ((c = e.read()) != -1) {
	        	json_obj_gPRC.append((char) c);
	        }
	
	        if (json_obj_gPRC.toString().length() > 70) {
	        	gprc_flag = 1;
	        	gPRC_response_in_JSON = json_obj_gPRC.toString();
	        }
	       
	        
	 }
	 catch(Exception e) {
	
	  }
	
	
	// For local environment testing purpose, use the below URL to gather HTTP requests 
	// URL u_http = new URL ("http://localhost:8021/api/v1/query?query=istio_requests_total{destination_workload!=%22"+servicename+"%22,app=%22"+servicename+"%22,response_code=%22200%22,%20request_protocol=%22http%22}");

    // For a production environment, call Prometheus directly to its default port 9090 to gterh HTTP requests and do the query. 
	 URL u_http = new URL ("http://prometheus:9090/api/v1/query?query=istio_requests_total{destination_workload!=%22"+servicename+"%22,app=%22"+servicename+"%22,response_code=%22200%22,%20request_protocol=%22http%22}");
	 
	 try (InputStream innn = u_http.openStream()) {
	        InputStreamReader insss = new InputStreamReader(innn);
	        BufferedReader e = new BufferedReader(insss);
	        StringBuilder json_obje_HTTP = new StringBuilder();
	        int c;
	        while ((c = e.read()) != -1) {
	        	json_obje_HTTP.append((char) c);
	        }
	        if (json_obje_HTTP.toString().length() > 70) {
	        	http_flag = 1;
	        	http_response_in_JSON = json_obje_HTTP.toString();
	        }
	     
	 }
	 catch(Exception e) {	
	  }
	
	
	// Here if there is a gPRC request found, find all the destination service names to at the end get all possible paths for the giving service. 
	if (gprc_flag == 1) {
	JSONObject obj ;
	obj = new JSONObject(gPRC_response_in_JSON.toString());
	//final_JSON = final_JSON + ":::::::::::::::::::" + gPRC_response_in_JSON;
	if(obj.getString("status").trim().equalsIgnoreCase("success")) {
	  	System.out.println("-----------------------------------------------------------------------------");
	        JSONObject obj2 = new JSONObject(obj.get("data").toString());
	        JSONArray obj3 = new JSONArray(obj2.get("result").toString());
	   //   System.out.println("obj3  JSONArray="+obj3.toString());
	      for (int i =0 ; i < obj3.length(); i++ ) {
	          JSONObject tem1 = new JSONObject(obj3.get(i).toString());
	          JSONObject temp2 = new JSONObject(tem1.get("metric").toString());                
	          destination_service_name_list = destination_service_name_list + temp2.get("destination_service_name").toString() + ",";
	      }
	}
	}
	
	// Here if there is a HTTP request found, find all the destination service names to at the end get all possible paths for the giving service. 
	if (http_flag == 1) {
	JSONObject obj ;
	obj = new JSONObject(http_response_in_JSON.toString());
	   if(obj.getString("status").trim().equalsIgnoreCase("success")) {
	    	obj = new JSONObject(http_response_in_JSON.toString());
	      	System.out.println("-----------------------------------------------------------------------------");
		        JSONObject obj2 = new JSONObject(obj.get("data").toString());
		        JSONArray obj3 = new JSONArray(obj2.get("result").toString());
	          for (int i =0 ; i < obj3.length(); i++ ) {
	              JSONObject tem1 = new JSONObject(obj3.get(i).toString());
	              JSONObject temp2 = new JSONObject(tem1.get("metric").toString());  
	          	  System.out.println("emp2.get(\"destination_service_name\").toString()====="+temp2.get("destination_service_name").toString());
		          destination_service_name_list = destination_service_name_list + temp2.get("destination_service_name").toString() + ",";
	          }
	   }
	
	
	}	
	dictAllPossiblePaths.put( servicename, destination_service_name_list);		
        return dictAllPossiblePaths.get(servicename);
}
	
// This function helps to get in-degree of a service (AIS as explained in the paper). 
public String GetInDegree (String servicename) throws IOException {
	
		int http_flag = 0; // 0 means such service does not use HTTP as a request protocol. Otherwide the service use HTTP as a request protocol. 
		int gprc_flag = 0; // 0 means such service does not use gPRC as a request protocol. Otherwide service use gPRC as a request protocol. 
		String http_response_in_JSON = "";
		String gPRC_response_in_JSON = "";
		int final_JSON = 0;
		int total_inDegree = 0;
				
		URL u_gprc = null; 
		// For local environment testing purpose, use the below URL to gather gPRC requests  
		//u_gprc = new URL ("http://localhost:8021/api/v1/query?query=%20count(istio_requests_total{%20destination_service_name=%22"+servicename+"%22,%20app!=%22"+servicename+"%22,grpc_response_status=%220%22,request_protocol=%22grpc%22})"); 

	    // For a production environment, call Prometheus directly to its default port 9090 to gather gPRCE requests and do the query. 
		u_gprc = new URL ("http://prometheus:9090/api/v1/query?query=%20count(istio_requests_total{%20destination_service_name=%22"+servicename+"%22,%20app!=%22"+servicename+"%22,grpc_response_status=%220%22,request_protocol=%22grpc%22})"); 

		try (InputStream inn = u_gprc.openStream()) {
		        InputStreamReader inss = new InputStreamReader(inn);
		        BufferedReader e = new BufferedReader(inss);
		        StringBuilder json_obj_gPRC = new StringBuilder();
		        int c;
		        while ((c = e.read()) != -1) {
		        	json_obj_gPRC.append((char) c);
		        }
		        if (json_obj_gPRC.toString().length() > 70) {
		        	gprc_flag = 1;
		        	gPRC_response_in_JSON = json_obj_gPRC.toString();
		        }
		        
		 }
		 catch(Exception e) {
	      }
		
		// For local environment testing purpose, use the below URL to gather HTTP requests  
		//URL u_http = new URL ("http://localhost:8021/api/v1/query?query=count(istio_requests_total{destination_service_name=%22"+servicename+"%22,%20app!=%22"+servicename+"%22,response_code=%22200%22,%20request_protocol=%22http%22})");

	    // For a production environment, call Prometheus directly to its default port 9090 to gather gPRCE requests and do the query. 
		URL u_http = new URL ("http://prometheus:9090/api/v1/query?query=count(istio_requests_total{destination_service_name=%22"+servicename+"%22,%20app!=%22"+servicename+"%22,response_code=%22200%22,%20request_protocol=%22http%22})");
		 try (InputStream innn = u_http.openStream()) {
		        InputStreamReader insss = new InputStreamReader(innn);
		        BufferedReader e = new BufferedReader(insss);
		        StringBuilder json_obje_HTTP = new StringBuilder();
		        int c;
		        while ((c = e.read()) != -1) {
		        	json_obje_HTTP.append((char) c);
		        }
		        if (json_obje_HTTP.toString().length() > 70) {
		        	http_flag = 1;
		        	http_response_in_JSON = json_obje_HTTP.toString();
		        }
		 }
		 catch(Exception e) {
	      }
		
	
	
	if (gprc_flag == 1) {
		JSONObject obj ;
    	obj = new JSONObject(gPRC_response_in_JSON.toString());
        if(obj.getString("status").trim().equalsIgnoreCase("success")) {
       	 	//System.out.println("gPRC_response used as a protcol for the service: " + servicename);
	        JSONObject obj2 = new JSONObject(obj.get("data").toString());
	        JSONArray obj3 = new JSONArray(obj2.get("result").toString());
	        JSONObject obj4 = new JSONObject(obj3.get(0).toString());
	        JSONArray obj5 = new JSONArray(obj4.get("value").toString());
	        total_inDegree =total_inDegree + Integer.parseInt(obj5.get(1).toString());  	
       }
	    }
	
	if (http_flag == 1) {

		JSONObject obj ;
    	obj = new JSONObject(http_response_in_JSON.toString());
        if(obj.getString("status").trim().equalsIgnoreCase("success")) {
	        JSONObject obj2 = new JSONObject(obj.get("data").toString());
	        JSONArray obj3 = new JSONArray(obj2.get("result").toString());
	        JSONObject obj4 = new JSONObject(obj3.get(0).toString());
	        JSONArray obj5 = new JSONArray(obj4.get("value").toString());
	        total_inDegree = total_inDegree + Integer.parseInt(obj5.get(1).toString());  	
       }
	
}
	return total_inDegree + "";
}
// This function helps to return the out-degree for a service (ADS) as explained in the paper. 
public String GetOutDegree (String servicename) throws IOException {

		int http_flag = 0; // 0 means such service does not use HTTP as a request protocol. 
		int gprc_flag = 0; // 0 means such service does not use gPRC as a request protocol.
		String http_response_in_JSON = "";
		String gPRC_response_in_JSON = "";
		int final_JSON = 0;
		int total_outDegree = 0;
	
		 
			
		// Test if this service use gPRC, if Yes: return the response as JSON for later process 
		URL u_gprc = null; 
		
		// For local environment testing purpose, use the below URL to gather gPRC requests  
		//u_gprc = new URL ("http://localhost:8021/api/v1/query?query=count(istio_requests_total{destination_service_name!=%22"+servicename+"%22,source_app=%22"+servicename+"%22,app!=%22"+servicename+"%22,grpc_response_status=%220%22,request_protocol=%22grpc%22})");

	    // For a production environment, call Prometheus directly to its default port 9090 to gather gPRCE requests and do the query. 
		u_gprc = new URL ("http://prometheus:9090/api/v1/query?query=count(istio_requests_total{destination_service_name!=%22"+servicename+"%22,source_app=%22"+servicename+"%22,app!=%22"+servicename+"%22,grpc_response_status=%220%22,request_protocol=%22grpc%22})");
		try (InputStream inn = u_gprc.openStream()) {
		        InputStreamReader inss = new InputStreamReader(inn);
		        BufferedReader e = new BufferedReader(inss);
		        StringBuilder json_obj_gPRC = new StringBuilder();
		        int c;
		        while ((c = e.read()) != -1) {
		        	json_obj_gPRC.append((char) c);
		        }
		        if (json_obj_gPRC.toString().length() > 70) {
		        	gprc_flag = 1;
		        	gPRC_response_in_JSON = json_obj_gPRC.toString();
		        }
		 }
		catch(Exception e) {
	      }
		
		
		// Test if this service use HTTP, if Yes: return the response as JSON for later process 
		// For local environment testing purpose, use the below URL to gather HTTP requests  
		// URL u_http = new URL ("http://localhost:8021/api/v1/query?query=count(istio_requests_total{source_app=%22"+servicename+"%22,destination_service_name!=%22"+servicename+"%22,app!=%22"+servicename+"%22,response_code=%22200%22,request_protocol=%22http%22})");
 
	    // For a production environment, call Prometheus directly to its default port 9090 to gather gPRCE requests and do the query. 
		URL u_http = new URL ("http://prometheus:9090/api/v1/query?query=count(istio_requests_total{source_app=%22"+servicename+"%22,destination_service_name!=%22"+servicename+"%22,app!=%22"+servicename+"%22,response_code=%22200%22,request_protocol=%22http%22})");

		 try (InputStream innn = u_http.openStream()) {
		        InputStreamReader insss = new InputStreamReader(innn);
		        BufferedReader ee = new BufferedReader(insss);
		        StringBuilder json_obje_HTTP = new StringBuilder();
		        int c;
		        while ((c = ee.read()) != -1) {
		        	json_obje_HTTP.append((char) c);
		        }

		        if (json_obje_HTTP.toString().length() > 70) {
		        	http_flag = 1;
		        	http_response_in_JSON = json_obje_HTTP.toString();
		        }
		 }
			catch(Exception e) {
	      }
	
	if (gprc_flag == 1) {
		JSONObject obj = new JSONObject(gPRC_response_in_JSON.toString());
        if(obj.getString("status").trim().equalsIgnoreCase("success")) {
	        JSONObject obj2 = new JSONObject(obj.get("data").toString());
	        JSONArray obj3 = new JSONArray(obj2.get("result").toString());
	        JSONObject obj4 = new JSONObject(obj3.get(0).toString());
	        JSONArray obj5 = new JSONArray(obj4.get("value").toString());
       	 total_outDegree =total_outDegree + Integer.parseInt(obj5.get(1).toString());  	
       }
	    }
	
	if (http_flag == 1) {

		JSONObject obj ;
    	obj = new JSONObject(http_response_in_JSON.toString());
        if(obj.getString("status").trim().equalsIgnoreCase("success")) {
	        JSONObject obj2 = new JSONObject(obj.get("data").toString());
	        JSONArray obj3 = new JSONArray(obj2.get("result").toString());
	        JSONObject obj4 = new JSONObject(obj3.get(0).toString());
	        JSONArray obj5 = new JSONArray(obj4.get("value").toString());
       	 total_outDegree = total_outDegree + Integer.parseInt(obj5.get(1).toString());  	
       }
	
}
	return total_outDegree +"";	    
}

// This function helps to return the price that a malicious service has to pay. 
public Double getPrice(String Microservice_name ) {
	
	// Price variable 
	double price = 0.0;
	
	// Find at runtime the value of ACS_current for the giving Microservice_name at runtime 
	double ACS_current = GetACS_current(Microservice_name);
	
	// Find at runtime the values of MinACS and MaxACS from all microservices
	List MinMax = minMaxACS();
	double ACSmin = 0.00;
	double ACSmax = 0.00;
	ACSmin = Double.valueOf(MinMax.get(0).toString());
	ACSmax = Double.valueOf(MinMax.get(1).toString());
	System.out.println("getPrice():::::: The ACSmin:"+ACSmin +" tand the ACSmax equals="+ACSmax);

	// Apply the rule of the organization 
	if (ACS_current == ACSmax) {
		price= 0.9;
	}
	else if (ACSmin < ACS_current && ACS_current < ACSmax) {
		price= 0.5;
	}
	else if (ACS_current == ACSmin) {
		price= 0.2;
	}

	// Return the price
	return price;
}

// Return the current ACS value for the giving microservice name  
public double GetACS_current(String Microservice_name ) {
	String strFile = "Metrics.csv";
    String[] nextRecord;
    double ACS_current = 0.0;
	 try {
	        CSVReader reader = new CSVReader(new FileReader(strFile));
	        while ((nextRecord = reader.readNext()) != null) {
	        	try {	 
		      	if (nextRecord[0].toString().equals(Microservice_name)) {
				        	ACS_current = Double.valueOf(nextRecord[3].toString());
			        	}
	        		}
	        		
	        	catch (Exception e) {
	        	}	         
	        }
	     }catch(IOException ie) {
	        ie.printStackTrace();
	     }
		return ACS_current;
}


// This REST API is opens to allow the Istio sidecar to call it to find new trust value for a malicious service. 
@RequestMapping("/DetermineNewTrustLevelforMaliciousMicroservice/{MaliciousMicroservice}/{TargetMicroservice}")
public Double DetermineNewTrustLevelforMaliciousMicroservice(@PathVariable(value = "MaliciousMicroservice") String MaliciousMicroservice,  @PathVariable(value = "TargetMicroservice") String TargetMicroservice ) {


	double newTrustValue = 0.0;
	
	// Go get the price of the TargetMicroservice and return the priceToPay
	double priceToPay = getPrice(TargetMicroservice);
	System.out.println("DetermineNewTrustLevelforMaliciousMicroservice():::::: The price for:"+TargetMicroservice +" that has to pay equals="+priceToPay);
	
	// Go get the trust value (current) for the MaliciousMicroservice and return the currentTrustValue
	double currentTrustValue = GetTrustValue_current(MaliciousMicroservice);
	System.out.println("DetermineNewTrustLevelforMaliciousMicroservice()::::: The trsut value for:"+MaliciousMicroservice +" equals="+currentTrustValue);

	// Now, update the trust value for the MaliciousMicroservice to through decreasing currentTrustValue by the price: priceToPay*100. If the currentTrustValue == 0.0, no need to go negative. Just put 0.0
    int currentTrustValue_ = (int)currentTrustValue;
	if (currentTrustValue_ > 0) {
		newTrustValue = currentTrustValue - ((currentTrustValue) * (priceToPay));
		System.out.println("DetermineNewTrustLevelforMaliciousMicroservice()::::::The new trust value for:"+MaliciousMicroservice +" equals="+newTrustValue);
	}
	else {
		newTrustValue = 0.0;
	}
	
	// the below code will open the registry file  Metrics.txt to look for the Malicious service and replace it with the new trust value  
	String strFile = "Metrics.csv";
	String[] nextRecord;
	double TrustValue_current = 0.0;
	 try {
	        CSVReader reader = new CSVReader(new FileReader(strFile));
	        // To know the row of the Malicious service to update the cell of the trust value 
	        int rowLine = 0;
	        while ((nextRecord = reader.readNext()) != null) {
	        	try {	
	        	// If the service found, do the process to replace the trust value 
		      	if (nextRecord[0].toString().equals(MaliciousMicroservice)) {
		      	 CSVReader reader2 = new CSVReader(new FileReader(strFile));
		 	     List<String[]> body = reader2.readAll();
		 	     body.get(rowLine)[4] = Double.toString(newTrustValue) ;
		 	     reader.close();
		 	     CSVWriter writer = new CSVWriter(new FileWriter(strFile));
		 	     writer.writeAll(body);
		 	     writer.flush();
		 	     writer.close();
			        	}
	        		}
	        	catch (Exception e) {
	        	}	
	        	rowLine++;
	        }
	     }catch(IOException ie) {
	        ie.printStackTrace();
	     }
	    System.out.println("DetermineNewTrustLevelforMaliciousMicroservice():::::: New trust value="+newTrustValue + " For MS:"+MaliciousMicroservice);
	return newTrustValue;
}

// This function helps to get the the current trust value in the registry
public double GetTrustValue_current(String Microservice_name ) {
	String strFile = "Metrics.csv";
	String[] nextRecord;
	double TrustValue_current = 0.0;
	 try {
	        CSVReader reader = new CSVReader(new FileReader(strFile));
	        while ((nextRecord = reader.readNext()) != null) {
	        	try {	 
		      	if (nextRecord[0].toString().equals(Microservice_name)) {
				        	TrustValue_current = Double.valueOf(nextRecord[4].toString());
			        	}
	        		}
	        		
	        	catch (Exception e) {
	        		System.out.println(e.getMessage());
	        	}	         
	        }
	     }catch(IOException ie) {
	        ie.printStackTrace();
	     }
	    System.out.println("GetTrustValue_current():::::: TrustValue_current="+TrustValue_current + " For MS:"+Microservice_name);
		return TrustValue_current;
}


// This function helps to find the min and max of ACS from all the microservices 
public List minMaxACS () {
	String strFile = "Metrics.csv";
    String[] nextRecord;
    Double minACS = 1000000.0;
    Double maxACS = 0.0;
    Double temp = 0.0;
     
    List<String> minMax = new ArrayList<String>();
       
   minMax.add(minACS.toString());
   minMax.add(maxACS.toString());
	 try {
		 	// Read from the CSV all the ACS values then find the min and max among them. 
	        CSVReader reader = new CSVReader(new FileReader(strFile));
	        while ((nextRecord = reader.readNext()) != null) {
	        	try {
	        			temp=Double.valueOf(nextRecord[3].toString());
	        			minACS=Double.valueOf(minMax.get(0));
	        			maxACS=Double.valueOf(minMax.get(1));
			        	if (temp > maxACS) {
			        		maxACS = temp;
			        		minMax.set(1,temp.toString());
			        	}
			        	if (temp < minACS) {
			        		minACS = temp;
			        		minMax.set(0,temp.toString());
			        	}
	        		}
	        	catch (Exception e) {
	        		System.out.println(e.getMessage());
	        	}
	        }
	     }catch(IOException ie) {
	        ie.printStackTrace();
	     }
		return minMax;
}


//  A REST API to allow me to test if RTE works or not in the cluster. You can remove it if you do not need it. 
@RequestMapping("/test")
public String say() {
	System.out.println("Called test ---------------------------It is works");
	return "hi, it works!";
}

// A REST API to allow me to test if prometheus works or not in the cluster. You can remove it if you do not need it. 
@RequestMapping("/checkprometheus")
public String checkprometheus() throws IOException {
	System.out.println("checkprometheus ::::::::::: checkprometheus::::::::::::::::::checkprometheus::::::::::::::::::: start");
	URL u = new URL("http://prometheus:9090/api/v1/query?query=sum(istio_requests_total)");
    try (InputStream in = u.openStream()) {
        InputStreamReader ins = new InputStreamReader(in);
        BufferedReader re = new BufferedReader(ins);
        StringBuilder json_obj = new StringBuilder();
        int c;
        while ((c = re.read()) != -1) {
        
        	json_obj.append((char) c);
        }
        //System.out.println("Service JSON ="+json_obj.toString());
        JSONObject obj ;
    	obj = new JSONObject(json_obj.toString());
        return json_obj.toString();
    }
}


// This REST API helps istio sidecars outbound to check if the request from caller service to the target service is legit? 
@RequestMapping("/IsItAnunauthorizedAccess/{MaliciousMicroservice}/{TargetMicroservice}")
public int IsItAnunauthorizedAccess(@PathVariable(value = "MaliciousMicroservice") String MaliciousMicroservice,  @PathVariable(value = "TargetMicroservice") String TargetMicroservice ) {
	 String Str = new String(TargetMicroservice);
     String temp [] = null; 
    String[] nextRecord;
	String strFile = "Metrics.csv";
	List<String> Allservice = null; 
	
	// This is the initial assumption that this access is not allow; otherwise set to true means it is safe
	int IsItAnunauthorizedAccess = 0; // not found: Malicious activity 
	try {
	        CSVReader reader = new CSVReader(new FileReader(strFile));
	        while ((nextRecord = reader.readNext()) != null) {
	        	try {	 
		      	if (nextRecord[0].toString().equals(MaliciousMicroservice)) {
				        	Allservice = new ArrayList<String>(Arrays.asList( nextRecord[5].split(",")));
				        	if (Allservice.size() > 0) {
				        		for (int i=0 ;i <Allservice.size(); i ++) {
				        			if (Allservice.get(i).toString().equalsIgnoreCase(TargetMicroservice)) {
				        			    IsItAnunauthorizedAccess = 1; // found path. it is not a Malicious activity
				        			}
				        			
				        		}
				        		
				        	}
			        	}
	        		}
	        		
	        	catch (Exception e) {
	        		IsItAnunauthorizedAccess = 3; // error can not tell if it is Malicious activity or not 
	        	}	         
	        }
	     }catch(IOException ie) {
	    	 IsItAnunauthorizedAccess = 3; // error can not tell if it is Malicious activity or not 
	     }

	System.out.println("IsItAnunauthorizedAccess():::::::::::: IsItAnunauthorizedAccess="+IsItAnunauthorizedAccess);
	return IsItAnunauthorizedAccess;
}



// This REST API or as function can be called manually or by the RET after the timeframe as defined in the 
@GetMapping(value="/ACS_baselinePhase")
public String ACS_baselinePhase () throws IOException {
	System.out.println("---------------------- ACS_baselinePhase-------------------------- start");
	String in_degree_string = "";
	String out_degree_string = "";
	String ACS_string = "";
	String status = "";
	
	int out_degree = 0;
	int in_degree = 0;
	int ACS_in = 0;
		File file= new File (""+"Metrics.csv");
		FileWriter out_file;
	
	   if (file.exists()== false) {
		   status = "Metrics.csv file does not exists() >> creating the file with all ACS values for all microservice";
		   	file.createNewFile();
		    out_file = new FileWriter(file);
		    CSVWriter writer = new CSVWriter(out_file);
		   	String[] header_ = { "Microservice_name", "AIS", "ADS", "ACS", "TM", "CanAccess" };
		    writer.writeNext(header_);
		    
		    List list = getAllServiceName();
		    for (int i = 0 ; i < list.size(); i++) {
			 out_degree_string = GetOutDegree(list.get(i).toString());
			 out_degree = Integer.parseInt(out_degree_string);
			 in_degree_string = GetInDegree(list.get(i).toString());
			 in_degree = Integer.parseInt(in_degree_string);			 
			 String destination_service_name_list  = FinAllPossiblePaths(list.get(i).toString());  
			 ACS_in = out_degree * in_degree;
			 ACS_string = ACS_in + "";
			 String[] tem_data = { list.get(i).toString() , in_degree_string, out_degree_string , ACS_string , "100", destination_service_name_list.toString() };
			 writer.writeNext(tem_data);
		    									}
		     writer.close();
	   }
	   else {
		   status = "Metrics.csv file is exists(). This file can be called once after a specific defined time.";
	   }		
		System.out.println("---------------------- ACS_baselinePhase-------------------------- end");

	return status;
}
}




