package com.voicebase.monitoring.trafficlight.service;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.voicebase.monitoring.trafficlight.model.ColorStates;
import com.voicebase.monitoring.trafficlight.model.ColorStates.Color;
import com.voicebase.monitoring.trafficlight.model.ColorStates.State;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.PostConstruct;
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
  private ColorStates colorStates = ColorStates.off();

  public TrafficLight() {
    pins.put(Color.red, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, Color.red.name(), PinState.HIGH));
    pins.put(Color.yellow, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, Color.yellow.name(), PinState.HIGH));
    pins.put(Color.green, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, Color.green.name(), PinState.HIGH));

    for (GpioPinDigitalOutput pin : pins.values()) {
      pin.setShutdownOptions(true, PinState.HIGH);
    }
  }

  public void setColorStates(ColorStates colorStates) {
    if (this.colorStates.compareTo(colorStates)!=0) {
      for (Entry<Color, State> entry : colorStates.getColorStatesMap().entrySet()) {
        Color color = entry.getKey();
        State state = entry.getValue();

        // Clear all pins
        for (GpioPinDigitalOutput pin : pins.values()) {
          pin.high();
        }

        GpioPinDigitalOutput pin = pins.get(color);
        switch (state) {
          case on:
            pin.low();
            break;
          case flash:
            pin.blink(500);
            break;
          case off:
          default:
            LOGGER.info("Turn it off:"+color);
            pin.blink(0);
            pin.high();
            break;
        }
      }

      this.colorStates = colorStates;

      LOGGER.info("Change setColorStates:{}", colorStates);
    }
  }

  public ColorStates getColorStates() {
    return colorStates;
  }

  @PostConstruct
  public void test() throws InterruptedException {
    ColorStates colorStates = ColorStates.off();
    for (int i=0; i<3; i++) {
      for (Color color : Color.values()) {
        colorStates.getColorStatesMap().put(color, State.on);
        setColorStates(colorStates);
        Thread.sleep(500);
        colorStates.getColorStatesMap().put(color, State.off);
        setColorStates(colorStates);
      }
    }
  }

  @Override
  protected void finalize() throws Throwable {
    gpio.shutdown();
    super.finalize();
  }
}
