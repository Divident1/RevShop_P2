package com.revshop.dto;

public class OrderResponseDto {

    private String orderId;
    private String message;
    private String paymentStatus;

    public OrderResponseDto() {
    }

    public OrderResponseDto(String orderId, String message, String paymentStatus) {
        this.orderId = orderId;
        this.message = message;
        this.paymentStatus = paymentStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public static OrderResponseDtoBuilder builder() {
        return new OrderResponseDtoBuilder();
    }

    public static class OrderResponseDtoBuilder {
        private String orderId;
        private String message;
        private String paymentStatus;

        public OrderResponseDtoBuilder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public OrderResponseDtoBuilder message(String message) {
            this.message = message;
            return this;
        }

        public OrderResponseDtoBuilder paymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }

        public OrderResponseDto build() {
            return new OrderResponseDto(orderId, message, paymentStatus);
        }
    }
}