package io.chucknorris.api.lib.event;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    private AmazonSNSClient snsClient;

    @Value("${application.event.sns_topic_arn}")
    private String topicArn;

    public EventService(AmazonSNSClient snsClient) {
        this.snsClient = snsClient;
    }

    public PublishResult publishEvent(Event event) throws JsonProcessingException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(simpleDateFormat);

        String message = objectMapper.writeValueAsString(event);
        PublishRequest publishRequest = new PublishRequest(topicArn, message);

        PublishResult publishResult = snsClient.publish(publishRequest);

        logger.info(
            "[event_published] " +
                "event_message_id: \"" + publishResult.getMessageId() + "\" " +
                "event_message: \"" + message + "\""
        );

        return publishResult;
    }
}
