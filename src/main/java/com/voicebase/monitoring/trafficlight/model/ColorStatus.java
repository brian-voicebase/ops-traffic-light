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
public class ColorStatus implements Comparable<ColorStatus> {
  public enum Color {red, yellow, green};
  public enum State {on, flash, off};

  @JsonProperty("message")
  private String message;

  @JsonProperty("color_states")
  private Map<Color, State> colorStates;

  public ColorStatus() {
  }

  public ColorStatus(ColorStatus colorStatus) {
    if (colorStatus!=null) {
      this.message = colorStatus.getMessage();
      colorStates = new HashMap<Color, State>();
      for (Color color : Color.values()) {
        State state = (colorStatus.colorStates != null) ? colorStatus.colorStates.get(color) : null;
        colorStates.put(color, (state != null) ? state : State.off);
      }
    }
  }

  public int compareTo(ColorStatus o) {
    for (Entry<Color, State> entry : colorStates.entrySet()) {
      Color color = entry.getKey();
      State state = entry.getValue();
      State checkState = o.getColorStates().get(color);
      if (checkState!=state) {
        return -1;
      }
    }
    return 0;
  }

  public static ColorStatus reset() {
    ColorStatus colorStates = new ColorStatus();
    colorStates.setColorStates(new HashMap<Color, State>());

    for (Color color : Color.values()) {
      colorStates.colorStates.put(color, State.off);
    }
    return colorStates;
  }
}
