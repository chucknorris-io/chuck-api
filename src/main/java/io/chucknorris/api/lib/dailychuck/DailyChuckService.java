package io.chucknorris.api.lib.dailychuck;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.chucknorris.api.model.Joke;
import io.chucknorris.api.repository.JokeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class DailyChuckService {

    private AmazonS3 amazonS3;
    private JokeRepository jokeRepository;

    @Value("${dailychuck.bucket_name}")
    private String bucketName;

    @Value("${application.base_url}")
    private String baseUrl;

    @Value("${dailychuck.key_name}")
    private String keyName;

    public DailyChuckService(AmazonS3 amazonS3, JokeRepository jokeRepository) {
        this.amazonS3 = amazonS3;
        this.jokeRepository = jokeRepository;
    }

    public DailyChuckIssue composeDailyChuckIssue(DailyChuckIssue[] excludedIssues) {
        Joke joke = jokeRepository.getRandomJoke();

        Boolean isIncluded = false;
        for (DailyChuckIssue dailyChuckIssue : excludedIssues) {
            if (dailyChuckIssue.getJokeId().equals(joke.getId())) {
                isIncluded = true;
                break;
            }
        }

        if (!isIncluded) {
            DailyChuckIssue dailyChuckIssue = new DailyChuckIssue();
            dailyChuckIssue.setDate(new Date());
            dailyChuckIssue.setJokeId(joke.getId());

            return dailyChuckIssue;
        } else {
            return composeDailyChuckIssue(excludedIssues);
        }
    }

    public DailyChuck getDailyChuck() throws IOException {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, keyName);
        S3Object object = amazonS3.getObject(getObjectRequest);

        ObjectMapper mapper = new ObjectMapper();
        DailyChuck dailyChuck = mapper.readValue(object.getObjectContent(), DailyChuck.class);

        return dailyChuck;
    }

    public PutObjectResult persist(DailyChuck dailyChuck) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String fileContentString = mapper.writeValueAsString(dailyChuck);
        byte[] fileContentBytes = fileContentString.getBytes(StandardCharsets.UTF_8);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(MediaType.APPLICATION_JSON_VALUE);
        metadata.setContentLength(fileContentBytes.length);

        PutObjectRequest request = new PutObjectRequest(
            bucketName,
            keyName,
            new ByteArrayInputStream(fileContentBytes),
            metadata
        );

        return amazonS3.putObject(request);
    }

    public DailyChuckRss toRss(DailyChuck dailyChuck) {
        return new DailyChuckRss(baseUrl, dailyChuck, jokeRepository);
    }
}
