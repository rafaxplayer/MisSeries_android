package rafaxplayer.misseries.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import rafaxplayer.misseries.models.Capitulo;

/**
 * Created by rafax on 10/01/2017.
 */

public class ListCapitulosAdapter extends RecyclerView.Adapter<ListCapitulosAdapter.ViewHolder>{

    private Context con;
    private List<Capitulo> listCaps;
    private DatabaseReference capitulosRef;

    public ListCapitulosAdapter(Context con, List<Capitulo> listCaps,DatabaseReference capitulosRef) {
        this.con = con;
        this.listCaps = listCaps;
        this.capitulosRef=capitulosRef;
    }

    @Override
    public ListCapitulosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_capitulo, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(listCaps.get(position).name);
        holder.switchVisto.setChecked(listCaps.get(position).visto);

    }

    public void checkAll(final Boolean b){

        capitulosRef.orderByChild("seriecode").equalTo(listCaps.get(0).seriecode).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> capsUpdates = new HashMap<>();
                for(DataSnapshot data:dataSnapshot.getChildren()){
                   Capitulo cap = data.getValue(Capitulo.class);
                    HashMap<String,Object> capMap = cap.toMap();
                    capMap.put("visto",b);
                    capsUpdates.put("/"+data.getKey(),capMap);

                }
                capitulosRef.updateChildren(capsUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //notifyDataSetChanged();
    }
    public boolean isAllChecked(){
        Boolean ret = true;
        for(Capitulo cap :listCaps){
            if(!cap.visto){
                ret = false;
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
            ButterKnife.bind(this,v);
            v.setOnClickListener(this);
            switchVisto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    HashMap<String, Object> result = new HashMap<>();
                    result.put("visto",b);
                    result.put("notify",true);
                    capitulosRef.child(listCaps.get(ViewHolder.this.getLayoutPosition()).name).updateChildren(result);
                }
            });
        }

        @Override
        public void onClick(View view) {


        }
    }
}
