package com.pesapal.paymentgateway.payment.utils

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> pending(msg: String): Resource<T> {
            return Resource(Status.PENDING, null, msg)
        }

        fun <T> unAuthenticate(): Resource<T> {
            return Resource(Status.UNAUTHORIZED, null, null)
        }

        fun <T> error(msg: String): Resource<T> {
            return Resource(Status.ERROR, null, msg)
        }

        fun <T> loading(msg: String): Resource<T> {
            return Resource(Status.LOADING, null, msg)
        }

    }

}