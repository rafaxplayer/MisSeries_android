package rafaxplayer.misseries.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.classes.GlobalUttilities;
import rafaxplayer.misseries.classes.IconizedMenu;
import rafaxplayer.misseries.models.Capitulo;
import rafaxplayer.misseries.models.ItemTemp;

/**
 * Created by rafax on 10/01/2017.
 */

public class ListCapitulosAdapter extends RecyclerView.Adapter{

    private Context con;
    private List<Object> listCaps;
    private DatabaseReference capitulosRef;

    public ListCapitulosAdapter(Context con, List<Object> listCaps, DatabaseReference capitulosRef) {
        this.con = con;
        this.listCaps = listCaps;
        this.capitulosRef = capitulosRef;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch(viewType){
            case 0:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_capitulo, parent, false);
                return new ViewHolder(v);

            case 1:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_temp, parent, false);
                return new ViewHolderTemp(v);

            default:

        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(listCaps.get(position) instanceof Capitulo){
            ((ViewHolder)holder).name.setText(((Capitulo) listCaps.get(position)).name);
            ((ViewHolder)holder).switchVisto.setChecked(((Capitulo) listCaps.get(position)).visto);
        }else{
            ((ViewHolderTemp)holder).temp.setText(con.getString(R.string.tempslong_item)+((ItemTemp)listCaps.get(position)).temp);
        }

    }

    @Override
    public int getItemViewType(int position) {

        if(listCaps.get(position) instanceof Capitulo){
            return 0;
        }
        return 1;
    }

    public void checkAll(final Boolean b) {

        int position = listCaps.get(0) instanceof Capitulo ? 0 : 1;

        capitulosRef.orderByChild("seriecode").equalTo(((Capitulo) listCaps.get(position)).seriecode).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> capsUpdates = new HashMap<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Capitulo cap = data.getValue(Capitulo.class);
                    HashMap<String, Object> capMap = cap.toMap();
                    capMap.put("visto", b);
                    capsUpdates.put("/" + data.getKey(), capMap);

                }
                capitulosRef.updateChildren(capsUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //notifyDataSetChanged();
    }

    public boolean isAllChecked() {
        Boolean ret = true;
        for (Object cap : listCaps) {
            if (cap instanceof Capitulo) {
                if (!((Capitulo) cap).visto) {
                    ret = false;
                }
            }
        }
        return ret;
    }

    @Override
    public int getItemCount() {

        return listCaps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.textCapName)
        TextView name;
        @BindView(R.id.switchVisto)
        Switch switchVisto;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
            switchVisto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    HashMap<String, Object> result = new HashMap<>();
                    result.put("visto", b);
                    result.put("notify", true);
                    capitulosRef.child(((Capitulo) listCaps.get(ViewHolder.this.getLayoutPosition())).name).updateChildren(result);
                }
            });
        }

        @Override
        public void onClick(View view) {
            IconizedMenu PopupMenu = new IconizedMenu(con,switchVisto);
            Menu menu = PopupMenu.getMenu();
            MenuInflater inflater = PopupMenu.getMenuInflater();
            inflater.inflate(R.menu.options_capitulo_menu, PopupMenu.getMenu());
            PopupMenu.show();

            PopupMenu.setOnMenuItemClickListener(new IconizedMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.action_viewserie:
                            try {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalUttilities.BASE_URL+((Capitulo) listCaps.get(ViewHolder.this.getLayoutPosition())).url));
                                con.startActivity(browserIntent);
                            }catch (Exception e){
                                Log.e("View Url Intent",e.getMessage());
                            }
                            break;

                        case R.id.action_share:
                            try{
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, "Mis Series");
                                String sAux = GlobalUttilities.BASE_URL + ((Capitulo) listCaps.get(ViewHolder.this.getLayoutPosition())).url;
                                i.putExtra(Intent.EXTRA_TEXT,sAux);
                                con.startActivity(Intent.createChooser(i,"Compartir con:"));

                            }catch (Exception e){
                                Log.e("Share Intent",e.getMessage());
                            }

                            break;
                    }
                    return true;
                }
            });

        }
    }

    public class ViewHolderTemp extends RecyclerView.ViewHolder {
        @BindView(R.id.textTemp)
        TextView temp;

        public ViewHolderTemp(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
