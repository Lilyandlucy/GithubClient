package com.github.client.data.converter

import com.github.client.data.model.AccessTokenResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class GithubOAuthConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (type == AccessTokenResponse::class.java) {
            GithubOAuthConverter()
        } else {
            null
        }
    }

    private class GithubOAuthConverter : Converter<ResponseBody, AccessTokenResponse> {
        override fun convert(value: ResponseBody): AccessTokenResponse {
            val responseString = value.string()
            val params = responseString.split("&")
                .associate {
                    val parts = it.split("=")
                    parts[0] to parts[1]
                }

            return AccessTokenResponse(
                access_token = params["access_token"] ?: "",
                token_type = params["token_type"] ?: "",
                scope = params["scope"] ?: ""
            )
        }
    }
}