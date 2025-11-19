package com.partyst.app.partystapp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.GenericRedis;
import com.partyst.app.partystapp.records.requests.RegisterRequest;
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
            System.out.println("BD caída, no se procesan operaciones.");
            return;
        }

        if (records == null || records.isEmpty()) {
            System.out.println("⛔ No hay operaciones pendientes.");
            return;
        }
        

        

        System.out.println("📌 Procesando " + records.size() + " operaciones pendientes...");

        for (var record : records) {
            try {
                // EXTRAER WRAPPER
                Object data = record.getValue().get("data");
                GenericRedis<?> req = objectMapper.convertValue(data, GenericRedis.class);

                System.out.println("➡️ Intentando procesar: " + req.type());

                switch (req.type()) {

                    case "REGISTER_REQUEST" -> {
                        User user = objectMapper.convertValue(req.data(), User.class);

                        System.out.println("Intentando guardar usuario: " + user.getEmail());


                        userRepository.save(user);
                        System.out.println("✔ Guardado correctamente: " + user.getEmail());
                    }

                }

                // SOLO SI TODO SALE BIEN → ACK + delete
                redisQueueService.ackRecord(record.getId().getValue());
                redisQueueService.delete(record.getId().getValue());
                System.out.println("🧹 Borrado de Redis: " + record.getId());

            } catch (Exception e) {
                System.out.println("❗ Error procesando operación, se reintentará después");
                e.printStackTrace();
                // IMPORTANTE: NO ACK, NO DELETE → queda en PEL
            }
        }
    }
}
