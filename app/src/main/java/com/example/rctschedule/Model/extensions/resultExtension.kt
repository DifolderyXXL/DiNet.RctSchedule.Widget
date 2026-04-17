package com.example.rctschedule.Model.extensions

fun <T, TTo> Result<T>.moveFailure() : Result<TTo> {
    return Result.failure(this.exceptionOrNull()!!)
}

fun <T> Result<T>.nextOnSuccess(action: (T) ->  Result<T>) : Result<T> {
    if(this.isSuccess){
        return action(this.getOrNull()!!)
    }

    return this
}

fun <T> Result<T>.nextOnFailure(action: (Result<T>) ->  Result<T>) : Result<T> {
    if(this.isFailure){
        return action(this)
    }

    return this
}

suspend fun <T> Result<T>.nextOnFailureAsync(action: suspend (Result<T>) ->  Result<T>) : Result<T> {
    if(this.isFailure){
        return action(this)
    }

    return this
}