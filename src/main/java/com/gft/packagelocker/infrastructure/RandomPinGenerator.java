package com.gft.packagelocker.infrastructure;

import com.gft.packagelocker.application.delivery.PinGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomPinGenerator implements PinGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String generate() {
        int pin = RANDOM.nextInt(900000) + 100000;
        return String.valueOf(pin);
    }
}
