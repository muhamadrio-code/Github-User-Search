package com.muhammadrio.githubuser.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.muhammadrio.githubuser.Event
import com.muhammadrio.githubuser.SearchResponse
import com.muhammadrio.githubuser.model.QueryResult
import com.muhammadrio.githubuser.service.Retrofit
import retrofit2.Response
import java.io.IOException

class UserRepository {

    private val userApi = Retrofit.userApi

    private val _searchEvent = MutableLiveData<Event<SearchResponse>>()
    val searchEvent : LiveData<Event<SearchResponse>> = _searchEvent

    suspend fun searchUsers(q:String){
        runCatching {
            userApi.searchUsers(q)
        }.onFailure { e ->
            if (e is IOException) e.printStackTrace()
            _searchEvent.value = Event(SearchResponse.OnUnknownError(-1))
        }.onSuccess {response ->
            handleResponse(response)
        }
    }

    private fun handleResponse(response: Response<QueryResult>){
        when (response.code()){
            in 200..300 -> handleResponseSuccess(response)
            304 -> _searchEvent.value = Event(SearchResponse.OnUnknownError(response.code()))
            422 -> _searchEvent.value = Event(SearchResponse.OnValidationFailed)
            503 -> _searchEvent.value = Event(SearchResponse.OnServiceUnavailable)
        }
    }

    private fun handleResponseSuccess(response: Response<QueryResult>) {
        val body = response.body()
        body?.let { queryResult ->
            _searchEvent.value = Event(SearchResponse.OnSuccess(queryResult))
        }
    }

}