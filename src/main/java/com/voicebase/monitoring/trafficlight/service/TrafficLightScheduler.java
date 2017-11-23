package com.voicebase.monitoring.trafficlight.service;

import com.voicebase.monitoring.trafficlight.model.TrafficLightMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by brianmcglothlen on 10/24/16.
 */
@Service
public class TrafficLightScheduler {
  private static final Logger LOGGER = LoggerFactory.getLogger(TrafficLightScheduler.class);

  @Value("${monitoring.url}")
  private String url;

  @Value("${monitoring.token}")
  private String token;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private TrafficLight trafficLight;

  private HttpEntity<String> entity;

  private HttpEntity getEntity() {
    if (entity==null) {
      HttpHeaders headers = new HttpHeaders();
      headers.set("x-api-key", token);
      entity = new HttpEntity<String>("parameters", headers);
    }
    return entity;
  }

  @Scheduled(fixedRate = 60000)
  private void checkStatus() {
    ResponseEntity<TrafficLightMessage> responseEntity = restTemplate.exchange(url, HttpMethod.GET, getEntity(), TrafficLightMessage.class);
    LOGGER.info("color:{} message:{}", responseEntity.getBody().getColor(), responseEntity.getBody().getMessage());
    trafficLight.setColor(responseEntity.getBody().getColor());
  }
}
