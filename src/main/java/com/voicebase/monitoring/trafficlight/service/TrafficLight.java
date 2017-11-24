package com.voicebase.monitoring.trafficlight.service;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.voicebase.monitoring.trafficlight.model.TrafficLightMessage.Color;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by brianmcglothlen on 11/22/17.
 */
@Component
public class TrafficLight {
  private static final Logger LOGGER = LoggerFactory.getLogger(TrafficLight.class);
  private final GpioController gpio = GpioFactory.getInstance();
  private Map<Color, GpioPinDigitalOutput> pins = new HashMap<Color, GpioPinDigitalOutput>();
  private Color color;

  public TrafficLight() {
    pins.put(Color.red, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, Color.red.name(), PinState.HIGH));
    pins.put(Color.yellow, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, Color.yellow.name(), PinState.HIGH));
    pins.put(Color.green, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, Color.green.name(), PinState.HIGH));

    for (GpioPinDigitalOutput pin : pins.values()) {
      pin.setShutdownOptions(true, PinState.LOW);
      pin.low();
    }
  }

  public void setColor(Color color) {
    LOGGER.info("setColor:{}", color);

    if (this.color != color) {
      GpioPinDigitalOutput pin = pins.get(color);
      pin.high();

      if (this.color != null) {
        pin = pins.get(this.color);
        pin.low();
      }

      this.color = color;
    }
  }

  public Color getColor() {
    return color;
  }
}
