package cz.uhk.productScanner.restapi

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings.Secure
import android.util.Log
import androidx.preference.PreferenceManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import cz.broc.productscanner.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitProvider {

    val instance: Retrofit
        get() {
            return _instance!!
        }
    private var _instance: Retrofit? = null

    fun setInstance(retrofit: Retrofit) {
        _instance = retrofit
    }


}

@Module
@InstallIn(SingletonComponent::class)
object RetrofitProviderModule {
    @Provides
    fun provideRetrofitProvider(@ApplicationContext context: Context): RetrofitProvider {
        val retrofitProvider = RetrofitProvider()
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.SECONDS).apply {
                addInterceptor(
                    Interceptor { chain ->
                        val builder = chain.request().newBuilder()

                        // Retrieving user credentials
                        val prefs: SharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context)

                        //, "UserCredentials", MODE_PRIVATE
                        val userId = prefs.getInt("userId", 0)
                        val location = prefs.getInt("locId", 0)
                        if (userId > 0) {
                            builder.header("userId", "$userId")
                        }
                        if (location > 0) {
                            builder.header("locId", "$location")
                        }

                        builder.header("ceserver-version", "v2")

                        val androidId: String = Secure.getString(
                            context.getContentResolver(),
                            Secure.ANDROID_ID
                        );
                        Log.d("deviceId", androidId)
                        Log.d("userId", userId.toString())
                        Log.d("locationId", location.toString())
                        builder.header("deviceId", androidId)

                        builder.build()

                        chain.proceed(builder.build())
                    }
                )
            }
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        var retrofitBuilder = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))

        val serverUrl: String = context.getString(R.string.server_url)
        val retrofit = retrofitBuilder.baseUrl(serverUrl).build()

        retrofitProvider.setInstance(retrofit)

        return retrofitProvider
    }

}