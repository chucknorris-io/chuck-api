package io.chucknorris.lib.event;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import java.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EventService {

  private static final Logger logger = LoggerFactory.getLogger(EventService.class);

  private MeterRegistry meterRegistry;

  private AmazonSNSClient snsClient;

  @Value("${application.event.sns_topic_arn}")
  private String topicArn;

  public EventService(MeterRegistry meterRegistry, AmazonSNSClient snsClient) {
    this.meterRegistry = meterRegistry;
    this.snsClient = snsClient;
  }

  /**
   * Publishes an event {@link Event} to an AWS SNS topic specified in
   * "application.event.sns_topic_arn" and returns the result {@link PublishResult}.
   *
   * @param event The event being published {@link Event}
   * @return publishResult
   * @throws JsonProcessingException Thrown in case of problems encountered when processing JSON
   *                                 content that are not pure I/O problems.
   */
  public PublishResult publishEvent(Event event) throws JsonProcessingException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setDateFormat(simpleDateFormat);

    String message = objectMapper.writeValueAsString(event);
    PublishRequest publishRequest = new PublishRequest(topicArn, message);

    PublishResult publishResult = snsClient.publish(publishRequest);

    meterRegistry.counter(
        "application.event.publish",
        "event_name", event.getName()
    ).increment();

    logger.info(
        "[event_published] "
            +
            "event_message_id: \"" + publishResult.getMessageId() + "\" "
            +
            "event_message: \"" + message + "\""
    );

    return publishResult;
  }
}
