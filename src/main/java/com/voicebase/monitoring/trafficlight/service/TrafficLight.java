package com.voicebase.monitoring.trafficlight.service;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.voicebase.monitoring.trafficlight.model.ColorStatus;
import com.voicebase.monitoring.trafficlight.model.ColorStatus.Color;
import com.voicebase.monitoring.trafficlight.model.ColorStatus.State;
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
  private static final int FLASHRATE = 500;
  private final GpioController gpio = GpioFactory.getInstance();
  private final Map<Color, GpioPinDigitalOutput> pins = new HashMap<Color, GpioPinDigitalOutput>();
  private ColorStatus colorStatus = ColorStatus.reset();

  public TrafficLight() {
    pins.put(Color.red, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, Color.red.name(), PinState.HIGH));
    pins.put(Color.yellow, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, Color.yellow.name(), PinState.HIGH));
    pins.put(Color.green, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, Color.green.name(), PinState.HIGH));

    for (GpioPinDigitalOutput pin : pins.values()) {
      pin.setShutdownOptions(true, PinState.HIGH);
    }
  }

  public void setColorStatus(ColorStatus colorStatus) {
    // Use copy constructor so we save our own version of colorStatus and not a pointer to it.
    // (so it can't be changed without calling this API).
    ColorStatus copiedColorStatus = new ColorStatus(colorStatus);

    if (this.colorStatus.compareTo(copiedColorStatus)!=0) {
      for (Entry<Color, State> entry : copiedColorStatus.getColorStates().entrySet()) {
        GpioPinDigitalOutput pin = pins.get(entry.getKey());
        switch (entry.getValue()) {
          case on:
            pin.low();
            break;
          case flash:
            pin.blink(FLASHRATE);
            break;
          case off:
          default:
            pin.blink(0);
            pin.high();
            break;
        }
      }

      this.colorStatus = copiedColorStatus;
      LOGGER.info("Change setColorStates:{}", colorStatus);
    }
  }

  public ColorStatus getColorStatus() {
    return colorStatus;
  }

  @PostConstruct
  public void test() throws InterruptedException {
    for (State state : State.values()) {
      for (Color color : Color.values()) {
        ColorStatus colorStatus = ColorStatus.reset();
        colorStatus.getColorStates().put(color, state);
        setColorStatus(colorStatus);
        Thread.sleep(1500);
      }
    }

    for (State state : State.values()) {
      ColorStatus colorStatus = ColorStatus.reset();
      colorStatus.getColorStates().put(Color.red, state);
      colorStatus.getColorStates().put(Color.yellow, state);
      colorStatus.getColorStates().put(Color.green, state);
      setColorStatus(colorStatus);
      Thread.sleep(1500);
    }
  }

  @Override
  protected void finalize() throws Throwable {
    gpio.shutdown();
    super.finalize();
  }
}
