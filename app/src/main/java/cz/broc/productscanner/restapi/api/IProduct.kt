package cz.broc.capriscan.restapi.api

import cz.broc.capriscan.restapi.model.ProductResponse
import cz.uhk.productScanner.restapi.RetrofitProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import retrofit2.Call

import retrofit2.http.GET

import retrofit2.http.Path


interface IProduct {
    @GET("/brocklj/product_scanner/refs/heads/main/{scv_name}")
    fun fetchProduct(@Path("scv_name") mchId: String): Call<List<ProductResponse>>
}



@Module
@InstallIn(SingletonComponent::class)
object ProductAPIModule {
    @Provides
    fun provideProductAPIService(retrofitProvider: RetrofitProvider): IProduct {
        return  retrofitProvider.instance.create(IProduct::class.java)
    }

}