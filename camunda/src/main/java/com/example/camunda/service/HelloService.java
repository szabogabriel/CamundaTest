package com.example.camunda.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
public class HelloService implements JavaDelegate {
	
	public void sayHello(String message) {
		System.out.println("This is me saying: " + message);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		sayHello("[FirstTask] Camunda process started. Waiting for message.");
	}

}
