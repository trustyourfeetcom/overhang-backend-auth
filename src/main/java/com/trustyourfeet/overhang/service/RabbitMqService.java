package com.trustyourfeet.overhang.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trustyourfeet.overhang.config.RabbitMqConfig;
import com.trustyourfeet.overhang.event.AccountRegistrationEvent;

@Service
public class RabbitMqService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishAccountRegistrationEvent(AccountRegistrationEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.AUTH_EXCHANGE,
                RabbitMqConfig.AUTH_ACCOUNT_REGISTRATION_ROUTING,
                event);
    }
}
