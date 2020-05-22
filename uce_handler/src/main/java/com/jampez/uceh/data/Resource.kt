package com.jampez.uceh.data

import com.jampez.uceh.utils.AppException

class Resource <T> private constructor(val status: Resource.Status, val data: T?, val exception: AppException?) {
    enum class Status {
        SUCCESS, FAILED, ERROR
    }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> failed(data: T?): Resource<T> {
            return Resource(Status.FAILED, data, null)
        }

        fun <T> error(exception: AppException?): Resource<T> {
            return Resource(Status.ERROR, null, exception)
        }
    }
}