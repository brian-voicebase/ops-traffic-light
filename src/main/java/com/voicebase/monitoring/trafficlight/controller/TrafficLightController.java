package com.voicebase.monitoring.trafficlight.controller;

import com.voicebase.monitoring.trafficlight.model.TrafficLightMessage.Color;
import com.voicebase.monitoring.trafficlight.service.TrafficLight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
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

  @RequestMapping(method = RequestMethod.PUT, value = "/color/{color}")
  public void setColor(@PathVariable("color") String colorValue) throws UnsupportedColorException {
    try {
      trafficLight.setColor(Color.valueOf(colorValue.toLowerCase()));
    } catch (IllegalArgumentException e) {
      LOGGER.warn(e.getMessage());
      throw new UnsupportedColorException();
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "/color")
  public String getColor() {
    return trafficLight.getColor().toString();
  }

  @RequestMapping(method = RequestMethod.PUT, value = "/quiet-mode/{enable}")
  public void setQuietMode(@PathVariable("enable") boolean enable)  {
    trafficLight.setQuietMode(enable);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/quiet-mode")
  public Boolean getQuietMode()  {
    return trafficLight.getQuietMode();
  }

  @RequestMapping(method = RequestMethod.GET, value = "/test")
  public void test() throws InterruptedException {
    trafficLight.test();
  }

  @ExceptionHandler(Exception.class)
  public void error(Exception e) {
  }

  @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Unsupported Color. Colors supported: red, yellow, green, off.")
  public class UnsupportedColorException extends Exception {
    public UnsupportedColorException() {
    }
  }
}
