package org.wsulca.masterlistas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListasActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FlowingDrawer mDrawer;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private FirebaseAnalytics analytics;
    private long timeLoggin;
    private FirebaseRemoteConfig remoteConfig;
    private static final int CACHE_TIME_SECONDS = 3600; // 10 HORAS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas);
        analytics = FirebaseAnalytics.getInstance(this);
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings config = new FirebaseRemoteConfigSettings
                .Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        remoteConfig.setConfigSettings(config);
        remoteConfig.setDefaults(R.xml.remote_config);
        remoteConfig.fetch(CACHE_TIME_SECONDS)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ListasActivity.this, getString(R.string.fetch_successfully), Toast.LENGTH_SHORT).show();
                            remoteConfig.activateFetched();
                        } else {
                            Toast.makeText(ListasActivity.this, getString(R.string.fetch_failure), Toast.LENGTH_SHORT).show();
                        }
                        final boolean navigationDrawerAbierto = remoteConfig.getBoolean("navigation_drawer_abierto");
                        updatePrimeraVez(navigationDrawerAbierto);

                    }
                });

        timeLoggin = System.currentTimeMillis();
        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Navigation Drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.vNavigation);
        navigationView.setNavigationItemSelectedListener(this);
        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == ElasticDrawer.STATE_CLOSED) {
                    timeLoggin = System.currentTimeMillis();
                } else if (newState == ElasticDrawer.STATE_OPEN) {
                    Bundle param = new Bundle();
                    param.putString("element", "drawer left");
                    param.putLong("timebetweenLogin", System.currentTimeMillis() - timeLoggin);
                    analytics.logEvent("openLeftMenu", param);
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
                Log.i("MainActivity", "openRatio=" + openRatio + " ,offsetPixels=" + offsetPixels);
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.toggleMenu();
            }
        });

        //Inicializar los elementos
        List items = new ArrayList();
        items.add(new Lista(R.drawable.trabajo, getString(R.string.title_work), 2));
        items.add(new Lista(R.drawable.casa, getString(R.string.title_personal), 3));
        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);
        // Usar un administrador para LinearLayout
        // lManager = new GridLayoutManager(this, 2);
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);
        // Crear un nuevo adaptador
        adapter = new ListaAdapter(ListasActivity.this, items);
        recycler.setAdapter(adapter);
        recycler.addOnItemTouchListener(new RecyclerItemClickListener(ListasActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(ListasActivity.this, DetalleListaActivity.class);
                intent.putExtra("numeroLista", position);
                /*
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        ListasActivity.this, new Pair<View, String>(v.findViewById(R.id.imagen),
                                getString(R.string.transition_name_img)));
                                */
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ListasActivity.this,
                        new Pair<View, String>(v.findViewById(R.id.imagen), getString(R.string.transition_name_img)),
                        new Pair<View, String>(ListasActivity.this.findViewById(R.id.fab), getString(R.string.transition_action_plus))
                );
                ActivityCompat.startActivity(ListasActivity.this, intent, options.toBundle());
            }
        }));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.fab_pressed), Snackbar.LENGTH_LONG).show();
            }
        });

        Transition lista_enter = TransitionInflater.from(this).inflateTransition(R.transition.transition_lista_enter);
        getWindow().setEnterTransition(lista_enter);

        abrePrimeraVez(); // se abre la siguiente vez que ingresa, ya que el remote config puede
        // tardar en sincronizar y sería mala experiencia mostrarle el menu cuando no haya hecho mada
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    public void updatePrimeraVez(boolean update) {
        SharedPreferences sp = getSharedPreferences("mispreferencias", 0);
        SharedPreferences.Editor e = sp.edit();
        e.putBoolean("abrePrimeraVez", update).commit();
    }

    public void abrePrimeraVez() {
        SharedPreferences sp = getSharedPreferences("mispreferencias", 0);
        boolean primerAcceso = sp.getBoolean("abrePrimeraVez", false);
        if (primerAcceso) {
            if (mDrawer != null) {
                mDrawer.openMenu();
                SharedPreferences.Editor e = sp.edit();
                e.putBoolean("abrePrimeraVez", false).commit();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_compartir:
                compatirTexto("http://play.google.com/store/apps/details?id=" + getPackageName());
                break;
            case R.id.nav_compartir_lista:
                compatirTexto("LISTA DE LA COMPRA: patatas, leche, huevos. ---- " +
                        "Compartido por: http://play.google.com/store/apps/details?id=" +
                        getPackageName());
                break;
            case R.id.nav_compartir_logo:
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                compatirBitmap(bitmap, "Compartido por: " + "http://play.google.com/store/apps/details?id=" + getPackageName());
                break;
        }
        return false;
    }

    void compatirTexto(String texto) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, texto);
        startActivity(Intent.createChooser(i, "Selecciona aplicación"));
    }

    void compatirBitmap(Bitmap bitmap, String texto) { // guardamos bitmap en el directorio cache
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Obtenemos la URI usando el FileProvider
        File path = new File(getCacheDir(), "images");
        File file = new File(path, "image.png");
        Uri uri = FileProvider.getUriForFile(this, getPackageName()+".fileprovider", file); //Compartimos la URI
        if (uri != null) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // temp permission for receiving app to read this file
            i.setDataAndType(uri, getContentResolver().getType(uri));
            i.putExtra(Intent.EXTRA_STREAM, uri);
            i.putExtra(Intent.EXTRA_TEXT, texto);
            startActivity(Intent.createChooser(i, "Selecciona aplicación"));
        }
    }
}