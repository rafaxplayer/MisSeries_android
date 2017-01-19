package rafaxplayer.misseries.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rafaxplayer.misseries.models.Capitulo;
import rafaxplayer.misseries.models.Serie;

import static rafaxplayer.misseries.MisSeries.capitulosRef;
import static rafaxplayer.misseries.MisSeries.seriesRef;

/**
 * Created by rafax on 16/01/2017.
 */

public class updateDataSerieAsync extends AsyncTask<Void, Void, Void> {

    private ProgressDialog mProgressDialog;
    private Context con;
    private Serie serie;

    public updateDataSerieAsync(Context con, Serie serie){
        this.con = con;
        this.serie=serie;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(con);
        mProgressDialog.setTitle("Buscando datos de la serie");
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            // Connect to the web site
            Document document= Jsoup.connect("http://seriesdanko.com/serie.php?serie="+serie.code).get();
            serie.name=document.select(".post-title").text().replace("Lista de capitulos de ","");
            serie.poster = document.select(".ict").last().attr("src");
            Elements links= document.select("a[href^=capitulo]");
            Pattern p = Pattern.compile("&temp=(.*?)&");
            for(Element link :links){
                Capitulo cap= new Capitulo();

                cap.name = link.text();
                cap.url = link.attr("href");
                cap.visto = false;
                cap.notify = false;
                cap.seriecode = serie.code;

                Matcher m = p.matcher(link.attr("href"));
                while(m.find())
                {
                    cap.temp = m.group(1);
                    Log.e("LINK_TEMP",m.group(1)); //is your string. do what you want
                }
               capitulosRef.child(link.text()).setValue(cap);

            }

            Matcher m = p.matcher(links.last().attr("href"));
            while(m.find())
            {
                serie.temps = m.group(1);
                Log.e("LINK_TEMPS",m.group(1)); //is your string. do what you want
            }
            seriesRef.child(serie.code).setValue(serie);
            //Log.e("HTML",document.select(".ict").last().attr("src"));


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // Set downloaded image into ImageView
        mProgressDialog.setMessage("Ok Serie añadida");
        mProgressDialog.dismiss();
    }
}
