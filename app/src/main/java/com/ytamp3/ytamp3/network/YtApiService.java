package com.ytamp3.ytamp3.network;

import com.ytamp3.ytamp3.model.Song;
import com.ytamp3.ytamp3.model.SongRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface YtApiService {
    @Headers({
            "Content-Type: application/json"
    })    @POST("download")
    Call<Song> getSong(
            @Body SongRequest song
    );
}
