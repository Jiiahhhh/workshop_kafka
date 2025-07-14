package com.ilal

import groovy.json.JsonOutput
import groovy.util.logging.Slf4j
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Slf4j
class KafkaProducerService {

    private KafkaProducer<String, String> producer

    @PostConstruct
    void init() {
        // Configuration to connect kafka server in Docker
        Properties props = new Properties()
        props.put("bootstrap.servers", "localhost:9092")
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

        try {
            this.producer = new KafkaProducer<>(props)
            log.info("Kafka Producer successfully initialized.")
        } catch (Exception e) {
            log.error("Failed to initialize Kafka Producer", e)
        }
    }

    // Main method to send message
    void send(String topic, Map message) {
        if (producer == null) {
            log.error("Kafka Producer is not initialized. Cannot send message")
            return
        }
        // Change message from Map to JSON String format
        String jsonMessage = JsonOutput.toJson(message)

        // Send message to the intended topic
        producer.send(new ProducerRecord<>(topic, jsonMessage))
        log.info("Message sent to topic [${topic}]: ${jsonMessage}")
    }

    @PreDestroy
    void cleanup() {
        // Close producer connection when app is shut down
        producer?.close()
        log.info("Kafka Producer has been closed.")
    }
}
