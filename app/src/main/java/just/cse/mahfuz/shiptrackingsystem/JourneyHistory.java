package just.cse.mahfuz.shiptrackingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class JourneyHistory extends AppCompatActivity {

    Context context= JourneyHistory.this;
    TextView journeyDate,destination,deadWeight,draught;
    String sJourneyDate,sDestination,sDeadWeight,sDraught;
    LinearLayout edit;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String uid;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Journey History");


        journeyDate= findViewById(R.id.journeyDate);
        destination= findViewById(R.id.destination);
        deadWeight= findViewById(R.id.deadWeight);
        draught= findViewById(R.id.draught);

        edit=findViewById(R.id.edit);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();
        uid = firebaseAuth.getUid();

        progressDialog= new ProgressDialog(JourneyHistory.this);

        loadContents();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context,EditJourneyHistioryActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    private void loadContents() {
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        firebaseFirestore.collection("users").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        try {
                            sJourneyDate=task.getResult().getString("sJourneyDate");
                            sDestination=task.getResult().getString("sDestination");
                            sDeadWeight= task.getResult().getString("sDeadWeight");
                            sDraught= task.getResult().getString("sDraught");


                            journeyDate.setText(sJourneyDate);
                            destination.setText(sDestination);
                            deadWeight.setText(sDeadWeight);
                            draught.setText(sDraught);

                            progressDialog.dismiss();
                        }
                        catch (Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context,"Failed to retrive data from database",Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
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
