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
class BaristaService {

    KafkaProducerService kafkaProducerService

    @PostConstruct
    void listen() {
        // Run in seperate thread so it is not blocking the app
        Thread.start {
            try {
                Properties props = new Properties()
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
                // IMPORTANT: ID group for baristas
                props.put(ConsumerConfig.GROUP_ID_CONFIG, "barista-group")
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

                KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)
                consumer.subscribe(["new-orders"])
                log.info("BaristaService is now listening to 'new-orders' topic.")

                while (true) {
                    def records = consumer.poll(Duration.ofMillis(1000))
                    for (def record in records) {
                        def data = new JsonSlurper().parseText(record.value())
                        log.info("Barista received order #${data.orderId}")

                        def drinkInOrder = data.items.findAll { it.category == 'DRINK' }

                        // loop for every item in order
                        drinkInOrder.each { item ->
                            // Barista ONLY care about DRINK
                            log.info(" -> Making '${item.name}' (x${item.quantity})...")
                            sleep(3000)
                            updateItemStatus(data.orderId, item.menuItemId) //Update DB Status
                            log.info(" -> Drink '${item.name}' is READY.")
                        }

                        // After ALL Drinks in order have done, send report.
                        if (!drinkInOrder.isEmpty()) {
                            def readinessEvent = [orderId: data.orderId, component: 'BARISTA']
                            kafkaProducerService.send("readiness-events", readinessEvent)
                        }
                    }
                }
            } catch (Exception e) {
                log.error("FATAL: BaristaService consumer thread has crashed!: ", e)
            }
        }
    }

//    @Transactional
    void updateItemStatus(Long orderId, Long menuItemId) {
        Order.withTransaction {
            Order order
            int maxRetries = 5
            int retryCount = 0

            // Loop to get data each times
            while (retryCount < maxRetries) {
                order = Order.get(orderId)
                if (order) {
                    // If data found, out of loop
                    break
                }
                // If data not found, wait in more time
                retryCount++
                log.warn("   -> Order #${orderId} not found in DB. Retrying in 200ms... (Attempt ${retryCount}/${maxRetries})")
                sleep(200) // Wait 200 millisecond
            }
            // After loop, cek again if the order finally founds.
            if (!order) {
                log.error("   -> FAILED to find Order #${orderId} after ${maxRetries} retries. Skipping item update.")
                return // Out from method if the order still not founds.
            }

            // If order found, continue to the logic
            OrderItem itemToUpdate = order.items.find { it.menuItem.id == menuItemId}
            if (itemToUpdate) {
                itemToUpdate.status = OrderItem.ItemStatus.COMPLETED
                itemToUpdate.save(flush: true)
            }
        }
    }
}
