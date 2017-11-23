package com.voicebase.monitoring.trafficlight.model;

import lombok.Data;

/**
 * Created by brianmcglothlen on 11/22/17.
 */
@Data
public class TrafficLightMessage {
  public enum Color {green, yellow, red};

  private Color color;
  private String message;
}
