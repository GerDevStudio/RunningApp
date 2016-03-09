package fr.gerdevstudio.runningapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Boolean mPlayAnimations = true;
    private Context mContext;
    private TextView mAucunEntrainement;
    private List<Training> mTrainings;
    private ListView mListView;
    private TrainingsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.mContext = this;

        // initializing views
        mAucunEntrainement = (TextView) findViewById(R.id.aucun_entrainement);
        mListView = (ListView) findViewById(R.id.list_view);

        // load Trainings
        SharedPrefsManager prefs = new SharedPrefsManager(this);
        TrainingList trainings = prefs.getTrainings();

        // initialize Adapter for list view
        if (trainings.getTrainings().isEmpty()) {
            mAucunEntrainement.setVisibility(View.VISIBLE);
        } else {
            mTrainings = trainings.getTrainings();

            //@todo permettre a l'adapter d'utiliser un type TrainingList directement
            mAdapter = new TrainingsAdapter(this, R.layout.listview_training_element, mTrainings);
            mListView.setAdapter(mAdapter);

            // initializing click to get detail of a training
            AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Training t = mTrainings.get(position);

                    // put information into intent
                    Intent i = new Intent(MainActivity.this, ResultActivity.class);

                    i.putExtra(ResultActivity.EXTRA_DISTANCE, t.getDistance());
                    i.putExtra(ResultActivity.EXTRA_DUREE, t.getDuree());
                    i.putExtra(ResultActivity.EXTRA_VITESSE, t.getVitesse());
                    i.putExtra(ResultActivity.EXTRA_DATE, DateUtils.dateToString(t.getDate()));
                    i.putExtra(ResultActivity.EXTRA_SAVE_TRAINING, false);
                    i.putExtra(ResultActivity.EXTRA_TRAINING_POSITION, position);
                    i.putParcelableArrayListExtra(ResultActivity.EXTRA_COORDINATES_LIST, t.getCoords());

                    // getting views informations
                    View date = view.findViewById(R.id.date);

                    // because shared element transitions requireds lollipop
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, date, "date");
                        startActivity(i, options.toBundle());
                        mPlayAnimations = false;
                    } else {
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
            };

            mListView.setClickable(true);
            mListView.setOnItemClickListener(listener);
        }

        //set fab to start Training activity with custom animation
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, TrainingActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // we can go back on main activity when a new training has been added.
        // we need to refresh adapter.

        // load Trainings
        SharedPrefsManager prefs = new SharedPrefsManager(this);
        TrainingList trainings = prefs.getTrainings();

        // initialize Adapter for list view
        if (trainings.getTrainings().isEmpty()) {
            mAucunEntrainement.setVisibility(View.VISIBLE);
        } else {
            mTrainings = trainings.getTrainings();

            mAdapter = new TrainingsAdapter(this, R.layout.listview_training_element, mTrainings);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (mPlayAnimations) {
            UiUtils.showFab(fab);

            UiUtils.appear(mListView, 1200);
        }
    }
}
