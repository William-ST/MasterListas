package org.example.masterlistas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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

import java.util.ArrayList;
import java.util.List;

public class ListasActivity extends AppCompatActivity {

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
                            Toast.makeText(ListasActivity.this, "Fetch OK", Toast.LENGTH_SHORT).show();
                            remoteConfig.activateFetched();
                        } else {
                            Toast.makeText(ListasActivity.this, "Fetch ha fallado", Toast.LENGTH_SHORT).show();
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
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Toast.makeText(getApplicationContext(), menuItem.getTitle(),
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
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
        items.add(new Lista(R.drawable.trabajo, "Trabajo", 2));
        items.add(new Lista(R.drawable.casa, "Personal", 3));
        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);
        // Usar un administrador para LinearLayout
        // lManager = new GridLayoutManager(this, 2);
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);
        // Crear un nuevo adaptador
        adapter = new ListaAdapter(items);
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
                Snackbar.make(view, "Se presionó el FAB", Snackbar.LENGTH_LONG).show();
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

}