package com.example.camunda.controller;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.camunda.service.HelloService;

@RestController
@RequestMapping("/r1")
public class RestEndpoint {

	@Autowired
	private HelloService helloService;

	@Autowired
	private RuntimeService runtimeService;

	@GetMapping("/start")
	public String serviceB() {
		helloService.sayHello("Hello, Camunda from B");

		ProcessInstance pi = runtimeService.startProcessInstanceByKey("TestProcess");
		
		String id = pi.getProcessInstanceId();
		
		return "<a href='/r1/continue?id=" + id + "'>Continue process with id: " + id + "</a>"; 
	}

	@GetMapping("/continue")
	public String serviceC(@RequestParam String id) {
		helloService.sayHello("Gonna continue Camunda process with id: " + id);

		MessageCorrelationResult result = runtimeService.createMessageCorrelation("MessageContinue")
			.processInstanceId(id)
			.correlateWithResult();
		
		return "Message sent to Camunda with result: " + result.getResultType()
			+ "<a href='/r1/start'>Start new.</a>";
	}

}
