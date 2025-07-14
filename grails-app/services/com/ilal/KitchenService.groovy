package com.ilal

import grails.gorm.transactions.Transactional
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer

import javax.annotation.PostConstruct
import java.time.Duration

@Slf4j
@Transactional
class KitchenService {

    KafkaProducerService kafkaProducerService
    BaristaService baristaService

    @PostConstruct
    void listen() {
        Thread.start {
            try {
                Properties props = new Properties()
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
                // IMPORTANT: Group ID is different to each chef
                props.put(ConsumerConfig.GROUP_ID_CONFIG, "kitchen-group")
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

                KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)
                consumer.subscribe(["new-orders"])
                log.info(" KitchenService is now listening to 'new-orders' topic.")

                while (true) {
                    def records = consumer.poll(Duration.ofMillis(1000))
                    for (def record in records) {
                        def data = new JsonSlurper().parseText(record.value())
                        log.info(" Kitchen received order #${data.orderId}")

                        def foodInOrder = data.items.findAll { it.category == 'FOOD' }

                        foodInOrder.each { item ->
                            // Chef only care with FOOD
                            log.info(" -> Cooking '${item.name} (x${item.quantity})...")
                            sleep(5000)
                            baristaService.updateItemStatus(data.orderId, item.menuItemId)
                            log.info(" -> Food '${item.name}' is READY.")
                        }
                        if (!foodInOrder.isEmpty()) {
                            def readinessEvent = [orderId: data.orderId, component: 'KITCHEN']
                            kafkaProducerService.send("readiness-events", readinessEvent)
                        }
                    }
                }
            } catch (Exception e) {
                log.error("FATAL: KitchenService consumer thread has crashed!: ", e)
            }
        }
    }
}
