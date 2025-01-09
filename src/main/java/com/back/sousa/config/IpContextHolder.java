package com.back.sousa.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class IpContextHolder {

    private static final ThreadLocal<String> ipContext = new ThreadLocal<>();

    public void set(String ipAddress) {
        ipContext.set(ipAddress);
    }

    public void remove() {
        ipContext.remove();
    }

    public String get() {
        return ipContext.get();
    }
}