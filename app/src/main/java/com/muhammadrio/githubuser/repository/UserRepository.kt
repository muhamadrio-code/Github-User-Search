package com.muhammadrio.githubuser.repository

import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.model.QueryResult
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.model.UserDetails
import com.muhammadrio.githubuser.network.ErrorMessage
import com.muhammadrio.githubuser.network.Result
import com.muhammadrio.githubuser.network.Retrofit
import retrofit2.Response
import java.net.UnknownHostException

class UserRepository {

    private val userApi = Retrofit.userApi

    suspend fun getUserDetails(userLogin:String) : Result<UserDetails> {
        return runCatching {
            val response = userApi.getUserDetails(userLogin)
            handleResponse(response)
        }.getOrElse { t ->
            handleException(t)
        }
    }

    suspend fun searchUsers(q:String,page:Int = 1) : Result<List<User>> {
       return runCatching {
           val response = userApi.searchUsers(q,page)
           when(val result = parseResponse(response)){
               is Result.Success -> Result.Success(result.value.items)
               is Result.Failure -> result
           }
        }.getOrElse { t ->
            handleException(t)
        }
    }

    suspend fun getFollowers(userLogin: String) : Result<List<User>> {
        return runCatching {
            val response = userApi.getFollowers(userLogin)
            parseResponse(response)
        }.getOrElse { t ->
            handleException(t)
        }
    }

    suspend fun getFollowing(userLogin: String) : Result<List<User>> {
        return runCatching {
            val response = userApi.getFollowing(userLogin)
            parseResponse(response)
        }.getOrElse { t ->
            handleException(t)
        }
    }

    private fun <T>parseResponse(response: Response<T>) : Result<T> {
        return when(val result = handleResponse(response)){
            is Result.Success -> Result.Success(result.value)
            is Result.Failure -> result
        }
    }

    private fun <T>handleException(throwable:Throwable) : Result<T> {
        throwable.printStackTrace()
        return when(throwable){
            is UnknownHostException -> Result.Failure(
                ErrorMessage(
                    header = R.string.connection_failed_tittle,
                    body = R.string.connection_failed_message,
                    code = Retrofit.CONNECTION_ERROR_CODE
                ),
                throwable
            )
            else -> Result.Failure(
                ErrorMessage(
                    header = R.string.unknown_error_tittle,
                    body = R.string.unknown_error_message,
                    code = Retrofit.UNKNOWN_ERROR_CODE
                ),
                throwable
            )
        }
    }

    private fun <T>handleResponse(response: Response<T>): Result<T> {
        return when (response.code()){
            in 200..300 -> {
                val body = response.body()
                response.errorBody()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Failure(
                        ErrorMessage(
                            header = R.string.unknown_error_tittle,
                            body = R.string.unknown_error_message,
                            code = response.code()
                        )
                    )
                }
            }
            304 -> Result.Failure(
                ErrorMessage(
                    header = R.string.not_modified_tittle,
                    body = R.string.not_modified_message,
                    code = response.code()
                )
            )
            422 -> Result.Failure(
                ErrorMessage(
                    header = R.string.validation_failed_tittle,
                    body = R.string.validation_failed_message,
                    code = response.code()
                )
            )
            404 -> Result.Failure(
                ErrorMessage(
                    header = R.string.resource_not_found_tittle,
                    body = R.string.resource_not_found_message,
                    code = response.code()
                )
            )
            503 -> Result.Failure(
                ErrorMessage(
                    header = R.string.service_unavailable_tittle,
                    body = R.string.service_unavailable,
                    code = response.code()
                )
            )
            else -> Result.Failure(
                ErrorMessage(
                    header = R.string.unknown_error_tittle,
                    body = R.string.unknown_error_message,
                    code = response.code()
                )
            )
        }
    }

}