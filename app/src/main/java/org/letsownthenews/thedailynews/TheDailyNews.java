package org.letsownthenews.thedailynews;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class TheDailyNews extends Activity {

    private ViewGroup container;
    private View intro;
    private View summary;
    private View newsroom;
    private DecimalFormat percent = new DecimalFormat("0%");
    private DecimalFormat deltaPercent = new DecimalFormat("+0%;-0%");
    private DecimalFormat oneDp = new DecimalFormat("0.0");

    private Button conservativeButton;
    private Button labourButton;
    private Button libdemButton;
    private Button ukipButton;
    private Button newsroomButton;
    private Button printButton;

    private enum Party { CONSERVATIVE, LABOUR, LIBDEM, UKIP; }
    private enum Result { CONSERVATIVE, LABOUR, HUNG, OTHER };
    private Party selectedParty = null;
    private List<Story> stories = new ArrayList<Story>();
    private List<Story> selectedStories = new ArrayList<Story>();

    private int currentWeek = 0;

    private float readership = 1.0f;  // total readership in millions
    private int popularity = 200;  // starting popularity of politicians
    private int bias = 0;  // how biased is the editor?

    // initial readership breakdown
    private float conservativeReadership = 0.25f;
    private float labourReadership = 0.25f;
    private float libdemReadership = 0.25f;
    private float ukipReadership = 0.25f;

    // initial poll breakdown (sum to 1)
    private float conservativeVoteShare = 0.32f;
    private float labourVoteShare = 0.34f;
    private float libdemVoteShare = 0.19f;
    private float ukipVoteShare = 0.15f;

    // initial poll change
    private float conservativeVoteChange = 0.0f;
    private float labourVoteChange = 0.0f;
    private float libdemVoteChange = 0.0f;
    private float ukipVoteChange = 0.0f;

    // initial seats
    private int conservativeSeats = 248;
    private int labourSeats = 322;
    private int libdemSeats = 50;
    private int ukipSeats = 3;

    //
    private int timeBonus = 10;
    private TimerTask timeBonusTask;
    private Timer timeBonusTimer = new Timer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.the_daily_news);
        container = (ViewGroup)findViewById(R.id.container);
        intro = View.inflate(this, R.layout.intro, null);
        summary = View.inflate(this, R.layout.summary, null);
        newsroom = View.inflate(this, R.layout.newsroom, null);

        // set up the stories (bit tedious..)
        stories.add(new Story(1,"Macro","Government figures reveal immigration was 130,000 last year down from 150,000, claims policy has been successful","Britain flooded with 130,000 people seeking jobs",4,2,-3,4,2,0,0));
        stories.add(new Story(1,"Macro","Government figures reveal immigration was 130,000 last year down from 150,000, claims policy has been successful","Immigration falls to 130,000",1,0,1,-1,1,0,0));
        stories.add(new Story(1,"Macro","Government figures reveal immigration was 130,000 last year down from 150,000, claims policy has been successful","Government tackles immigration",-3,0,3,-2,2,-1,2));

        stories.add(new Story(1,"Tory","David Cameron's son has 8th birthday party and is given £1m in trust","Cameron gives £1m to son",2,0,-4,0,-2,0,0));
        stories.add(new Story(1,"Tory","David Cameron's son has 8th birthday party and is given £1m in trust","Cameron relaxes at son's birthday",-1,0,0,0,1,0,0));

        stories.add(new Story(1,"Labour","Ed Milliband talks to union conference on rising inequality","Red Ed returns",0,0,-1,1,1,-2,0));
        stories.add(new Story(1,"Labour","Ed Milliband talks to union conference on rising inequality","Ed stands up for working poor",-2,0,1,-1,-1,3,0));
        stories.add(new Story(1,"Labour","Ed Milliband talks to union conference on rising inequality","Ed calls on unions to srike",2,2,-1,0,0,-4,0));

        stories.add(new Story(1,"UKIP","UKIP leader Nigel Farage files tax return late confusing online and paper deadlines.","Nigel Farage avoids tax",2,1,-2,-3,0,0,0));
        stories.add(new Story(1,"UKIP","UKIP leader Nigel Farage files tax return late confusing online and paper deadlines.","Nigel Farage submits tax return late",0,0,-2,-1,0,0,0));
        stories.add(new Story(1,"UKIP","UKIP leader Nigel Farage files tax return late confusing online and paper deadlines.","Farage caught wrong side of government bureaucracy",1,1,2,3,0,0,0));

        stories.add(new Story(1,"Lib dem","Nick Clegg speaks to students about investment in research. Booed by a small group holding tuition fee posters.","Tuition fees still haunts Clegg",1,0,-2,0,0,0,-2));
        stories.add(new Story(1,"Lib dem","Nick Clegg speaks to students about investment in research. Booed by a small group holding tuition fee posters.","Clegg announces more research investment",0,0,1,0,0,0,1));

        stories.add(new Story(1,"Celeb","Shouting heard coming from the Beckham's house","Beckham bust up",6,0,-2,0,0,0,0));






        stories.add(new Story(2,"Macro","A-level results, pupils getting A grades rises from 25% to 30%.","Education standards rising",-2,0,1,0,3,0,1));
        stories.add(new Story(2,"Macro","A-level results, pupils getting A grades rises from 25% to 30%.","30% get A grades at A-level",0,0,0,0,0,0,0));
        stories.add(new Story(2,"Macro","A-level results, pupils getting A grades rises from 25% to 30%.","A-levels getting easier",2,0,-2,0,-2,0,0));

        stories.add(new Story(2,"","","Cameron in elitist knees up",3,0,-1,0,-3,1,0));
        stories.add(new Story(2,"","","Cameron visits school for the socially impaired",1,0,-2,-1,1,0,0));
        stories.add(new Story(2,"","","Cameron sent back to school to learn arithmetic",2,0,-3,1,-2,1,1));

        stories.add(new Story(2,"Tory","Boris Johnson seen talking to Nigel Farage in a pub in Essex.","Boris may defect to UKIP",1,2,0,4,-2,0,0));
        stories.add(new Story(2,"Tory","Boris Johnson seen talking to Nigel Farage in a pub in Essex.","Boris talks to Nigel Farage",-1,0,0,1,-1,0,0));
        stories.add(new Story(2,"Tory","Boris Johnson seen talking to Nigel Farage in a pub in Essex.","Boris gets drunk with Nigel",3,2,-2,-1,-1,0,0));

        stories.add(new Story(2,"Labour","Ed Milliband caught speeding on the M11 in his Mini Cooper","Ed Milliband caught speeding",1,0,-2,0,1,-2,1));
        stories.add(new Story(2,"Labour","Ed Milliband caught speeding on the M11 in his Mini Cooper","Ed Milliband drives a Mini Cooper",-2,0,1,0,0,1,0));
        stories.add(new Story(2,"Labour","Ed Milliband caught speeding on the M11 in his Mini Cooper","Miliband calls for higher speed limits",0,2,2,0,0,3,0));

        stories.add(new Story(2,"UKIP","UKIP MP candidate caught saying he doesn't like Romanians. Farage says it is a one-off and the candidate has been expelled from the party.","UKIP still racist",2,0,-3,-4,0,0,0));
        stories.add(new Story(2,"UKIP","UKIP MP candidate caught saying he doesn't like Romanians. Farage says it is a one-off and the candidate has been expelled from the party.","UKIP expels member for racist comments",1,0,-1,-1,0,0,0));

        stories.add(new Story(2,"Celeb","Rio Ferdinand hosts £1m birthday party","Rio's £1m birthday bash",6,0,-2,0,0,0,0));






        stories.add(new Story(3,"Macro","Immigration figures show 10,000 Romanians have moved to UK below concerns of more than 100,000","10,000 Romanians flood to UK",3,2,-2,5,1,-1,-3));
        stories.add(new Story(3,"Macro","Immigration figures show 10,000 Romanians have moved to UK below concerns of more than 100,000","Immigration from Romania less than feared",0,0,1,-2,-1,0,1));
        stories.add(new Story(3,"Macro","Immigration figures show 10,000 Romanians have moved to UK below concerns of more than 100,000","Government controls immigration",-2,0,2,-2,3,0,2));

        stories.add(new Story(3,"Lib dem","Vince Cable speech on the need for a clear Lib Dem message and the need for further reform of the banking industry.","Vince Cable makes bid for leadership",0,1,-1,0,0,0,-1));
        stories.add(new Story(3,"Lib dem","Vince Cable speech on the need for a clear Lib Dem message and the need for further reform of the banking industry.","Vince Cable attacks banks",0,0,1,0,0,0,4));
        stories.add(new Story(3,"Lib dem","Vince Cable speech on the need for a clear Lib Dem message and the need for further reform of the banking industry.","Vince Cable proposes minimum wage for bankers",1,3,2,0,0,0,5));

        stories.add(new Story(3,"Macro","UK hosts climate change summit with the USA","David Cameron takes climate change lead",0,0,2,-1,4,-1,-1));
        stories.add(new Story(3,"Macro","UK hosts climate change summit with the USA","US snubs UK at summit",1,1,-1,0,-1,0,0));
        stories.add(new Story(3,"Macro","UK hosts climate change summit with the USA","Cameron is US President's pet",3,0,-2,0,-2,0,0));

        stories.add(new Story(3,"Tory","Tory minister resigns over governments stance on Israel","Minister resigns over Israel",1,0,2,0,-1,0,0));
        stories.add(new Story(3,"Tory","Tory minister resigns over governments stance on Israel","Minister quits, says Government stance is immoral",3,0,2,0,-2,0,-1));
        stories.add(new Story(3,"Tory","Tory minister resigns over governments stance on Israel","Cameron fires incompetent minister",-1,2,-1,0,1,0,0));

        stories.add(new Story(3,"Labour","Ed Miliband visits Obama ","Miliband discusses global issues with Obama",0,0,2,-1,-1,2,-1));
        stories.add(new Story(3,"Labour","Ed Miliband visits Obama ","Miliband on US visit",0,0,0,0,0,1,0));
        stories.add(new Story(3,"Labour","Ed Miliband visits Obama ","Miliband goes on a tourist tour of White House",2,0,-2,0,0,-2,0));

        stories.add(new Story(3,"Celeb","Prince Harry lookalike found on Tinder","Is Prince Harry on Tinder?",6,0,-2,0,0,0,0));







        stories.add(new Story(4,"Macro","GDP growth increases to 2%. GDP remains below 2007 peak. Debt increasing at record levels.","Economy bounces back",-1,0,2,-1,4,0,2));
        stories.add(new Story(4,"Macro","GDP growth increases to 2%. GDP remains below 2007 peak. Debt increasing at record levels.","Debt uncontrollable",1,0,-1,0,-1,0,0));
        stories.add(new Story(4,"Macro","GDP growth increases to 2%. GDP remains below 2007 peak. Debt increasing at record levels.","We're still poorer than 2007",2,0,-1,1,-2,1,-1));

        stories.add(new Story(4,"Macro","Plans to increase NHS spending with up to 5% going to private contractors","Government readies to privatise NHS",2,2,-2,0,-2,1,0));
        stories.add(new Story(4,"Macro","Plans to increase NHS spending with up to 5% going to private contractors","NHS spending increased",-1,0,1,0,2,0,1));

        stories.add(new Story(4,"Tory","Boris says Tories could get message across better. Says Cameron ","Split in Tory party",2,0,-1,1,-2,0,0));
        stories.add(new Story(4,"Tory","Boris says Tories could get message across better. Says Cameron ","Boris calls for more conviction politicians",0,0,1,0,-1,0,0));

        stories.add(new Story(4,"Labour","Labour announces living wage plans, CBI says it would be be a burden on business.","Labour vows to tackle in-work poverty",-2,0,1,-1,-1,1,-1));
        stories.add(new Story(4,"Labour","Labour announces living wage plans, CBI says it would be be a burden on business.","Labour's plans would burden UK businesses",-1,0,-1,0,0,-3,0));
        stories.add(new Story(4,"Labour","Labour announces living wage plans, CBI says it would be be a burden on business.","Labour guarantees everyone good pay",0,2,2,-1,-1,3,-1));

        stories.add(new Story(4,"Lib Dem","Nick Clegg says Lib Dems will not partner with Tories after next election","Lib dems rule out another Tory partnership",0,0,1,0,0,0,8));

        stories.add(new Story(4,"Celeb","Baby Prince George takes first steps","Prince George struts his stuff",6,0,-2,0,0,0,0));







        stories.add(new Story(5,"Macro","Unemployment falls to 6.5%. Falls in the south, rises in the north.","Unemployment falls",0,0,2,0,2,-1,1));
        stories.add(new Story(5,"Macro","Unemployment falls to 6.5%. Falls in the south, rises in the north.","North-South divide widens",3,0,-2,0,-2,1,-1));

        stories.add(new Story(5,"Tory","Cameron announces industrial investment in the North","Tories last ditch effort to win support in the North",1,0,-2,0,-2,0,0));
        stories.add(new Story(5,"Tory","Cameron announces industrial investment in the North","Tories back Northern business",-2,0,0,-1,3,-1,-1));

        stories.add(new Story(5,"UKIP","UKIP announces general election manifesto, 3 key policies","UKIP commits to EU referendum",0,0,0,3,0,0,0));
        stories.add(new Story(5,"UKIP","UKIP announces general election manifesto, 3 key policies","UKIP would stop immigration",0,0,0,3,0,0,0));
        stories.add(new Story(5,"UKIP","UKIP announces general election manifesto, 3 key policies","UKIP would privatise the NHS",1,1,0,-2,0,0,0));

        stories.add(new Story(5,"Tory","Senior Tory MP says may defect to UKIP","Tories may lose more MPs to UKIP",1,0,0,2,-1,1,1));

        stories.add(new Story(5,"Labour","Ed Miliband eats fish and chips, but drops it all over street.","Ed 'fumble fingers' Miliband fails at chippy",2,1,-2,0,0,-2,0));
        stories.add(new Story(5,"Labour","Ed Miliband eats fish and chips, but drops it all over street.","Ed Miliband tucks into fish and chips",0,0,0,0,0,1,0));

        stories.add(new Story(5,"Lib dems","Nick Clegg calls for Lib Dems to return to their roots","Lib Dems to all live on an organic farm",2,3,-2,0,0,0,-3));
        stories.add(new Story(5,"Lib dems","Nick Clegg calls for Lib Dems to return to their roots","Who is Nick Clegg?",2,3,-2,0,0,0,-3));
        stories.add(new Story(5,"Lib dems","Nick Clegg calls for Lib Dems to return to their roots","Lib Dems to return to liberal values",0,0,0,0,0,0,3));






        stories.add(new Story(6,"Macro","House prices rise 10% last month","House price boom out of control",2,0,-1,1,-2,1,-1));
        stories.add(new Story(6,"Macro","House prices rise 10% last month","Housing market recovers",1,0,1,0,2,-1,1));

        stories.add(new Story(6,"UKIP","Nigel Farage says Cameron is out of touch with ordinary people","Farage says Cameron is a toff",0,0,0,2,-2,0,0));
        stories.add(new Story(6,"UKIP","Nigel Farage says Cameron is out of touch with ordinary people","Farage tells kettle its black",0,0,0,-2,2,0,0));

        stories.add(new Story(6,"Tory","Tory minister affair with former Spice Girl Geri Haliwell","Tory minister spices it up",6,0,-2,0,-1,0,0));
        stories.add(new Story(6,"Tory","Tory minister affair with former Spice Girl Geri Haliwell","Tory in affair with Geri",6,0,-2,0,-1,0,0));
        stories.add(new Story(6,"Tory","Tory minister affair with former Spice Girl Geri Haliwell","Tory leaves wife for Spice Girl",6,0,-2,0,-2,0,0));

        stories.add(new Story(6,"Govt","Government approves fracking in North England","Government to start fracking boom in the North",0,0,1,-1,2,-1,-1));
        stories.add(new Story(6,"Govt","Government approves fracking in North England","Fracking concerns grow after government approval",0,0,-1,0,-2,0,-1));

        stories.add(new Story(6,"Labour","Labour offices spends £100k on new efficient lighting","Shadow cabinet scared of own shadow",2,2,-2,0,0,-3,0));
        stories.add(new Story(6,"Labour","Labour offices spends £100k on new efficient lighting","Labour installs green lighting",1,0,1,0,0,1,0));

        stories.add(new Story(6,"Celeb","England football team lose 5-0 to Germany","England football team lets down country",5,0,-1,0,0,0,0));






        stories.add(new Story(7,"Tory","Michael Gove is seen near No.10","Gove may return to Education department after election",0,0,0,0,-2,0,0));
        stories.add(new Story(7,"Tory","Michael Gove is seen near No.10","Gove may step down this election",0,0,1,0,2,0,0));

        stories.add(new Story(7,"Tory","David Cameron holds impressive photoshoot","Cameron ready for a second term",-1,0,0,0,2,0,0));
        stories.add(new Story(7,"Tory","David Cameron holds impressive photoshoot","Cameron is looking tired",0,0,0,0,-1,0,0));
        stories.add(new Story(7,"Tory","David Cameron holds impressive photoshoot","Cameron caught in shameless publicity stunt",0,0,-2,0,-2,0,0));

        stories.add(new Story(7,"Labour","Heading home from volunteering at a soup kitchen Miliband gets lost in Manchester","Ed 'no direction' Miliband gets lost in Manchester",2,0,-1,0,0,-2,0));
        stories.add(new Story(7,"Labour","Heading home from volunteering at a soup kitchen Miliband gets lost in Manchester","Miliband hands out soup to homeless",-1,0,2,-1,-1,1,-1));
        stories.add(new Story(7,"Labour","Heading home from volunteering at a soup kitchen Miliband gets lost in Manchester","Miliband gets free meal from soup kitchen",1,2,-2,0,0,-3,0));

        stories.add(new Story(7,"Labour","You learn that Miliband had a bust of Lenin on his student desk","Miliband is fan of Lenin",3,3,-1,0,0,-3,0));
        stories.add(new Story(7,"Labour","You learn that Miliband had a bust of Lenin on his student desk","Miliband's idealistic youth",1,0,1,0,0,-1,0));

        stories.add(new Story(7,"UKIP","UKIP shadow minister punches protestor in a scuffle","UK senior member punches protestor",2,0,-2,-3,0,0,0));
        stories.add(new Story(7,"UKIP","UKIP shadow minister punches protestor in a scuffle","Mob attack UKIP event",1,0,0,-1,0,0,0));

        stories.add(new Story(7,"Lib Dem","Nick Clegg stands up for assylum seekers rights","Nick Clegg defends UK liberal values",0,0,1,0,0,0,2));
        stories.add(new Story(7,"Lib Dem","Nick Clegg stands up for assylum seekers rights","Clegg would make UK a home for extremists",0,0,-1,0,0,0,-2));

        showIntro();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showIntro() {

        // switch to intro screen
        container.removeAllViews();
        container.addView(intro);

        conservativeButton = (Button)findViewById(R.id.intro_conservative_button);
        labourButton = (Button)findViewById(R.id.intro_labour_button);
        libdemButton = (Button)findViewById(R.id.intro_libdem_button);
        ukipButton = (Button)findViewById(R.id.intro_ukip_button);

        conservativeButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                  selectedParty = Party.CONSERVATIVE;
                                                    showSummary();
                                                }
                                             }
        );

        labourButton.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      selectedParty = Party.LABOUR;
                                                      showSummary();
                                                  }
                                              }
        );

        libdemButton.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      selectedParty = Party.LIBDEM;
                                                      showSummary();
                                                  }
                                              }
        );

        ukipButton.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      selectedParty = Party.UKIP;
                                                      showSummary();
                                                  }
                                              }
        );

    }

    private void showSummary() {

        // show summary view
        container.removeAllViews();
        container.addView(summary);

        // title
        TextView summaryTitle = (TextView) findViewById(R.id.summary_title_text);
        summaryTitle.setText("Week " + currentWeek + " Summary");

        // summary box
        timeBonus = 10;
        final TextView summaryTimeBonus = (TextView) findViewById(R.id.summary_time_bonus);
        summaryTimeBonus.setText("Time bonus " + oneDp.format(timeBonus));
        TextView summaryStorySelection = (TextView) findViewById(R.id.summary_story_selection);
        summaryStorySelection.setText("Story selection " + "xxx");
        TextView summaryReadership = (TextView) findViewById(R.id.summary_readership);
        summaryReadership.setText("Readership " + oneDp.format(readership) + "m");

        // start the timeBonusTimer
        timeBonusTask = new TimerTask() {
            @Override
            public void run() {
                timeBonus--;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        summaryTimeBonus.setText("Time bonus " + oneDp.format(timeBonus));
                    }
                });
            }
        };
        timeBonusTimer.scheduleAtFixedRate(timeBonusTask, 2000, 2000);

        // poll box
        TextView conservativeVoteShareTextView = (TextView) findViewById(R.id.conservative_vote_share);
        conservativeVoteShareTextView.setText(percent.format(conservativeVoteShare));
        TextView conservativeVoteChangeTextView = (TextView) findViewById(R.id.conservative_vote_change);
        conservativeVoteChangeTextView.setText(deltaPercent.format(conservativeVoteChange));
        TextView conservativeSeatPredictionTextView = (TextView) findViewById(R.id.conservative_seat_prediction);
        conservativeSeatPredictionTextView.setText(String.valueOf(conservativeSeats));

        TextView labourVoteShareTextView = (TextView) findViewById(R.id.labour_vote_share);
        labourVoteShareTextView.setText(percent.format(labourVoteShare));
        TextView labourVoteChangeTextView = (TextView) findViewById(R.id.labour_vote_change);
        labourVoteChangeTextView.setText(deltaPercent.format(labourVoteChange));
        TextView labourSeatPredictionTextView = (TextView) findViewById(R.id.labour_seat_prediction);
        labourSeatPredictionTextView.setText(String.valueOf(labourSeats));

        TextView libdemVoteShareTextView = (TextView) findViewById(R.id.libdem_vote_share);
        libdemVoteShareTextView.setText(percent.format(libdemVoteShare));
        TextView libdemVoteChangeTextView = (TextView) findViewById(R.id.libdem_vote_change);
        libdemVoteChangeTextView.setText(deltaPercent.format(libdemVoteChange));
        TextView libdemSeatPredictionTextView = (TextView) findViewById(R.id.libdem_seat_prediction);
        libdemSeatPredictionTextView.setText(String.valueOf(libdemSeats));

        TextView ukipVoteShareTextView = (TextView) findViewById(R.id.ukip_vote_share);
        ukipVoteShareTextView.setText(percent.format(ukipVoteShare));
        TextView ukipVoteChangeTextView = (TextView) findViewById(R.id.ukip_vote_change);
        ukipVoteChangeTextView.setText(deltaPercent.format(ukipVoteChange));
        TextView ukipSeatPredictionTextView = (TextView) findViewById(R.id.ukip_seat_prediction);
        ukipSeatPredictionTextView.setText(String.valueOf(ukipSeats));

        // update message
        TextView summaryUpdate = (TextView) findViewById(R.id.summary_update_text);
        summaryUpdate.setText("TODO: Week " + currentWeek + " update message");

        // have to add a button listener every time, as it get ditched when we clear the container
        newsroomButton = (Button) findViewById(R.id.summary_newsroom_button);
        newsroomButton.setText("Go to week " + (currentWeek+1) + " newsroom");
        newsroomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeBonusTask.cancel();
                showNewsroom();
            }
        });

        // override some UI when we reach the election:

        if (currentWeek > 6) {
            summaryTitle.setText("General election result");
            TextView pollTitle = (TextView) findViewById(R.id.summary_poll_title);
            pollTitle.setText("Election result");

            // calculate score
            int finalScore = 0;
            switch (selectedParty) {
                case CONSERVATIVE: finalScore = Math.round(-223 + conservativeSeats * 0.98f); break;
                case LABOUR: finalScore = Math.round(-47 + labourSeats*0.41f); break;
                case UKIP: finalScore = Math.round(82 + ukipSeats*1.8f); break;
                case LIBDEM: finalScore = Math.round(-250 + libdemSeats*5.0f); break;
            }

            // calculate result
            Result result = Result.OTHER;
            if (labourSeats > 325) result = Result.LABOUR;
            if (conservativeSeats > 325) result = Result.CONSERVATIVE;
            if (labourSeats + libdemSeats > 325) result = Result.HUNG;

            String resultMessage;
            switch (result) {
                case CONSERVATIVE: {
                    if (selectedParty == Party.CONSERVATIVE) {
                        resultMessage = "Well done you have helped create a Conservative majority government.";
                    } else if (selectedParty == Party.UKIP) {
                        resultMessage = "The Conservatives form a majority government.";
                    } else {
                        resultMessage = "You have failed, the Conservatives form a majority government.";
                    }
                    break;
                }
                case LABOUR: {
                    if (selectedParty == Party.LABOUR) {
                        resultMessage = "Well done you have helped create a Labour majority government";
                    } else if (selectedParty == Party.UKIP) {
                        resultMessage = "Labour form a majority government.";
                    } else {
                        resultMessage = "You have failed, Labour form a majority government.";
                    }
                    break;
                }
                case HUNG: {
                    if (selectedParty == Party.LABOUR) {
                        resultMessage = "It is a hung parliament, the Lib Dems form an uneasy pact with Labour.";
                    } else if (selectedParty == Party.CONSERVATIVE) {
                        resultMessage = "You have failed, it's is a hung parliament, the Lib Dems form an uneasy pact with Labour.";
                    } else if (selectedParty == Party.LIBDEM) {
                        resultMessage = "Well done, it is a hung parliament, the Lib Dems hold the balance of power and form a coalition with Labour.";
                    } else {  // UKIP
                        resultMessage = "It is a hung parliament, the Lib Dems hold the balance of power and form a coalition with Labour.";
                    }
                    break;
                }
                default: resultMessage = "After no clear winner in the election, coalition negotiations breakdown and another general election is called"; break;
            }
            // append a UKIP message
            if (selectedParty == Party.UKIP) {
                if (ukipSeats < 1) {
                    resultMessage += " A disappointing result for UKIP with no MPs elected";
                } else if (ukipSeats < 3) {
                    resultMessage += " You helped elect UKIPs first MPs, but there is a feeling they could have done better with more press support";
                } else {
                    resultMessage += "Well done, you helped get UKIP's first MPs elected.";
                }
            }

            String finalMessage;
            final String shareMessage;
            if (finalScore < 90) {
                finalMessage = "You are a slave to the truth, your honesty is only matched by your stupidity, you are no press baron";
                shareMessage = "slave to the truth";
            } else if (finalScore < 140) {
                finalMessage = "Rookie press baron, you’re pulling the strings but still care about the truth.";
                shareMessage = "rookie press baron";
            } else {
                finalMessage = "You are a media mogul in waiting, the truth can be moulded to your every whim and desire";
                shareMessage = "mega media mogul";
            }

            String shareResult = "error";
            switch(result) {
                case CONSERVATIVE: shareResult = "achieve a Conservative victory."; break;
                case LABOUR: shareResult = "achieve a Labour victory."; break;
                case HUNG: shareResult = "achieve a hung parliament."; break;
                case OTHER: shareResult = "???"; break;  //TODO: what should this be? UKIP may or may not have got seats.
            }


            // display score
            //TODO: final message
            summaryUpdate.setText("Final Score: " + finalScore);
            newsroomButton.setText("Share score");
            final int finalFinalScore = finalScore;
            final String finalShareResult = "error";
            newsroomButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT,"I just scored " + finalFinalScore + " and am a " + shareMessage + " on The Daily News, manipulating headlines to " + finalShareResult);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "The daily news score!");
                    startActivity(Intent.createChooser(shareIntent, "Share..."));
                }
            });

        }

    }

    private void showNewsroom() {

        // switch to newsroom screen, add stories for current week
        container.removeAllViews();
        container.addView(newsroom);
        currentWeek++;

        // update title
        TextView newsroomTitle = (TextView)findViewById(R.id.newsroom_title_text);
        newsroomTitle.setText("Week " + currentWeek + " Newsroom");

        // assemble the stories for the current week
        ViewGroup newsroomContent = (ViewGroup)findViewById(R.id.newsroom_content);
        newsroomContent.removeAllViews();
        String lastStory = null;
        for (Story s : stories) {
            if (s.week == currentWeek) {
                if (lastStory == null || !lastStory.equals(s.text)) {
                    // Add textview for story text
                    TextView text = new TextView(TheDailyNews.this);
                    text.setText(s.text);
                    newsroomContent.addView(text);
                    lastStory = s.text;
                }
                // Add title
                Button title = new Button(TheDailyNews.this);
                title.setText(s.title);
                final Story s2 = s;
                title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedStories.contains(s2)) {
                            selectedStories.remove(s2);
                            v.setBackgroundColor(Color.parseColor("#888888"));
                        } else {
                            selectedStories.add(s2);
                            v.setBackgroundColor(Color.MAGENTA);
                        }
                    }
                });
                newsroomContent.addView(title);
            }
        }

        printButton = (Button) findViewById(R.id.newsroom_print_button);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check there are 4 selected titles
                if (selectedStories.size() == 4) {
                    updateStats();
                    selectedStories.clear();
                    showSummary();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TheDailyNews.this);
                    builder.setMessage("You must select exactly 4 titles").setTitle("Error").create().show();
                }
            }
        });

    }

    private void updateStats() {
        // update stats
        int readershipDelta = 0;
        int popularityDelta = 0;
        int conservativeDelta = 0;
        int labourDelta = 0;
        int libdemDelta = 0;
        int ukipDelta = 0;
        for (Story s2 : selectedStories) {
            readershipDelta += s2.readership;
            popularityDelta += s2.popularity;
            conservativeDelta += s2.conservative;
            labourDelta += s2.labour;
            libdemDelta += s2.libdem;
            ukipDelta += s2.ukip;
        }

        // TODO: have these formulae changed?
        readership = 0.9f*readership + (readershipDelta * (1+timeBonus))/25.0f;
        //popularity = popularity + (int)readership*popularityDelta;   // not currently used

        int pointsDifferential = 0;
        boolean biasFlag = true;
        switch (selectedParty) {
            case CONSERVATIVE: {
                pointsDifferential = conservativeDelta - labourDelta - libdemDelta - ukipDelta;
                for (Story s : selectedStories) {
                    if (s.conservative < 0) biasFlag = false;
                    //bias++;
                }
                break;
            }
            case LABOUR: {
                pointsDifferential = -conservativeDelta + labourDelta - libdemDelta - ukipDelta;
                for (Story s : selectedStories) {
                    if (s.labour < 0) biasFlag = false;
                    //bias++;
                }
                break;
            }
            case LIBDEM: {
                pointsDifferential = -conservativeDelta - labourDelta + libdemDelta - ukipDelta;
                for (Story s : selectedStories) {
                    if (s.libdem < 0) biasFlag = false;
                    //bias++;
                }
                break;
            }
            case UKIP: {
                pointsDifferential = -conservativeDelta - labourDelta - libdemDelta + ukipDelta;
                for (Story s : selectedStories) {
                    if (s.ukip < 0) biasFlag = false;
                    //bias++;
                }
                break;
            }
        }

        if (pointsDifferential > 4 && biasFlag) bias++;

        // calculate new readership breakdown
        float readershipNormalisation = 1 + (conservativeDelta + labourDelta + libdemDelta + ukipDelta)*readership/800.0f;
        conservativeReadership += conservativeDelta/800.0f*readership/readershipNormalisation;
        labourReadership += labourDelta/800.0f*readership/readershipNormalisation;
        libdemReadership += libdemDelta/800.0f*readership/readershipNormalisation;
        ukipReadership += ukipDelta/800.0f*readership/readershipNormalisation;

        float totalReadershipBreakdown = conservativeReadership + labourReadership + libdemReadership + ukipReadership;
        if (totalReadershipBreakdown != 1.0f) {
            Log.e("showSummary", "Readership drifted to " + totalReadershipBreakdown);
        }

        // calculate new likely vote share
        float newConservativeVoteShare = conservativeVoteShare * conservativeReadership * 4;
        float newLabourVoteShare = labourVoteShare * labourReadership * 4;
        float newLibdemVoteShare = libdemVoteShare * libdemReadership * 4;
        float newUkipVoteShare = ukipVoteShare * ukipReadership * 4;

        // normalise vote share
        float voteShareNormalisation = newConservativeVoteShare + newLabourVoteShare + newLibdemVoteShare + newUkipVoteShare;
        newConservativeVoteShare /= voteShareNormalisation;
        newLabourVoteShare /= voteShareNormalisation;
        newLibdemVoteShare /= voteShareNormalisation;
        newUkipVoteShare /= voteShareNormalisation;

        // calculate change from last week
        conservativeVoteChange = newConservativeVoteShare - conservativeVoteShare;
        labourVoteChange = newLabourVoteShare - labourVoteShare;
        libdemVoteChange = newLibdemVoteShare - libdemVoteShare;
        ukipVoteChange = newUkipVoteShare - ukipVoteShare;

        // throw away temp values
        conservativeVoteShare = newConservativeVoteShare;
        labourVoteShare = newLabourVoteShare;
        libdemVoteShare = newLibdemVoteShare;
        ukipVoteShare = newUkipVoteShare;

        // update seats
        conservativeSeats = Math.round(220 + conservativeVoteShare * 783.8f - labourVoteShare * 566 - libdemVoteShare * 158);
        labourSeats = Math.round(290 - conservativeVoteShare*584 + labourVoteShare*703 - libdemVoteShare*103);
        libdemSeats = Math.round(100 - conservativeVoteShare*198 - labourVoteShare*106 + libdemVoteShare*262);
        ukipSeats = Math.round(Math.max((ukipVoteShare-0.1f), 0)*60 + Math.max((ukipVoteShare-0.2f), 0)*300 + Math.max((ukipVoteShare-0.3f), 0)*500);
    }

}
