package com.partyst.app.partystapp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.GenericRedis;
import com.partyst.app.partystapp.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisRetryWorker {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisQueueService redisQueueService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseHealthService databaseHealthService;

    @Scheduled(fixedDelay = 10000)
    public void retryPendingOperations() {

        List<MapRecord<String, Object, Object>> records = redisQueueService.readPending();

        if (!databaseHealthService.isDatabaseUp()) {
            return;
        }

        if (records == null || records.isEmpty()) {
            return;
        }
        

        

        for (var record : records) {
            try {
                Object data = record.getValue().get("data");
                GenericRedis<?> req = objectMapper.convertValue(data, GenericRedis.class);

                switch (req.type()) {

                    case "REGISTER_REQUEST" -> {
                        User user = objectMapper.convertValue(req.data(), User.class);

        


                        userRepository.save(user);
        
                    }

                }

                redisQueueService.ackRecord(record.getId().getValue());
                redisQueueService.delete(record.getId().getValue());


            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }
}
