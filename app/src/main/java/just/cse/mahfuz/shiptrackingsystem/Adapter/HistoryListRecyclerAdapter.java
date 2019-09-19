package just.cse.mahfuz.shiptrackingsystem.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import just.cse.mahfuz.shiptrackingsystem.Model.History;
import just.cse.mahfuz.shiptrackingsystem.Model.Users;
import just.cse.mahfuz.shiptrackingsystem.R;

public class HistoryListRecyclerAdapter extends RecyclerView.Adapter<HistoryListRecyclerAdapter.myViewHolder> implements Filterable {
    Context context;
    List<History> historyModel;
    List<History> filteredHistoryModel;

    String sStartDate, sEndDate, sDestination, sDeadWeight, sDraught, timestamp;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String uid;
    ProgressDialog progressDialog;

    String type;


    public HistoryListRecyclerAdapter(Context c, List<History> historyModel, String type) {
        context = c;
        this.historyModel = historyModel;
        filteredHistoryModel = historyModel;
        this.type = type;

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        progressDialog = new ProgressDialog(context);

    }

    @NonNull
    @Override
    public HistoryListRecyclerAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.history_row, viewGroup, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HistoryListRecyclerAdapter.myViewHolder myViewHolder, int i) {


        sStartDate = filteredHistoryModel.get(i).getsStartDate();
        sEndDate = filteredHistoryModel.get(i).getsEndDate();
        sDestination = filteredHistoryModel.get(i).getsDestination();
        sDeadWeight = filteredHistoryModel.get(i).getsDeadWeight();
        sDraught = filteredHistoryModel.get(i).getsDraught();

        myViewHolder.startDate.setText("Start: "+sStartDate);
        myViewHolder.endDate.setText("End: "+sEndDate);
        myViewHolder.destination.setText("Destination: "+sDestination);
        myViewHolder.deadWeight.setText("Dead Weight: "+sDeadWeight);
        myViewHolder.draught.setText("Draught: "+sDraught);


//        if ("contacts".equals(type)) {
//
//        }
//
//        else if ("track".equals(type)) {
//
//            myViewHolder.call.setVisibility(View.INVISIBLE);
//
//            myViewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    progressDialog.setMessage("Loading...");
//                    progressDialog.show();
//                    progressDialog.setCancelable(true);
//
//                    Intent intent = new Intent(context, MapsActivity.class);
//
//                    //converting bitmap to bytearray
//                    Bitmap bitmap = ((BitmapDrawable) myViewHolder.image.getDrawable()).getBitmap();
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                    byte[] b = baos.toByteArray();
//
//
//                    intent.putExtra("image", b);
//
//                    intent.putExtra("startDate", myViewHolder.startDate.getText().toString());
//                    intent.putExtra("endDate", myViewHolder.endDate.getText().toString());
//                    //intent.putExtra("country",myViewHolder.startDate.getText().toString());
//                    intent.putExtra("destination", myViewHolder.destination.getText().toString());
//                    intent.putExtra("deadWeight", myViewHolder.deadWeight.getText().toString());
//                    intent.putExtra("draught", myViewHolder.draught.getText().toString());
//
//                    context.startActivity(intent);
//
//
//                }
//            });
//
//        }
//

    }

    @Override
    public int getItemCount() {

        return filteredHistoryModel.size();
    }


    class myViewHolder extends RecyclerView.ViewHolder {
        TextView startDate, endDate, destination, deadWeight, draught;



        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            startDate = itemView.findViewById(R.id.startDate);
            endDate = itemView.findViewById(R.id.endDate);
            destination = itemView.findViewById(R.id.destination);
            deadWeight = itemView.findViewById(R.id.deadWeight);
            draught = itemView.findViewById(R.id.draught);

        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredHistoryModel = historyModel;
                } else {
                    List<History> filteredList = new ArrayList<>();
                    for (History row : historyModel) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getsStartDate().contains(charSequence)
                                || row.getsEndDate().contains(charSequence)
                                || row.getsDestination().toLowerCase().contains(charString.toLowerCase())
                                || row.getsDeadWeight().contains(charSequence)
                                || row.getsDraught().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    filteredHistoryModel = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredHistoryModel;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredHistoryModel = (ArrayList<History>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

//    public interface ShipListRecyclerAdapterListener {
//        void onShipSelected(Users contact);
//    }


}



