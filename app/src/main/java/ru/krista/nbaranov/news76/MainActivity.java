package ru.krista.nbaranov.news76;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import ru.krista.nbaranov.news76.helpers.DatabaseHandler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ListView listView;
    static final String BLOG_URL = "http://76.ru/text/rss.xml";
    static final String TAG_titular = "rss channel item";
    public ArrayList<News> newsArary = new ArrayList<>();
    SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

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
                Intent intent = new Intent(MainActivity.this, NewsDisplay.class);
                intent.putExtra("link", newsArary.get(position).getLink());
                startActivity(intent);
            }
        });
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(onRefreshListener);
        swipeLayout.setColorSchemeColors(
                Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        fillListViewFromDB();
    }

    private void fillListViewFromDB() {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        //db.onUpgrade(db.getWritableDatabase(),1,2);
        Cursor cursor = db.getNews();
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            do {
                News news = new News();
                news.setId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ID)));
                news.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TITLE_NEWS)));
                news.setDate(cursor.getString(cursor.getColumnIndex(DatabaseHandler.DATE_NEWS)));
                news.setLink(cursor.getString(cursor.getColumnIndex(DatabaseHandler.LINK_NEWS)));
                newsArary.add(news);
            } while (cursor.moveToNext());
            NewsAdapter arrayAdapter = new NewsAdapter(getApplication().getApplicationContext(), newsArary);
            listView.setAdapter(arrayAdapter);
        }
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            GetNewsAsync mt = new GetNewsAsync();
            mt.execute();
            swipeLayout.setRefreshing(false);
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class GetNewsAsync extends AsyncTask<Void, Void, Void> {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());

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
        }
    }
}
