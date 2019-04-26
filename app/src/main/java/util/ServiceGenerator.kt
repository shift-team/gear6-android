package util

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// Taken from https://futurestud.io/tutorials/retrofit-2-creating-a-sustainable-android-client
object ServiceGenerator {

    private val BASE_URL = "https://gear6.io"

    private val builder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())

    private val retrofit = builder.build()

    private val httpClient = OkHttpClient.Builder()

    fun <S> createService(
        serviceClass: Class<S>
    ): S {
        return retrofit.create(serviceClass)
    }
}