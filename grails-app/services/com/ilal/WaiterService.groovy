package com.ilal

import grails.gorm.transactions.Transactional
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer

import javax.annotation.PostConstruct
import java.time.Duration

@Slf4j
class WaiterService {

    @PostConstruct
    void listen() {
        Thread.start {
            try {
                Properties props = new Properties()
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
                props.put(ConsumerConfig.GROUP_ID_CONFIG, "waiter-group")
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

                KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)
                consumer.subscribe(["readiness-events"])
                log.info(" WaiterService is now listening to 'readiness-events' topic.")

                while (true) {
                    def records = consumer.poll(Duration.ofMillis(1000))
                    for (def record in records) {
                        def data = new JsonSlurper().parseText(record.value())
                        log.info(" Waiter received a readiness report for order #${data.orderId} from ${data.component}")
                        checkOrderCompletion(data.orderId)
                    }
                }
            } catch (Exception e) {
                log.error("FATAL: WaiterService consumer thread has crashed!", e)
            }
        }
    }

    @Transactional
    void checkOrderCompletion(Long orderId) {
        Order order = Order.get(orderId)
        if (!order) {
            log.warn("Waiter received event for order #${orderId}, but order was not found in DB.")
            return
        }
        if (order.status == Order.OrderStatus.NEW) {
            order.status = Order.OrderStatus.PROCESSING
            order.save(flush: true)
        }

        boolean allItemsReady = order.items.every { it.status == OrderItem.ItemStatus.COMPLETED }

        if (allItemsReady) {
            log.info("   -> 🎉 ALL ITEMS FOR ORDER #${order.id} ARE READY! Preparing for delivery.")
            order.status = Order.OrderStatus.READY
            order.save(flush: true)
        } else {
            log.info("   -> ⏳ Order #${order.id} is partially ready. Still waiting for other components.")
        }
    }
}
