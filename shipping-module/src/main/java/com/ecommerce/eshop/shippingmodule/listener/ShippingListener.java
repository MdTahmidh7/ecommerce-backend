package com.ecommerce.eshop.shippingmodule.listener;

import com.ecommerce.eshop.shippingmodule.dto.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ShippingListener {

//    private static final Logger logger = LoggerFactory.getLogger(ShippingListener.class);
//
//    @RabbitListener(queues = "q.order.created")
//    public void onOrderCreated(OrderCreatedEvent event) {
//        logger.info("Received order created event: {}", event);
//        // TODO: Process the order for shipment
//    }
}
