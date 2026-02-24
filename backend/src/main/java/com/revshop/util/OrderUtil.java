package com.revshop.util;

import java.util.UUID;

public class OrderUtil {

    public static String generateOrderId() {

        return "ORD-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();

    }

}