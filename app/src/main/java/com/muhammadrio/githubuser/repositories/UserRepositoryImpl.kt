package com.muhammadrio.githubuser.repositories

import androidx.lifecycle.LiveData
import com.muhammadrio.githubuser.DEFAULT_PER_PAGE
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.data.ErrorMessage
import com.muhammadrio.githubuser.data.Result
import com.muhammadrio.githubuser.data.local.FavoriteUserDao
import com.muhammadrio.githubuser.data.remote.GithubUserApi
import com.muhammadrio.githubuser.data.remote.Retrofit
import com.muhammadrio.githubuser.model.QueryParams
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.model.UserDetails
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.UnknownHostException
import kotlin.math.ceil

class UserRepositoryImpl(
    private val favoriteUserDao: FavoriteUserDao,
    private val githubUserApi: GithubUserApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository {

    private var totalPages = 1

    override suspend fun getUserDetails(userLogin: String): Result<UserDetails> {
        return withContext(dispatcher) {
            runCatching {
                val response = githubUserApi.getUserDetails(userLogin)
                handleResponse(response)
            }.getOrElse { t ->
                handleException(t)
            }
        }
    }

    override suspend fun getUsers(queryParams: QueryParams): Result<List<User>> {
        return withContext(dispatcher) {
            runCatching {
                val response = githubUserApi.searchUsers(queryParams.query,queryParams.page)
                when (val result = handleResponse(response)) {
                    is Result.Success -> {
                        val totalItems = result.value.totalCount
                        totalPages = ceil(totalItems.toFloat() / DEFAULT_PER_PAGE).toInt()
                        Result.Success(result.value.items)
                    }
                    is Result.Failure -> result
                }
            }.getOrElse { t ->
                handleException(t)
            }
        }
    }

    override suspend fun getFollowers(userLogin: String): Result<List<User>> {
        return withContext(dispatcher) {
            runCatching {
                val response = githubUserApi.getFollowers(userLogin)
                handleResponse(response)
            }.getOrElse { t ->
                handleException(t)
            }
        }
    }

    override suspend fun getFollowing(userLogin: String): Result<List<User>> {
        return withContext(dispatcher) {
            runCatching {
                val response = githubUserApi.getFollowing(userLogin)
                handleResponse(response)
            }.getOrElse { t ->
                handleException(t)
            }
        }
    }

    override fun getFavoriteUsers(): LiveData<List<User>> {
        return favoriteUserDao.getFavoriteUsers()
    }

    override suspend fun insertFavoriteUser(user: User) {
        withContext(dispatcher) {
            user.setIsFavorite(true)
            favoriteUserDao.insertFavoriteUser(user)
        }
    }

    override suspend fun deleteFavoriteUser(user: User) {
        withContext(dispatcher) {
            favoriteUserDao.deleteFavoriteUser(user)
        }
    }

    override suspend fun clearFavoriteUsers() {
        withContext(dispatcher) {
            favoriteUserDao.clearFavoriteUser()
        }
    }

    override suspend fun checkIsFavoriteUser(id: Int): Boolean {
        return withContext(dispatcher){
            favoriteUserDao.isFavoriteUser(id)
        }
    }

    private fun <T> handleException(throwable: Throwable): Result<T> {
        throwable.printStackTrace()
        return when (throwable) {
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

    private fun <T> handleResponse(response: Response<T>): Result<T> {
        return when (response.code()) {
            in 200..300 -> {
                val body = response.body()
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