package com.example.RTE;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.lang.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RTEApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(RTEApplication.class, args);
		System.out.println("RTE v1.0 ");
		
		// In the Baseline Phase, the first process: captureTelemetryData has to collect the telemetry data in a timeframe. Here is the timeframe. You can set it to as you need. It will be read from the YAML file. 
		// Here we set it in 60min by default. 
	    int timeToExecuteTheBaselinePhase = Integer.parseInt(System.getenv().getOrDefault("thresholdTimeInMins", "60"));
		double keepCount = 0.0;
	    final ScheduledExecutorService sch = Executors.newScheduledThreadPool(1);
	    System.out.println("Starting" + timeToExecuteTheBaselinePhase + " minutes countdown from now to find the baseline .......");
        ScheduledFuture<?> countdown = sch.schedule(new Runnable() {
            @Override
            public void run() {
            	// Call the ACS_baselinePhase function to create the Metrics.csv file for all microservices
            	Rest1sService rest = new Rest1sService ();
            	try {
					rest.ACS_baselinePhase();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Error calling ACS_baselinePhase()");
					e.printStackTrace();
				}
                
            }}, timeToExecuteTheBaselinePhase, TimeUnit.MINUTES);

        while (!countdown.isDone()) {
            try {
                Thread.sleep(1000);
                keepCount = keepCount + 1000;
                System.out.println("Still the baseline function has not be called yet .. " + (keepCount/1000/60) + " out of: "+ timeToExecuteTheBaselinePhase);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        sch.shutdown();
	}

}
