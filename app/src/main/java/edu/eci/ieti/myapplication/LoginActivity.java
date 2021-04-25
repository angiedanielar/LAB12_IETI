package edu.eci.ieti.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import edu.eci.ieti.myapplication.model.LoginWrapper;
import edu.eci.ieti.myapplication.model.Token;
import edu.eci.ieti.myapplication.services.AuthService;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private EditText editTextEmail;
    private EditText editTextPassword;
    private SharedPreferences sharedPreferences;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void toLogin(View view) {
        storeToken();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()) {
            executorService.execute(() -> {
                try {
                    Response<Token> response =
                            authService.login(new LoginWrapper(email, password)).execute();
                    Token token = response.body();
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(LaunchActivity.TOKEN_KEY, token.getAccessToken());
                            editor.apply();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            editTextEmail.setText("");
                            editTextPassword.setText("");
                        } else {
                            editTextPassword.setError("Acceso denegado");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        else {
            if (email.isEmpty()) editTextPassword.setError("El correo no puede estar en blanco");
            if (password.isEmpty()) editTextPassword.setError("La clave no puede estar en blanco");
        }
    }

    private void storeToken() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080") // localhost para el emulador
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        authService = retrofit.create(AuthService.class);
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }
}