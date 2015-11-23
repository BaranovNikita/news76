package ru.krista.nbaranov.news76;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import ru.krista.nbaranov.news76.helpers.DatabaseHandler;
import ru.krista.nbaranov.news76.helpers.Utils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ListView listView;
    static final String BLOG_URL = "http://76.ru/text/rss.xml";
    static final String TAG_titular = "rss channel item";
    public ArrayList<News> newsArary = new ArrayList<>();
    SwipeRefreshLayout swipeLayout;
    private static MainActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (Utils.isOnline()) {
                    Intent intent = new Intent(MainActivity.this, NewsDisplay.class);
                    intent.putExtra("link", newsArary.get(position).getLink());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setNestedScrollingEnabled(true);
        }
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(onRefreshListener);
        swipeLayout.setColorSchemeColors(
                Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        fillListViewFromDB();
    }

    public static MainActivity getInstance() {
        return instance;
    }



    private void fillListViewFromDB() {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        //db.onUpgrade(db.getWritableDatabase(),1,2);
        Cursor cursor = db.getNews();
        newsArary.clear();
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            do {
                News news = new News();
                news.setId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.GUID)));
                news.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TITLE_NEWS)));
                news.setDate(cursor.getString(cursor.getColumnIndex(DatabaseHandler.DATE_NEWS)));
                news.setLink(cursor.getString(cursor.getColumnIndex(DatabaseHandler.LINK_NEWS)));
                newsArary.add(news);
            } while (cursor.moveToNext());
            NewsAdapter arrayAdapter = new NewsAdapter(getApplication().getApplicationContext(), newsArary);
            listView.setAdapter(arrayAdapter);
        }
    }

    public void displayAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.wanna_update)).setCancelable(
                false).setPositiveButton(getResources().getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        refreshLayout();
                        dialog.cancel();
                    }
                }).setNegativeButton(getResources().getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshLayout();
        }
    };

    private void refreshLayout() {
        if (Utils.isOnline()) {
            GetNewsAsync mt = new GetNewsAsync();
            mt.execute();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_connection),
                    Toast.LENGTH_LONG).show();
        }
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            showAbout();
        } else if (id == R.id.nav_exit) {
            finish();
        } else if (id == R.id.nav_update) {
            refreshLayout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void showAbout() {
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        TextView textView = (TextView) messageView.findViewById(R.id.about_credits);
        int defaultColor = textView.getTextColors().getDefaultColor();
        textView.setTextColor(defaultColor);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo76);
        builder.setTitle(getResources().getString(R.string.about));
        builder.setView(messageView);
        builder.create();
        builder.show();
    }

    class GetNewsAsync extends AsyncTask<Void, Void, Void> {
        DatabaseHandler db = new DatabaseHandler(MainActivity.this);
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.downloading),
                    getResources().getString(R.string.wait), true);
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Document doc = Jsoup.connect(BLOG_URL).ignoreContentType(true).get();
                Elements items = doc.select(TAG_titular);
                for (Element item : items) {
                    News news = new News();
                    int start = item.toString().indexOf("<link>") + 6;
                    int stop = item.toString().indexOf("<description>") - 3;
                    news.setId(item.select("guid").text());
                    news.setTitle(item.select("title").text());
                    news.setDate(item.select("pubDate").text());
                    news.setLink(item.toString().substring(start, stop));
                    db.addNews(news);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            fillListViewFromDB();
            progress.dismiss();
        }
    }
}
