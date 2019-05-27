package org.example.masterlistas;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class RegistroActivity extends AppCompatActivity {

    public final static String ARG_USERNAME = "arg_username";

    private EditText etUsuarioName, etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etUsuarioName = findViewById(R.id.usuario);
        etEmail = findViewById(R.id.email);

        if (getIntent() != null && getIntent().hasExtra(ARG_USERNAME)) {
            final String username = getIntent().getStringExtra(ARG_USERNAME);
            if (!TextUtils.isEmpty(username)) {
                if (username.contains("@")) {
                    etEmail.setText(username);
                } else {
                    etUsuarioName.setText(username);
                }
            }
        }
    }

    public void mostrarContraseña(View v) {
        EditText contraseña = (EditText) findViewById(R.id.contraseña);
        CheckBox mostrar = (CheckBox) findViewById(R.id.mostrar_contraseña);
        if (mostrar.isChecked()) {
            contraseña.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        } else {
            contraseña.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    public void acceder(View view) {
        Intent intent = new Intent(this, ListasActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

}
