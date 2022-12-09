package algonquin.cst2335.soccermatchhighlights.model;


public class SoccerMatch {

    // Fields for the title, date, and teams for the match
    private String mTitle;
    private String mDate;
    private String mTeam1;
    private String mTeam2;
    private String mUrl;

    // Constructor for creating a new SoccerMatch
    public SoccerMatch(String title, String date, String team1, String team2, String url) {
        mTitle = title;
        mDate = date;
        mTeam1 = team1;
        mTeam2 = team2;
        mUrl = url;
    }

    // Getters for the fields
    public String getTitle() { return mTitle; }
    public String getDate() { return mDate; }
    public String getTeam1() { return mTeam1; }
    public String getTeam2() { return mTeam2; }
    public String getUrl() { return mUrl; }
}
