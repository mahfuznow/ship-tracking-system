package just.cse.mahfuz.shiptrackingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fenchtose.nocropper.CropInfo;
import com.fenchtose.nocropper.CropResult;
import com.fenchtose.nocropper.Cropper;
import com.fenchtose.nocropper.CropperView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    Context context= EditProfileActivity.this;

    CircleImageView image;
    Button chooseImage;
    EditText shipName, ownerName, ownerEmail, ownerPhone;
    TextView country,shipID;

    String sImage,sShipName,sShipID,sPassword,sCountry, sOwnerName, sOwnerEmail, sOwnerPhone;

    LinearLayout done;

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
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        image=findViewById(R.id.image);
        chooseImage=findViewById(R.id.chooseImage);

        shipName=findViewById(R.id.shipName);
        shipID =findViewById(R.id.shipID);

        country=findViewById(R.id.country);
        ownerName =findViewById(R.id.ownerName);
        ownerEmail =findViewById(R.id.ownerEmail);
        ownerPhone =findViewById(R.id.ownerPhone);

        done =findViewById(R.id.signUp);



        FirebaseApp.initializeApp(context);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();

        progressDialog = new ProgressDialog(context);


        loadContents();

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select a Picture"),111);
            }
        });
        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogList("Choose Your Country",getResources().getStringArray(R.array.countries_array),country);
            }
        });

        shipID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Ship Id cannot be changed, You need to create a new account for a new ship ID",Toast.LENGTH_LONG).show();
            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Updating profile...");
                progressDialog.show();
                progressDialog.setCancelable(false);

                sShipName=shipName.getText().toString().trim();
                sShipID= shipID.getText().toString().trim();

                sCountry=country.getText().toString().trim();
                sOwnerName = ownerName.getText().toString().trim();
                sOwnerEmail = ownerEmail.getText().toString().trim();
                sOwnerPhone = ownerPhone.getText().toString().trim();




                if (TextUtils.isEmpty(sShipName) ||
                        TextUtils.isEmpty(sShipID) ||
                        TextUtils.isEmpty(sPassword) ||
                        TextUtils.isEmpty(sCountry) ||
                        TextUtils.isEmpty(sOwnerName) ||
                        TextUtils.isEmpty(sOwnerEmail) ||
                        TextUtils.isEmpty(sOwnerPhone) )
                {
                    progressDialog.dismiss();
                    Toast.makeText(context,"Please Insert all the required fields",Toast.LENGTH_SHORT).show();
                }
                else {

                    if (isNetworkAvailable()) {
                        //Writing into firestore
                        Map<String, Object> setvalue = new HashMap<>();

                        setvalue.put("sShipName", sShipName);
                        setvalue.put("sShipID", sShipID);
                        setvalue.put("sPassword", sPassword);
                        setvalue.put("sCountry", sCountry);
                        setvalue.put("sOwnerName", sOwnerName);
                        setvalue.put("sOwnerEmail", sOwnerEmail);
                        setvalue.put("sOwnerPhone", sOwnerPhone);

                        firebaseFirestore.collection("users").document(uid).update(setvalue);




                        //that means user  choose a new photo
                        if (filePath!=null) {
                            uploadFile();
                        }
                        else {
                            finishingUpdating();
                        }

                    }
                    else {
                        Toast.makeText(context,"No internet connection, please try again",Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });



    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                cropImage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public  void cropImage() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

        View view1 = LayoutInflater.from(context).inflate(R.layout.image_crop_layout, null);
        builder.setView(view1);
        builder.setCancelable(true);
        final android.app.AlertDialog alertDialog = builder.create();

        Button select=view1.findViewById(R.id.select);
        final CropperView cropperView = view1.findViewById(R.id.cropper_view);
        cropperView.setImageBitmap(bitmap);
        cropperView.cropToCenter();
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap=cropperView.getCroppedBitmap().getBitmap();
                image.setImageBitmap(bitmap);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
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


    private void uploadFile() {
        //checking if file is available
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            progressDialog.setTitle("Updating Profile..");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);


            //getting the storage reference
            final StorageReference sRef = storageReference.child("Image/" + System.currentTimeMillis() + "." + getFileExtension(filePath));

            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                             sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    sImage=uri.toString();
                                    Map<String,Object> setImgUrl= new HashMap<>();
                                    setImgUrl.put("sImage",sImage);
                                    firebaseFirestore.collection("users").document(uid).update(setImgUrl);

                                    Toast.makeText(context, "Image updated succesfully ", Toast.LENGTH_SHORT).show();

                                    finishingUpdating();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Updating Image.." + ((int) progress) + "%...");
                        }
                    });
        } else {
            //display an error if no file is selected
        }
    }

    public void finishingUpdating() {
        progressDialog.setMessage("Please wait..");
        Toast.makeText(context, "Profile Succesfully edited", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();

        Intent intent = new Intent(context, ProfileActivity.class);
        finish();
        startActivity(intent);
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    public  void AlertDialogList(String title, final String[] string_array, final TextView textView) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(string_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(string_array[which]);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
