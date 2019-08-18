package just.cse.mahfuz.shiptrackingsystem.Adapter;

        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.net.Uri;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import com.bumptech.glide.Glide;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.firestore.FirebaseFirestore;

        import java.util.List;

        import just.cse.mahfuz.shiptrackingsystem.Model.Users;
        import just.cse.mahfuz.shiptrackingsystem.R;

public class ShipListRecyclerAdapter extends RecyclerView.Adapter<ShipListRecyclerAdapter.myViewHolder> {
    Context context;
    List<Users> usersModel;
    String sImage, sShipName, sShipID, sCountry, sOwnerName, sOwnerEmail, sOwnerPhone;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String uid;
    ProgressDialog progressDialog;

    String type;

    public ShipListRecyclerAdapter(Context c, List<Users> usrs, String type) {
        context = c;
        usersModel = usrs;

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        progressDialog = new ProgressDialog(context);

    }

    @NonNull
    @Override
    public ShipListRecyclerAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.contact_row, viewGroup, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShipListRecyclerAdapter.myViewHolder myViewHolder, int i) {


        sImage = usersModel.get(i).getsImage();
        sShipID = usersModel.get(i).getsShipID();
        sShipName = usersModel.get(i).getsShipName();
        sCountry = usersModel.get(i).getsCountry();
        sOwnerName = usersModel.get(i).getsOwnerName();
        sOwnerEmail = usersModel.get(i).getsOwnerEmail();
        sOwnerPhone = usersModel.get(i).getsOwnerPhone();


        if (!"".equals(sImage) && sImage!=null) {
            Glide.with(context)
                    .load(sImage)
                    //.override(80, 80)
                    //.thumbnail(0.1f)
                    .into(myViewHolder.image);
        }

        myViewHolder.shipID.setText(sShipID);
        myViewHolder.shipName.setText(sShipName);
        myViewHolder.ownerName.setText(sOwnerName);
        myViewHolder.ownerEmail.setText(sOwnerEmail);
        myViewHolder.ownerPhone.setText(sOwnerPhone);


        myViewHolder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                String p = "tel:+88"+myViewHolder.ownerPhone.getText().toString().trim();
                i.setData(Uri.parse(p));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {

        return usersModel.size();
    }


    class myViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView shipID, shipName, ownerName, ownerEmail, ownerPhone;
        Button call;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            shipID = itemView.findViewById(R.id.shipID);
            shipName = itemView.findViewById(R.id.shipName);
            ownerName = itemView.findViewById(R.id.ownerName);
            ownerEmail = itemView.findViewById(R.id.ownerEmail);
            ownerPhone = itemView.findViewById(R.id.ownerPhone);

            call = itemView.findViewById(R.id.call);



        }
    }


}



