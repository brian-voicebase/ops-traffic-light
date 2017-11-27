package com.voicebase.monitoring.trafficlight.service;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.voicebase.monitoring.trafficlight.model.TrafficLightMessage.Color;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${monitoring.quiet-mode}")
  private boolean quietMode;

  public TrafficLight() {
    pins.put(Color.red, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, Color.red.name(), PinState.HIGH));
    pins.put(Color.yellow, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, Color.yellow.name(), PinState.HIGH));
    pins.put(Color.green, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, Color.green.name(), PinState.HIGH));
    pins.put(Color.off, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, Color.off.name(), PinState.HIGH));

    for (GpioPinDigitalOutput pin : pins.values()) {
      pin.setShutdownOptions(true, PinState.LOW);
    }
  }

  public void setColor(Color color) {
    LOGGER.debug("setColor:{}", color);

    if (this.color != color) {
      // Only light green, if it's enabled.
      if (!quietMode || color!=Color.green) {
        pins.get(color).low();
      }

      pins.get(this.color).high();
      this.color = color;

      LOGGER.info("Change setColor:{}", color);
    }
  }

  public Color getColor() {
    return color;
  }

  public void setQuietMode(boolean quietMode)  {
    this.quietMode = quietMode;
    Color saveColor = color;
    setColor(Color.off);
    setColor(saveColor);
  }

  public boolean getQuietMode()  {
    return quietMode;
  }

  @PostConstruct
  public void test() throws InterruptedException {
    for (int i=0;i<3;i++) {
      for (Color color : Color.values()) {
        setColor(color);
        Thread.sleep(500);
      }
    }
  }

  @Override
  protected void finalize() throws Throwable {
    gpio.shutdown();
    super.finalize();
  }
}
