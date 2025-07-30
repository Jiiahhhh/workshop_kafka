package com.ilal

import groovy.json.JsonOutput       // To convert Map to JSON
import groovy.util.logging.Slf4j    // To logging
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

// @Slf4j to magically add 'log' variable to this class.
@Slf4j
class KafkaProducerService {

    // Declare a private property/variable named 'producer'
    // <String, String> as its data type. That is mean, key and value from kafka message will be String.
    private KafkaProducer<String, String> producer

    // @PostConstruct will be automatically execute after this service is ready (initialization)
    @PostConstruct
    void init() {
        try {
            // Create a props to cover all configurations (like a forms)
            Properties props = new Properties()
            // "bootstrap.servers" is a gate to kafka Cluster
            // Client only need to know one broker, and then that broker will
            // told client about all other brokers in the cluster.
            // "localhost:9092" is my Kafka Docker Container.
            props.put("bootstrap.servers", "localhost:9092")
            // "key.serializer" is a rule to change 'key' from the message to byte
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
            // "value.serializer" is a rule to change 'value' of the message to byte
            // I choose StringSerializer because I send a message in String JSON format
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
            // After all the props has filled, I create Producer object.
            producer = new KafkaProducer<>(props)
            log.info("Kafka Producer successfully initialized.")
        } catch (Exception e) {
            log.error("Failed to initialize Kafka Producer", e)
        }
    }

    // Main method to send message
    // topic is the purpose of message, and message is the actual message in Map
    void send(String topic, Map message) {
        // If producer is failed to created in init, will returns nothing
        // Avoid NullPointerException.
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
