package com.cardnetwork;


import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class AtomicID {
    private final AtomicLong id = new AtomicLong(1);
    public long getID(){
        return id.getAndIncrement();
    }
}
