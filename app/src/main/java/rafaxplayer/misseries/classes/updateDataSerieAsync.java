package rafaxplayer.misseries.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rafaxplayer.misseries.models.Serie;

import static rafaxplayer.misseries.MisSeries.seriesRef;

/**
 * Created by rafax on 16/01/2017.
 */

public class updateDataSerieAsync extends AsyncTask<Void, Void, Integer> {

    private ProgressDialog mProgressDialog;
    private Context con;
    private String code;

    public updateDataSerieAsync(Context con, String code){
        this.con = con;
        this.code = code;
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
    protected Integer doInBackground(Void... params) {

        try {
            // Connect to the web site
            Document document = Jsoup.connect("http://seriesdanko.com/serie.php?serie=" + code).get();
            String name = document.select(".post-title").text().replace("Lista de capitulos de ", "");
            String poster = document.select(".ict").last().attr("src");
            Log.e("TITLE SERIE", name);

            Elements links = document.select("a[href^=capitulo]");
            Pattern p = Pattern.compile("&temp=(.*?)&");
            String temps = "0";

            Matcher m = p.matcher(links.last().attr("href"));
            while (m.find()) {

                temps = m.group(1);
                //Log.e("LINK_TEMP",m.group(1)); //is your string. do what you want
            }
            Serie serie = new Serie();
            serie.code = code;
            serie.name = name;
            serie.poster = poster;
            serie.temps = temps;
            seriesRef.child(serie.code).setValue(serie);


        }catch(IOException ex){

            ex.printStackTrace();
            return 1;

        } catch (Exception e) {

            e.printStackTrace();
           return 1;
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {

        if(result==0) {
            mProgressDialog.setMessage("Ok Serie añadida");
        }else{
            Toast.makeText(con, "La serie no existe o ocurrio un error", Toast.LENGTH_SHORT).show();
        }
        mProgressDialog.dismiss();
    }
}
