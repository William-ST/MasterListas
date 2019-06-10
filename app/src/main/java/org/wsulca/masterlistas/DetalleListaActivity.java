package org.wsulca.masterlistas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

public class DetalleListaActivity extends AppCompatActivity {

    private final String TAG = DetalleListaActivity.class.getCanonicalName();
    private FloatingActionsMenu floatingActionsMenu;

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
            toolbar.setTitle(R.string.title_work);
            imageView.setImageResource(R.drawable.trabajo);
        } else {
            toolbar.setTitle(R.string.title_personal);
            imageView.setImageResource(R.drawable.casa);
        }

        floatingActionsMenu = findViewById(R.id.multiple_actions);

        getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                Log.d(TAG, "onTransitionStart");
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                floatingActionsMenu.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTransitionCancel(Transition transition) {
                Log.d(TAG, "onTransitionCancel");
            }

            @Override
            public void onTransitionPause(Transition transition) {
                Log.d(TAG, "onTransitionPause");
            }

            @Override
            public void onTransitionResume(Transition transition) {
                Log.d(TAG, "onTransitionResume");
            }
        });
    }

    @Override
    public void onBackPressed() {
        floatingActionsMenu.setAlpha(0f);
        super.onBackPressed();
    }

}
