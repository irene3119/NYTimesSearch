package com.irene.nytimessearch.Models;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Irene on 2017/2/20.
 */

public class Doc {

    public String web_url;
    public String snippet;
    public String lead_paragraph;
    public Timestamp pub_date;
    public Headline headline = new Headline();
    public ArrayList<MultiMedia> multimedia = new ArrayList<>();


    public class Headline {
        public String main;
    }
}
