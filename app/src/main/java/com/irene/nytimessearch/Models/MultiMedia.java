package com.irene.nytimessearch.Models;

/**
 * Created by Irene on 2017/2/20.
 */

public class MultiMedia {
    public String url;
    public int height;
    public int width;
    public String type;
    public String subtype;

    public String getImageUrl() {
        return "http://www.nytimes.com/" + url;
    }


}
