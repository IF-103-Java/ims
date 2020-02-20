package com.ita.if103java.ims.config;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class GeneratedKeyHolderFactory implements KeyHolderFactory {
    public KeyHolder newKeyHolder() {
        return new GeneratedKeyHolder();
    }
}
