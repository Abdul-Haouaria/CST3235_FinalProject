package algonquin.cst2335.soccermatchhighlights;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sp;
    private static final String SP_NAME = "myPref";
    EditText editTextVideos;
    ImageView imageView;
    Toolbar myToolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    ArrayList<Integer> randomImages = new ArrayList<>();
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(clickFab -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(R.string.help)
                    .setMessage(R.string.search_help)
                    .setNegativeButton(getString(R.string.close), (click, arg) -> {})
                    .create().show();
        });

        progressBar = findViewById(R.id.SR_progressbar);
        progressBar.setVisibility(View.INVISIBLE);

        randomImages.add(R.drawable.background0);
        randomImages.add(R.drawable.background1);
        randomImages.add(R.drawable.background2);
        randomImages.add(R.drawable.background3);

        editTextVideos = findViewById(R.id.editTextVideos);
        imageView = findViewById(R.id.imgRandom);

        //
        sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editTextVideos.setText(sp.getString("competition", ""));

        //
        Random random = new Random();
        int index = random.nextInt(randomImages.size());
        imageView.setImageResource((randomImages.get(index)));

        myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        findViewById(R.id.resultPageBtn).setOnClickListener(click -> {

            SharedPreferences.Editor editor = sp.edit();
            editor.putString("video", editTextVideos.getText().toString()).commit();

            progressBar.setVisibility(View.VISIBLE);

            Intent goToResult = new Intent(MainActivity.this, ResultActivity.class);

            goToResult.putExtra("video", editTextVideos.getText().toString());
            startActivity(goToResult);

            Handler handler = new Handler();
            handler.postDelayed(() -> progressBar.setVisibility(View.INVISIBLE), 2000); //3000 milliseconds
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = "Have a nice day!";
        //
        switch (item.getItemId()) {
            //
            case R.id.home_item:
                message = "You clicked on home.";
                this.recreate();
                break;
            case R.id.matches_item:
                message = "You clicked on matches.";
                Intent ii = new Intent(getApplicationContext(), ResultActivity.class);
                startActivity(ii);
                break;
            case R.id.favourites_item:
                message = "You clicked on favourites.";
                Intent iii = new Intent(getApplicationContext(), FavouritesActivity.class);
                startActivity(iii);
                break;
        }
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        String message = null;
        switch (item.getItemId()) {
            case R.id.drawerHome:
                this.finish();
                this.startActivity(getIntent());
                break;
            case R.id.drawerResults:
                Intent i2 = new Intent(getApplicationContext(), ResultActivity.class);
                startActivity(i2);
                break;
            case R.id.drawerFavourites:
                Intent i3 = new Intent(getApplicationContext(), FavouritesActivity.class);
                startActivity(i3);
                break;
        }
        //
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}