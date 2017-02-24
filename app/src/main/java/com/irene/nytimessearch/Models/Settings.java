package com.irene.nytimessearch.Models;

import java.io.Serializable;

/**
 * Created by Irene on 2017/2/24.
 */

public class Settings implements Serializable{
    public String BeginDate;
    public String Sort;
    public boolean Check_Arts;
    public boolean Check_Fashion;
    public boolean Check_Sports;

    public String getBeginDate() {
        return BeginDate;
    }

    public void setBeginDate(String beginDate) {
        BeginDate = beginDate;
    }

    public String getSort() {
        return Sort;
    }

    public void setSort(String sort) {
        Sort = sort;
    }

    public boolean isCheck_Arts() {
        return Check_Arts;
    }

    public void setCheck_Arts(boolean check_Arts) {
        Check_Arts = check_Arts;
    }

    public boolean isCheck_Fashion() {
        return Check_Fashion;
    }

    public void setCheck_Fashion(boolean check_Fashion) {
        Check_Fashion = check_Fashion;
    }

    public boolean isCheck_Sports() {
        return Check_Sports;
    }

    public void setCheck_Sports(boolean check_Sports) {
        Check_Sports = check_Sports;
    }

    public String genNewsDesk() {
        String newsType = isCheck_Arts() ? "\"Arts\"" : "";
        newsType += isCheck_Fashion() ? "\"Fashion\"" : "";
        newsType += isCheck_Sports() ? "\"Sports\"" : "";
        return newsType;
    }
}
