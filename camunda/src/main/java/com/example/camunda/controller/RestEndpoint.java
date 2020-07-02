package com.example.camunda.controller;

import java.util.HashMap;
import java.util.Map;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.camunda.service.HelloService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Api(value="/r1")
@RestController
@RequestMapping("/r1")
public class RestEndpoint {

  @Autowired
  private HelloService helloService;

  @Autowired
  private RuntimeService runtimeService;

  @ApiOperation(value = "This is the operation index.", response = String.class, notes="These are the notes...")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Request received."),
    @ApiResponse(code = 404, message = "Fehler.")})
  @GetMapping("/index")
  public String index() {
    helloService.sayHello("[REST] Initiating the Camunda process. Decide what to do!");

    return "<a href='/r1/start?skipWait=true'>Skip wait</a><br/><a href='/r1/start?skipWait=false'>Wait</a>";
  }

  @ApiOperation(value = "This is the operation index.", response = String.class, notes="These are the notes...")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Request received."),
      @ApiResponse(code = 404, message = "Fehler.")})
  @GetMapping("/start")
  public String start(@ApiParam(value = "Value whether to skip the wait step.", required = true) @RequestParam String skipWait) {
    helloService.sayHello("[REST] Starting new process with name: TestProcess");

    Boolean gonnaSkipWait = "TRUE".equalsIgnoreCase(skipWait);

    Map<String, Object> params = new HashMap<>();
    params.put("skipWait", gonnaSkipWait);

    ProcessInstance pi = runtimeService.startProcessInstanceByKey("TestProcess", params);

    String id = pi.getProcessInstanceId();

    if (!gonnaSkipWait) {
      return "I'm waiting. You can proceed by calling <br/>" + "<a href='/r1/continue?id=" + id
          + "&message=MessageContinueA' target='_blank'>step A with id: " + id + "</a><br/>"
          + " and <br/>" + "<a href='/r1/continue?id=" + id
          + "&message=MessageContinueB' target='_blank'>step B with id: " + id + "</a>";
    } else {
      return "I'm not waiting. This is finished. You can start over <a href='/r1/index'>here</a>.";
    }
  }

  @ApiOperation(value = "This is the operation index.", response = String.class, notes="These are the notes...")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Request received."),
      @ApiResponse(code = 404, message = "Fehler.")})
  @GetMapping("/continue")
  public String serviceC(@ApiParam(value = "ID of the process to be progressed.", required = true)@RequestParam String id, @ApiParam(value = "The message to be sent.", required = true)@RequestParam String message) {
    helloService.sayHello("[REST] Gonna continue Camunda process with id: " + id);

    MessageCorrelationResult result = runtimeService.createMessageCorrelation(message)
        .processInstanceId(id).setVariables(new HashMap<String, Object>()).correlateWithResult();

    return "Wait state for process id: " + id + " and Message: " + message + " received.<br/>"
        + "Result: " + result.getResultType().toString() + "<br/>"
        + "\n<a href='/r1/index'>Start new.</a>";
  }

}
