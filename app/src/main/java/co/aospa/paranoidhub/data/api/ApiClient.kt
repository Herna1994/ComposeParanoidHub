package co.aospa.paranoidhub.data.api

import co.aospa.paranoidhub.GlobalConstants
import co.aospa.paranoidhub.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private var BASE_URL = GlobalConstants.API_URL

    fun setUrlBase(direccion: String?) {
        BASE_URL = direccion ?: GlobalConstants.API_URL
        mRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(mOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private var mHttpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    private val mOkHttpClient : OkHttpClient
        get() {
            val builder = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
            if (BuildConfig.DEBUG) {
               // builder.addInterceptor(mHttpLoggingInterceptor)
            }
            return builder.build()
        }

    var mRetrofit: Retrofit? = null

    val client : Retrofit?
        get() {
            if (mRetrofit == null) {
                mRetrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(mOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return mRetrofit
        }
}