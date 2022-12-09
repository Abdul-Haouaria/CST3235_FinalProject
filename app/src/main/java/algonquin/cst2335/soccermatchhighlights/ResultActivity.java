package algonquin.cst2335.soccermatchhighlights;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import algonquin.cst2335.soccermatchhighlights.model.SoccerMatch;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class ResultActivity extends AppCompatActivity {

    // URL for the ScoreBat API
    private static final String URL = "https://www.scorebat.com/video-api/v1/";

    // RecyclerView and Adapter for displaying the list of matches
    private RecyclerView mRecyclerView;
    private MatchAdapter mAdapter;

    // SQLiteOpenHelper for accessing the database
    private static SQLiteOpenHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Create an instance of the SQLiteOpenHelper class
        mDBHelper = new MatchesDBHelper(this);

        // Get the RecyclerView from the layout
        mRecyclerView = findViewById(R.id.recycler_view_SR);

        // Set the layout manager for the RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create a new MatchAdapter with an empty list of matches
        List<SoccerMatch> matches = new ArrayList<>();
        mAdapter = new MatchAdapter(this, matches);

        // Set the Adapter for the RecyclerView
        mRecyclerView.setAdapter(mAdapter);

        // Get a RequestQueue for making network requests
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create a JsonArrayRequest for retrieving the list of matches from the API
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parse the response and update the Adapter with the list of matches
                        List<SoccerMatch> matches = parseResponse(response);
                        mAdapter.updateMatches(matches);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        error.printStackTrace();
                    }
                });

        // Add the request to the RequestQueue
        queue.add(request);
    }

    // Method for parsing the JSON response from the API
    private List<SoccerMatch> parseResponse(JSONArray response) {

        // Create a list to store soccer matches
        List<SoccerMatch> matchesToParse = new ArrayList<>();

        // Loop through the JSONArray and add matches to the list
        for (int i = 0; i < response.length(); i++) {

            try {

                JSONObject match = response.getJSONObject(i);

                // Parse the data for the match
                String title = match.getString("title");
                String video = match.getString("url");
                String date = match.getString("date");
                String team1 = match.getJSONObject("side1").getString("name");
                String team2 = match.getJSONObject("side2").getString("name");

                // Create a new Match object with the parsed data
                SoccerMatch m = new SoccerMatch(title, date, team1, team2, video);
                matchesToParse.add(m);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return matchesToParse;
    }

    // Method for opening a dialog showing the details for a match
    public void showMatchDetails(SoccerMatch match) {
        // Create a new MatchDetailsFragment and show it
        FragmentManager fm = getSupportFragmentManager();
        DetailsFragment fragment = DetailsFragment.newInstance(match);
        fragment.show(fm, "match_details");
    }

    // Method for adding a match to the database
    public void addMatchToFavorites(SoccerMatch match) {
        // Get a writable database
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // Create a new ContentValues object for the match
        ContentValues values = new ContentValues();
//        values.put(MatchesContract.MatchesEntry.COLUMN_NAME_TITLE, match.getTitle());
//        values.put(MatchesContract.MatchesEntry.COLUMN_NAME_DATE, match.getDate());
//        values.put(MatchesContract.MatchesEntry.COLUMN_NAME_TEAM1, match.getTeam1());
//        values.put(MatchesContract.MatchesEntry.COLUMN_NAME_TEAM2, match.getTeam2());
//        values.put(MatchesContract.MatchesEntry.COLUMN_NAME_URL, match.getUrl());
//
//        // Insert the new row into the database
//        long newRowId = db.insert(MatchesContract.MatchesEntry.TABLE_NAME, null, values);

        // Show a Toast confirming that the match was added to the database
//        String message = newRowId != -1 ? "Match added to favorites" : "Error adding match to favorites";
        Toast.makeText(this, "Match added to favourites", Toast.LENGTH_SHORT).show();
    }

    // Inner class for the database helper
    private class MatchesDBHelper extends SQLiteOpenHelper {

        public static final int VERSION = 1;
        public static final String DATABASE = "MatchesDatabase";
        public static final String TABLE_NAME = "Match";
        public static final String COL_TITLE = "TitleID";
        public static final String COL_MATCH_NAME = "MatchTitle";
        public static final String COL_VIDEO = "Video";

        public MatchesDBHelper(Context context) {
            super(context, DATABASE, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //Create table MyData ( _id INTEGER PRIMARY KEY AUTOINCREMENT, Message TEXT, SendOrReceive INTEGER);
            // String result = String.format(" %s %s %s", "FirstString" , "10", "10.0" );

            //                                      //TABLE_NAME               take care of id numbers
            db.execSQL( String.format( "Create table %s ( %s INTEGER PRIMARY KEY, %s TEXT, %s  TEXT);"
                    , TABLE_NAME, COL_TITLE, COL_MATCH_NAME, COL_VIDEO ) );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("Drop table if exists MatchesDatabase"); //deletes the current data
            //create a new table:

            this.onCreate(db);
        }
    }

    // Adapter subclass for displaying the list of matches in the RecyclerView
    private static class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

        private final Context mContext;
        private List<SoccerMatch> mMatches;

        public MatchAdapter(Context context, List<SoccerMatch> matches) {
            mContext = context;
            mMatches = matches;
        }

        // Method for updating the list of matches in the adapter
        public void updateMatches(List<SoccerMatch> matches) {
            mMatches = matches;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflate the item layout and create a new ViewHolder
            View view = LayoutInflater.from(mContext).inflate(R.layout.match_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // Bind the data for the item at the given position
            SoccerMatch match = mMatches.get(position);
            holder.bind(match);
        }

        @Override
        public int getItemCount() {
            return mMatches.size();
        }

        // ViewHolder subclass for displaying a match in the RecyclerView
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView mTitleView;
            private SoccerMatch mMatch;

            public ViewHolder(View itemView) {
                super(itemView);

                // Get the views from the layout
                mTitleView = itemView.findViewById(R.id.text_view_title);

                // Set the OnClickListener for the item
                itemView.setOnClickListener(this);
            }

            // Method for binding the data for a match to the views
            public void bind(SoccerMatch match) {
                mMatch = match;
                mTitleView.setText(match.getTitle());
            }

            @Override
            public void onClick(View view) {
                // When the user clicks the item, open the video URL in a browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mMatch.getUrl()));
                mContext.startActivity(intent);
            }
        }
    }

    // Fragment subclass for showing the details of a match
    public static class DetailsFragment extends DialogFragment {

        private static final String ARG_MATCH = "match";
        private SoccerMatch mMatch;

        public static DetailsFragment newInstance(SoccerMatch match) {
            DetailsFragment fragment = new DetailsFragment();
            fragment.mMatch = match;
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            // Inflate the layout for the fragment
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_details, null);

            // Get the views from the layout
            TextView dateView = view.findViewById(R.id.date);
            TextView team1View = view.findViewById(R.id.team1);
            TextView team2View = view.findViewById(R.id.team2);
            Button watchButton = view.findViewById(R.id.watch_button);
            Button saveButton = view.findViewById(R.id.save_button);

            // Set the data for the views
            dateView.setText(mMatch.getDate());
            team1View.setText(mMatch.getTeam1());
            team2View.setText(mMatch.getTeam2());

            // Set the OnClickListener for the watch button
            watchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get the URL for the match highlights
                    String url = mMatch.getUrl();

                    // When the user clicks the item, open the video URL in a browser
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mMatch.getUrl()));
                    getActivity().startActivity(intent);
                }
            });

            // Set the OnClickListener for the save button
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Save the match details in the database
                    SQLiteDatabase db = mDBHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("title", mMatch.getTitle());
                    values.put("date", mMatch.getDate());
                    values.put("team1", mMatch.getTeam1());
                    values.put("team2", mMatch.getTeam2());
                    values.put("url", mMatch.getUrl());
                    db.insert("matches", null, values);

                    // Show a Toast to confirm that the match was saved
                    Toast.makeText(getActivity(), "Match saved", Toast.LENGTH_SHORT).show();

                    // Dismiss the fragment
                    dismiss();
                }
            });

            // Return the fragment view
            return view;
        }
    }
}