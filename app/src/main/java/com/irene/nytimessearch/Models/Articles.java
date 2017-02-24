package com.irene.nytimessearch.Models;

import java.util.ArrayList;

/**
 * Created by Irene on 2017/2/21.
 */

public class Articles {

    public String status;
    public Response response = new Response();

    public class Response {
        public ArrayList<Doc> docs = new ArrayList<>();
    }

}
