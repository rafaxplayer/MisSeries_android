package rafaxplayer.misseries.activities;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import rafaxplayer.misseries.R;

public class Settings_Activity extends AppCompatActivity {
    String nameSerie;
    String codeSerie;
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Bundle bund = getIntent().getExtras();
        MyPreferenceFragment frag=new MyPreferenceFragment();
        if(bund != null){
            nameSerie = bund.getString("name");
            codeSerie = bund.getString("code");
            frag.setArguments(bund);
        }

        getFragmentManager().beginTransaction().replace(R.id.content,frag).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {


        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            if(getArguments()!= null){
                PreferenceCategory prefCat=(PreferenceCategory)findPreference("Seriename");
                prefCat.setTitle(getArguments().getString("name")+" - "+getArguments().getString("code"));
                CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getActivity());
                checkBoxPreference.setDefaultValue(true);
                checkBoxPreference.setKey(getArguments().getString("code"));
                checkBoxPreference.setTitle("Notificaciones");
                checkBoxPreference.setSummary("Activa o desactiva las notificaciones para esta serie");
                prefCat.addPreference(checkBoxPreference);
            }

        }

    }
}
