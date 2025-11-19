package com.partyst.app.partystapp.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RedisQueueService {

    private final String STREAM_KEY = "pending-operations";
    private final String GROUP = "retry-group";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        try {
            redisTemplate.opsForStream().createGroup(
                STREAM_KEY,
                ReadOffset.from("0"),
                GROUP
            );
            System.out.println("➡️ Grupo creado: retry-group");
        } catch (Exception e) {
            System.out.println("ℹ️ Grupo ya existe: retry-group");
        }
    }

    public void enqueue(Object payload) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", payload);

        redisTemplate.opsForStream().add(
                StreamRecords.newRecord()
                        .in(STREAM_KEY)
                        .ofMap(map)
        );

        System.out.println("📥 Operación encolada en Redis.");
    }

    /** 🔥 versión correcta de lectura de PENDING */
    public List<MapRecord<String, Object, Object>> readPending() {
        return redisTemplate.opsForStream().read(
                Consumer.from(GROUP, "worker-1"),
                StreamReadOptions.empty().count(10),
                StreamOffset.create(STREAM_KEY, ReadOffset.from("0"))
        );
    }

    public void ackRecord(String id) {
        redisTemplate.opsForStream().acknowledge(STREAM_KEY, GROUP, id);
    }

    public void delete(String id) {
        redisTemplate.opsForStream().delete(STREAM_KEY, id);
    }
}
