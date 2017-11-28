package com.voicebase.monitoring.trafficlight.controller;

import com.voicebase.monitoring.trafficlight.service.TrafficLight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/traffic")
public class TrafficLightController {
  private static final Logger LOGGER = LoggerFactory.getLogger(TrafficLightController.class);

  @Autowired
  private TrafficLight trafficLight;

  @RequestMapping(method = RequestMethod.GET, value = "/test")
  public void test() throws InterruptedException {
    trafficLight.test();
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Unsupported Color. Colors supported: red, yellow, green, off.")
  public void error(UnsupportedColorException e) {
  }

  public class UnsupportedColorException extends Exception {
    public UnsupportedColorException() {
    }
  }
}
