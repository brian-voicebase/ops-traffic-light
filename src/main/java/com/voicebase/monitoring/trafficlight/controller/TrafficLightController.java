package com.voicebase.monitoring.trafficlight.controller;

import com.voicebase.monitoring.trafficlight.model.TrafficLightMessage.Color;
import com.voicebase.monitoring.trafficlight.service.TrafficLight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/traffic")
public class TrafficLightController {
  private static final Logger LOGGER = LoggerFactory.getLogger(TrafficLightController.class);

  @Autowired
  private TrafficLight trafficLight;

  @RequestMapping(method = RequestMethod.PUT, value = "/color/{color}")
  public void setColor(@PathVariable("color") String colorValue) {
    try {
      trafficLight.setColor(Color.valueOf(colorValue.toLowerCase()));
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Unsupported color:{}. Colors supported:{}.", colorValue, Color.values());
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "/color")
  public String getColor() {
    return trafficLight.getColor().toString();
  }
}
