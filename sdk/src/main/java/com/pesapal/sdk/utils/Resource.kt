package com.pesapal.sdk.utils

data class Resource<out T>(val status: com.pesapal.sdk.utils.Status, val data: T?, val message: String?) {

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(com.pesapal.sdk.utils.Status.SUCCESS, data, null)
        }

        fun <T> loadFragment(frag: String): Resource<T> {
            return Resource(com.pesapal.sdk.utils.Status.LOADING, null, frag)
        }

        fun <T> loadFragment(data: T?): Resource<T> {
            return Resource(com.pesapal.sdk.utils.Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String): Resource<T> {
            return Resource(com.pesapal.sdk.utils.Status.ERROR, null, msg)
        }

        fun <T> loading(msg: String): Resource<T> {
            return Resource(com.pesapal.sdk.utils.Status.LOADING, null, msg)
        }

    }

}