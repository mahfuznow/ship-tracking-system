package just.cse.mahfuz.shiptrackingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    Context context=ProfileActivity.this;

    ImageView back;
    CircleImageView image;

    TextView shipName, shipID,country, ownerName, ownerEmail, ownerPhone;
    String sImage,sShipName,sShipID,sPassword,sCountry, sOwnerName, sOwnerEmail, sOwnerPhone;

    LinearLayout edit;

    ProgressDialog progressDialog;

    Bitmap bitmap;
    private Uri filePath;
    private StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        back=findViewById(R.id.back);
        image=findViewById(R.id.image);

        shipName=findViewById(R.id.shipName);
        shipID =findViewById(R.id.shipID);
        country=findViewById(R.id.country);
        ownerName =findViewById(R.id.ownerName);
        ownerEmail =findViewById(R.id.ownerEmail);
        ownerPhone =findViewById(R.id.ownerPhone);

        edit =findViewById(R.id.edit);



        FirebaseApp.initializeApp(context);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();

        progressDialog = new ProgressDialog(context);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        loadContents();


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(context, EditProfileActivity.class));

            }
        });



    }

    public void loadContents() {
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(true);

        firebaseFirestore.collection("users").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        try {
                            sImage=task.getResult().getString("sImage");
                            sShipName=task.getResult().getString("sShipName");
                            sShipID= task.getResult().getString("sShipID");
                            sPassword= task.getResult().getString("sPassword");
                            sCountry=task.getResult().getString("sCountry");
                            sOwnerName = task.getResult().getString("sOwnerName");
                            sOwnerEmail = task.getResult().getString("sOwnerEmail");
                            sOwnerPhone = task.getResult().getString("sOwnerPhone");




                            //setting contents to the navigation drawer

                            if (!"".equals(image) && sImage!=null) {
                                Glide.with(context)
                                        .load(sImage)
                                        //.override(80, 80)
                                        //.thumbnail(0.1f)
                                        .into(image);
                            }
                            shipName.setText(sShipName);
                            shipID.setText(sShipID);
                            country.setText(sCountry);
                            ownerName.setText(sOwnerName);
                            ownerEmail.setText(sOwnerEmail);
                            ownerPhone.setText(sOwnerPhone);

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
                        Toast.makeText(context,"An Error occurred",Toast.LENGTH_SHORT).show();

                    }
                });

    }

}
