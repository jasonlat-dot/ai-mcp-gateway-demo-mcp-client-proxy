package com.jasonlat.mcp.api;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IOpenAiApiProxy {

    @POST("v1/chat/completions")
    Single<Object> completions(@Body Object request);
}
