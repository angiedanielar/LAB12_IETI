package edu.eci.ieti.myapplication.services;

import edu.eci.ieti.myapplication.model.LoginWrapper;
import edu.eci.ieti.myapplication.model.Token;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("auth")
    Call<Token> login(@Body LoginWrapper loginWrapper);
}
