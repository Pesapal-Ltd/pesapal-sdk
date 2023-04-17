package com.pesapal.sdk.utils

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> loadFragment(frag: String): Resource<T> {
            return Resource(Status.LOADING, null, frag)
        }

        fun <T> loadFragment(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String): Resource<T> {
            return Resource(Status.ERROR, null, msg)
        }

        fun <T> loading(msg: String): Resource<T> {
            return Resource(Status.LOADING, null, msg)
        }

    }

}