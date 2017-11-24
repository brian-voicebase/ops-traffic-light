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
  private Color color = Color.off;

  public TrafficLight() {
    pins.put(Color.red, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, Color.red.name(), PinState.LOW));
    pins.put(Color.yellow, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, Color.yellow.name(), PinState.LOW));
    pins.put(Color.green, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, Color.green.name(), PinState.LOW));
    pins.put(Color.off, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, Color.off.name(), PinState.LOW));

    for (GpioPinDigitalOutput pin : pins.values()) {
      pin.setShutdownOptions(true, PinState.LOW);
    }
  }

  public void setColor(Color color) {
    LOGGER.info("setColor:{}", color);

    if (this.color != color) {
      GpioPinDigitalOutput pinHigh = pins.get(color);
      pinHigh.high();
      LOGGER.info("pinHigh name:{}", pinHigh.getName());

      GpioPinDigitalOutput pinLow = pins.get(this.color);
      pinLow.low();
      LOGGER.info("pinLow name:{}", pinLow.getName());

      this.color = color;
    }
  }

  public Color getColor() {
    return color;
  }

  @Override
  protected void finalize() throws Throwable {
    gpio.shutdown();
    super.finalize();
  }
}
