package ru.krista.nbaranov.news76;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;

import ru.krista.nbaranov.news76.helpers.DatabaseHandler;

public class NewsDisplay extends Activity {
    String link, DateAdded;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView imageToolbar;
    TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collapse);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        imageToolbar = (ImageView) findViewById(R.id.image_toolbar);
        description = (TextView) findViewById(R.id.description);
        Intent intent = getIntent();
        link = intent.getStringExtra("link");
        GetNewsAsync task = new GetNewsAsync();
        task.execute();

    }

    class GetNewsAsync extends AsyncTask<Void, Void, Void> {
        String title = "title";
        String img_src = "";
        String description_text = "";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(NewsDisplay.this, getResources().getString(R.string.downloading),
                    getResources().getString(R.string.wait), true);
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Document doc = Jsoup.connect(link).ignoreContentType(true).get();
                Elements titles = doc.select("span.title2");
                title = titles.text();
                Elements image = doc.select("img.news-record-thumbnail");
                img_src = image.attr("src");
                Elements p = doc.select("div.news-block-justify").select("p");
                for (Element item : p) {
                    description_text += item.text() + "\n\n";
                }
                Elements date = doc.select("span.title");
                DateAdded = date.text();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            collapsingToolbarLayout.setTitle(title);
            new DownloadImageTask(imageToolbar)
                    .execute(img_src);
            description.setText(String.format("%s\n\n%s%s\n\n", title, description_text, DateAdded));
            progress.dismiss();
        }
    }
}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
