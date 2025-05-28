package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.TrainRequest;
import com.gestiondesannotateurs.dtos.TrainResponse;
import com.gestiondesannotateurs.interfaces.ModelService;
import com.gestiondesannotateurs.utils.MultipartInputStreamFileResource;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class ModelServiceImpl implements ModelService {
    private RestTemplate restTemplate;
    public ModelServiceImpl(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }


    public String uploadFullDataset(TrainRequest request) throws IOException {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            InputStreamResource fileAsResource = new InputStreamResource(request.getFile().getInputStream()) {
                @Override
                public String getFilename() {
                    return request.getFile().getOriginalFilename();
                }

                @Override
                public long contentLength() throws IOException {
                    return request.getFile().getSize();
                }
            };

            body.add("file", fileAsResource);
            body.add("task", request.getTask());
            body.add("learning_rate", String.valueOf(request.getLearningRate()));
            body.add("epochs", String.valueOf(request.getEpochs()));
            body.add("batch_size", String.valueOf(request.getBatchSize()));
            body.add("user", request.getUser());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://localhost:8000/model/upload/full_dataset",
                    requestEntity,
                    String.class
            );
            return response.getBody() ;
        } catch (Exception e) {
            return "❌ Failed to upload the dataset. Please verify the file format and try again.";
        }
    }

    public String launchTraining(int datasetId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://localhost:8000/model/train/" + datasetId,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return "✅ Training started and end successfully you can check the history";
            } else {
                return "⚠️ Unexpected response while starting training.";
            }
        } catch (Exception e) {
            return "❌ Failed to start training. Please see the history and try later.";
        }
    }

    public String launchTesting(int datasetId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://localhost:8000/model/test/" + datasetId,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return "✅ Testing started and  end successfully."+response.getBody();
            } else {
                return "⚠️ Unexpected response while starting testing.";
            }
        } catch (Exception e) {
            return "❌ Failed to start testing. Please see the history and try again later.";
        }
    }
    public String getTrainingHistory(int datasetId) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    "http://localhost:8000/model/train/history/" + datasetId,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return "📈 Training History:\n" + response.getBody();
            } else {
                return "⚠️ Unable to retrieve training history.";
            }
        } catch (Exception e) {
            return "❌ Failed to get training history. Please check the dataset ID.";
        }
    }

    public String getTestingHistory(int datasetId) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    "http://localhost:8000/model/test/history/" + datasetId,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return "🧪 Testing History:\n" + response.getBody();
            } else {
                return "⚠️ Unable to retrieve testing history.";
            }
        } catch (Exception e) {
            return "❌ Failed to get testing history. Please check the dataset ID.";
        }
    }

}

