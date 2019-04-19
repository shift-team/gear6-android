package util

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Taken from https://futurestud.io/tutorials/retrofit-2-how-to-upload-files-to-server
interface FileUploadService {
    @Multipart
    @POST("obd2data")
    fun upload(
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>
}