    package org.asofat.shabadplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String URLS_FILE_PATH = "PL_myplaylists.txt";
    public static final String EXTRA_MP3_URL = "mp3_file_url_to_play";
    public static final String SongName = "Song Name : ";
    private boolean isInit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showPlaylist(null);

        isInit = false;
    }

    public void doScraping(View view) throws IOException {
        ArrayList<SongData> songs = new ArrayList<>();
        EditText urlTxt = findViewById(R.id.txtUrlToScrape);
        /** TODO: Actually scrape the URL. For now we will hard code some URLs as of they
         * were scraped from the page given.
         */
        String urlToGet = urlTxt.getText().toString();
        System.out.println("Fetching: " + urlToGet);
        new Thread() {
            @Override
            public void run() {
                super.run();
                Document doc = null;
                try {
                    doc = Jsoup.connect(urlToGet).get();

                    Elements names = doc.select("table#myTable > tbody > tr");
                    int id=0;
                    for (Element row : names) {
                        //TODO: Get the proper URL
                        String href = "https://rssb.org/"+row.select("td:nth-child(3) > a").attr("href");
                        Elements tds = row.select("td");
                        System.out.println("TDS size: " + tds.size());
                        System.out.println("TDS first: " + tds.get(0).text());

                        String name = row.select("td:nth-child(1)").text();
                        System.out.println("HREF: " + href + " Name: " + name);
                        SongData s = new SongData(""+id, href, name);
                        songs.add(s);
                        id++;
                    }

                    // Save these URLs as a local file, which can be later read by the player.
                    saveUrlsToLocalFile(songs);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showPlaylist(null);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void saveUrlsToLocalFile(ArrayList<SongData> songs) throws IOException {

        try (FileOutputStream fos = this.openFileOutput(URLS_FILE_PATH, Context.MODE_PRIVATE)) {
            for (SongData s : songs) {
                fos.write((s.toString()+"\n").getBytes());
            }
        }
        Log.i("AUDIO", "Written "+songs.size()+" songs to file.");

    }

    private ArrayList<SongData> readPlayList() throws FileNotFoundException {
        FileInputStream fis = this.openFileInput(URLS_FILE_PATH);
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        ArrayList<SongData> songs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                SongData d = SongData.parseFromString(line);
                if (d != null) {
                    songs.add(d);
                } else {
                    Log.e("AUDIO", "Value parsed is null for line: "+line);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
            e.printStackTrace();
        }
        return songs;
    }

    public void showPlaylist(View view) {
        TextView txt = findViewById(R.id.txtResult);
        try {
            ArrayList<SongData> songs = readPlayList();
            ItemFragment lvf = (ItemFragment) getSupportFragmentManager().findFragmentById(R.id.listViewFragment);
            lvf.getListData().addAll(songs);
            lvf.notifyDataChanged();
            Log.i("AUDIO", "Loaded songs list from file: "+songs.size());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            txt.setText("Error occurred when reading the platlist: " + e);
        }
    }
}
