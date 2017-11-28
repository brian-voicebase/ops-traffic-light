package com.voicebase.monitoring.trafficlight.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Data;

/**
 * Created by brianmcglothlen on 11/22/17.
 */
@Data
public class ColorStates implements Comparable<ColorStates> {
  private String message;
  public enum Color {red, yellow, green};
  public enum State {on, off, flash};

  @JsonProperty("color_states")
  private Map<Color, State> colorStatesMap = new HashMap<Color, State>();

  public static ColorStates off() {
    ColorStates colorStates = new ColorStates();
    for (Color colorType : Color.values()) {
      colorStates.colorStatesMap.put(colorType, State.off);
    }
    return colorStates;
  }

  public int compareTo(ColorStates o) {
    for (Entry<Color, State> entry : colorStatesMap.entrySet()) {
      Color color = entry.getKey();
      State state = entry.getValue();

      if (o.getColorStatesMap().get(color)!=state) {
        return -1;
      }
    }

    return 0;
  }
}
