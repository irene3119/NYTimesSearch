package com.irene.nytimessearch.Adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.irene.nytimessearch.Models.Articles;
import com.irene.nytimessearch.Models.Doc;
import com.irene.nytimessearch.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Irene on 2017/2/21.
 */

public class ArticleAdapter extends BaseAdapter {

    private Context mContext;
    private Articles articles;

    public ArticleAdapter(Context mContext, Articles articles) {
        this.mContext = mContext;
        this.articles = articles;
    }

    public void addAll(ArrayList<Doc> docs) {
        if(docs==null){
            docs = new ArrayList<Doc>();
        }
        articles.response.docs.addAll(docs);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(articles != null && articles.response.docs != null)
            return articles.response.docs.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return articles.response.docs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //get data from position
        final Doc doc = (Doc) getItem(position);

        //check if contextView exist, reuse it
        //not using recycled view -> inflate the layout
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_article_result, parent, false);
            holder = new ViewHolder(convertView);
            holder.image = (ImageView) convertView.findViewById(R.id.ivImage);
            holder.textTitle = (TextView) convertView.findViewById(R.id.tvTitle);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textTitle.setText(doc.headline.main);

        //clear out recycled image from convertView from last time
        holder.image.setImageResource(0);

        //populate the thumbnail image
        //remoted download the image in the background

        if(doc.multimedia != null)
        {
            for(int i = 0; i < doc.multimedia.size(); i++)
            {
                if(doc.multimedia.get(i).subtype.equals("thumbnail"))
                {
                    Glide.with(mContext).load(doc.multimedia.get(i).getImageUrl())
                            //.transform(new RoundedCornersTransformation(2, 2))
                            .centerCrop()
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(holder.image);
                }
            }
        }


        return convertView;
    }

    static class ViewHolder {

        @Nullable @BindView(R.id.ivImage)
        ImageView image;
        @Nullable @BindView(R.id.tvTitle)
        TextView textTitle;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
