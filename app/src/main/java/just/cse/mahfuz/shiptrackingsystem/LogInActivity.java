package just.cse.mahfuz.shiptrackingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.InetAddress;

public class LogInActivity extends AppCompatActivity {

    Context context=LogInActivity.this;

    EditText userName,password;
    String sUserName,sPassword;
    Button signIn,signUp;

    ProgressDialog progressDialog;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String uid;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(context, HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        userName=findViewById(R.id.username);
        password=findViewById(R.id.password);
        signIn=findViewById(R.id.signIn);
        signUp=findViewById(R.id.signUp);

        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();

        progressDialog = new ProgressDialog(context);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Logging In...");
                progressDialog.show();
                progressDialog.setCancelable(true);

                sUserName = userName.getText().toString().trim();
                sPassword = password.getText().toString().trim();


                if ((!TextUtils.isEmpty(sUserName)) && (!TextUtils.isEmpty(sPassword))) {

                    String sEmail= sUserName+"@gmail.com";
                    firebaseAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(context, HomeActivity.class);
                                finish();
                                startActivity(intent);

                            } else if (!isNetworkAvailable()) {
                                Toast.makeText(context, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(context, "LogIn failed. Please input correct Ship ID & password", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    })
                    ;
                } else {
                    Toast.makeText(context, "Please input both Ship ID & password", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context,SignUpActivity.class);
                startActivity(intent);
            }
        });


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
