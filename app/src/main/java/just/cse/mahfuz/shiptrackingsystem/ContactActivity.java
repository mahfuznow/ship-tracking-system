package just.cse.mahfuz.shiptrackingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import just.cse.mahfuz.shiptrackingsystem.Adapter.ShipListRecyclerAdapter;
import just.cse.mahfuz.shiptrackingsystem.Model.Users;

public class ContactActivity extends AppCompatActivity {

    Context context = ContactActivity.this;
    TextView title;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    ShipListRecyclerAdapter shipListRecyclerAdapter;

    ProgressDialog progressDialog;

    String type=" ";

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_list);

        getSupportActionBar().setTitle("Contacts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title=findViewById(R.id.title);
        recyclerView = findViewById(R.id.recyclerView);

        firebaseFirestore = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        progressDialog = new ProgressDialog(context);

        progressDialog.setMessage("Loading..");
        progressDialog.show();



        Query query = firebaseFirestore.collection("users").orderBy("sShipID", Query.Direction.ASCENDING);


        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        try {
                            List<Users> userModel = task.getResult().toObjects(Users.class);
                            shipListRecyclerAdapter = new ShipListRecyclerAdapter(context, userModel,type);
                            recyclerView.setAdapter(shipListRecyclerAdapter);
                            progressDialog.dismiss();
                        }
                        catch (Exception e) {
                            Toast.makeText(context,"An Error Occurred",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"An Error Occurred",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });


//        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//
//                if (e != null) {
//                    return;
//                }
//                List<Users> userModel = queryDocumentSnapshots.toObjects(Users.class);
//
//                shipListRecyclerAdapter = new ShipListRecyclerAdapter(context, userModel, "contacts");
//                recyclerView.setAdapter(shipListRecyclerAdapter);
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
