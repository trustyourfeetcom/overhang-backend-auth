package com.trustyourfeet.overhang.service;

import com.trustyourfeet.overhang.event.AccountRegistrationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final String AUTH_ACCOUNT_REGISTRATION_TOPIC = "auth.account.registration";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendRegistrationEvent(AccountRegistrationEvent event) {
        kafkaTemplate.send(AUTH_ACCOUNT_REGISTRATION_TOPIC, event);
    }
}
