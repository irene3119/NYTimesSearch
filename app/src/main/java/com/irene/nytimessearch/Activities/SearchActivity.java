package com.irene.nytimessearch.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.irene.nytimessearch.Adapter.ArticleAdapter;
import com.irene.nytimessearch.Models.Articles;
import com.irene.nytimessearch.Models.Doc;
import com.irene.nytimessearch.Models.Settings;
import com.irene.nytimessearch.R;
import com.irene.nytimessearch.Utils.EndlessScrollListener;
import com.irene.nytimessearch.Utils.SettingDiaglogFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements SettingDiaglogFragment.SettingItemDialogListener{

    @BindView(R.id.gvResults) GridView gvResults;

    SettingDiaglogFragment settingDialogFragment;

    private Articles articles;
    private ArticleAdapter articleAdapter;
    private Settings settings = null;
    private String queryItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("News Article Search");
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        setupGridViews();

    }


    public void setupGridViews() {
        //init grid view
        articles = new Articles();
        gvResults = (GridView) findViewById(R.id.gvResults);
        articleAdapter = new ArticleAdapter(getBaseContext(), articles);
        gvResults.setAdapter(articleAdapter);

        //hook up listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //create an intent to display the aricle
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                //get the article to display
                Doc doc = articles.response.docs.get(position);
                //pass in that article into intent
                i.putExtra("url",doc.web_url);
                //launch the activity
                startActivity(i);
            }
        });

        //infinite scrolling
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {

        Log.e("page",String.valueOf(offset)+" page");
        //String query = etQuery.getText().toString();
        genArticleRequest("http://api.nytimes.com/svc/search/v2/articlesearch.json",offset,queryItem);
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyDataSetChanged()`
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                Log.d(this.getClass().getName(),"query:"+ query);
                Toast.makeText(SearchActivity.this,"Searching for " + query, Toast.LENGTH_LONG).show();
                queryItem = query;
                articles.response.docs.clear();
                genArticleRequest("http://api.nytimes.com/svc/search/v2/articlesearch.json",0,queryItem);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(this.getClass().getName(),"onQueryTextChange:"+ newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setting) {

            showEditDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void genArticleRequest(String url, int page, String query) {
        RequestParams params = new RequestParams();
        params.put("api-key", "f150cff14d484d8ebaeafb227ff2ed78");
        params.put("page",page);
        params.put("q",query);
        if(settings != null){
            if(!settings.getBeginDate().equals("")){
                Log.e("DEBUG","begin date = "+settings.getBeginDate());
                params.put("begin_date", settings.getBeginDate());
            }
            params.put("sort", settings.getSort());
            Log.e("DEBUG", settings.getSort());
            if (settings.genNewsDesk().equals("")){
                params.put("fq", "news_desk:(" +settings.genNewsDesk()+ ")");
            }
        }
        getArticlesByRequest(url,params);
    }



    private void getArticlesByRequest(String url, RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(50000);
        client.get(url,params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Articles newArticle;
                // when api success
                Log.e("DEBUG",responseString);
                Gson gson = new Gson();
                newArticle = gson.fromJson(responseString, Articles.class);
                Log.e("DEBUG","doc size = "+String.valueOf(newArticle.response.docs.size()));

                articleAdapter.addAll(newArticle.response.docs);
                //Log.e("DEBUG", articles.response.docs.get(0).pub_date.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,  Throwable e) {
                // when api failed
                Toast.makeText(SearchActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        settingDialogFragment = SettingDiaglogFragment.newInstance(settings);
        settingDialogFragment.show(fm, "setting_fragment");

    }

    @Override
    public void onFinishSettingDialog(Settings savedSetting){
        settings = savedSetting;
    }
}
