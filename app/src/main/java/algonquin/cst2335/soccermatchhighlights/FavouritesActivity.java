package algonquin.cst2335.soccermatchhighlights;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;

import algonquin.cst2335.soccermatchhighlights.model.MyOpenHelper;
import algonquin.cst2335.soccermatchhighlights.model.SoccerMatch;


public class FavouritesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    MyOpenHelper myOpener;
    SQLiteDatabase theDatabase;
    RecyclerView recyclerView;
    MyRecyclerAdapter myAdapter;
    static ArrayList<SoccerMatch> matchesList = new ArrayList<>();
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        myOpener = new MyOpenHelper(this);
        theDatabase = myOpener.getWritableDatabase();
        Cursor results = theDatabase.rawQuery("Select * from Match;", null);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter = new MyRecyclerAdapter(matchesList));

        //Convert column names to indices:
        int idIndex = results.getColumnIndex(MyOpenHelper.COL_TITLE);
        int mealNameIndex = results.getColumnIndex(MyOpenHelper.COL_MEAL_NAME);
        int mealImageIndex = results.getColumnIndex(MyOpenHelper.COL_VIDEO);

        //cursor is pointing to row -1
        while (results.moveToNext()) { //pointing to row 2
            String id = results.getString(idIndex);
            String matchTitle = results.getString(mealNameIndex);
            String matchImage = results.getString(mealImageIndex);

            matchesList.add(new SoccerMatch(matchTitle, null, null, null, matchImage));
        }

        myAdapter.notifyDataSetChanged();

        // Get reference of widgets from XML layout
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                SoccerMatch clickedItem = myAdapter.getItem(position);

                String theMealID = myAdapter.getItem(position).getTitle();
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FavouritesActivity.this);
                dialogBuilder.setTitle(R.string.makeChoice)
                        .setMessage(R.string.detailsOrRemove)
                        .setPositiveButton(R.string.seeDetails, (click, arg) ->{
                            // a toast message with the name of the meal clicked
                            Toast.makeText(FavouritesActivity.this,
                                    "Results for " + myAdapter.getItem(position).getTitle(),
                                    Toast.LENGTH_SHORT).show();

                            // name of the meal clicked in a bundle to be passed to a fragment
                            Bundle fragmentData =new Bundle();
                            fragmentData.putString("idMeal", theMealID);

                            Intent recipeFrag = new Intent(FavouritesActivity.this, FragmentContainer.class);
                            recipeFrag.putExtras(fragmentData);
                            startActivity(recipeFrag);
                        })
                        .setNegativeButton(R.string.removeItem, (click, arg)->{
                            matchesList.remove(position);
                            myAdapter.notifyDataSetChanged();
                            theDatabase.delete(MyOpenHelper.TABLE_NAME, "_id=?",
                                    new String[]{theMealID});

                        })
                        .create().show();
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // do nothing
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch (item.getItemId()) {
            //what to do when the menu item is selected:
            case R.id.home_item:
                message = "You clicked on home.";
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                break;
            case R.id.matches_item:
                message = "You clicked on matches.";
                Intent ii = new Intent(getApplicationContext(), ResultActivity.class);
                startActivity(ii);
                break;
            case R.id.favourites_item:
                message = "You clicked on favourites.";
                // this will stop the activity and start it again, instead of starting
                // a new activity over the existent one.
                this.recreate();
                break;

        }
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String message = null;
        switch (item.getItemId()) {
            case R.id.drawerHome:
                Intent i1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i1);
                break;
            case R.id.drawerResults:
                Intent i2 = new Intent(getApplicationContext(), ResultActivity.class);
                startActivity(i2);
                break;
            case R.id.drawerFavourites:
                this.finish();
                this.startActivity(getIntent());
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {

        private List<SoccerMatch> matchesList;

        public MyRecyclerAdapter(List<SoccerMatch> matchesList) {
            this.matchesList = matchesList;
        }

        public SoccerMatch getItem(int position) {
            return matchesList.get(position);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.favourites_list_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            SoccerMatch match = matchesList.get(position);
            holder.matchTitle.setText(match.getTitle());
            Glide.with(holder.matchThumbnail.getContext())
                    .load(match.getUrl())
                    .centerCrop()
                    .into(holder.matchThumbnail);
        }

        @Override
        public int getItemCount() {
            return matchesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView matchTitle;
            public ImageView matchThumbnail;

            public MyViewHolder(View view) {
                super(view);
                matchTitle = view.findViewById(R.id.match_title);
                matchThumbnail = view.findViewById(R.id.match_thumbnail);
            }
        }
    }


    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
            void onLongItemClick(View view, int position);
        }

        private OnItemClickListener myListener;

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && myListener != null && myGestureDetector.onTouchEvent(e)) {
                myListener.onItemClick(childView, rv.getChildAdapterPosition(childView));
                return true;
            }

            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

        GestureDetector myGestureDetector;

        public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {

            myListener = listener;
            myGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {

                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    if (child != null && myListener != null) {
                        myListener.onItemClick(child, recyclerView.getChildAdapterPosition(child));
                    }

                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && myListener != null) {
                        myListener.onItemClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }
    }
}