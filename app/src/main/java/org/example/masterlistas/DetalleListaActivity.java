package org.example.masterlistas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

public class DetalleListaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_lista);

        int numeroLista = getIntent().getExtras().getInt("numeroLista");
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textWhite));
        toolbar.setTitle("");
        ImageView imageView = (ImageView) findViewById(R.id.imagen);
        if (numeroLista == 0) {
            toolbar.setTitle("Trabajo");
            imageView.setImageResource(R.drawable.trabajo);
        } else {
            toolbar.setTitle("Personal");
            imageView.setImageResource(R.drawable.casa);
        }
    }
}
