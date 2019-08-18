package just.cse.mahfuz.shiptrackingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.annotation.Nullable;

import just.cse.mahfuz.shiptrackingsystem.Adapter.ShipListRecyclerAdapter;
import just.cse.mahfuz.shiptrackingsystem.Model.Users;

public class ContactsActivity extends AppCompatActivity {

    Context context = ContactsActivity.this;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    ShipListRecyclerAdapter shipListRecyclerAdapter;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

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
                            shipListRecyclerAdapter = new ShipListRecyclerAdapter(context, userModel, "contacts");
                            recyclerView.setAdapter(shipListRecyclerAdapter);
                            progressDialog.dismiss();
                        }
                        catch (Exception e) {
                            Toast.makeText(context,"An Error Occured",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"An Error Occured",Toast.LENGTH_SHORT).show();
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
}
