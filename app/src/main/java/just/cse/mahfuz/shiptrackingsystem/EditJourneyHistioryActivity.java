package just.cse.mahfuz.shiptrackingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditJourneyHistioryActivity extends AppCompatActivity {

    Context context = EditJourneyHistioryActivity.this;
    TextView journeyDate;
    EditText destination, deadWeight, draught;
    String sJourneyDate, sDestination, sDeadWeight, sDraught;
    LinearLayout done;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String uid;

    ProgressDialog progressDialog;


    Calendar calendar;
    DatePickerDialog datePickerDialog;
    int year, month, dayOfMonth;
    int dayint;
    String dayArray[] = {"saturday", "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
    public String dayOfWeek;
    private String mytime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journey_histiory);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Journey");

        journeyDate = findViewById(R.id.journeyDate);
        destination = findViewById(R.id.destination);
        deadWeight = findViewById(R.id.deadWeight);
        draught = findViewById(R.id.draught);

        done = findViewById(R.id.done);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        uid = firebaseAuth.getUid();

        progressDialog = new ProgressDialog(EditJourneyHistioryActivity.this);

        loadContents();


        //requesting focus to edit text
        destination.requestFocus();
//        final String timeInMill=String.valueOf(System.currentTimeMillis());
//        mytime= DateFormat.format("dd.MM.yy", Long.parseLong(timeInMill)).toString();
//
//        journeyDate.setText(mytime);

        journeyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                                calendar.set(year, month, day);
                                dayint = calendar.get(Calendar.DAY_OF_WEEK);
                                dayOfWeek = dayArray[dayint];
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                                mytime = (simpleDateFormat.format(calendar.getTime()));
                                journeyDate.setText(mytime);

                            }
                        }, year, month, dayOfMonth);
                //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Loading..");
                progressDialog.show();

                sJourneyDate = journeyDate.getText().toString();
                sDestination = destination.getText().toString();
                sDeadWeight = deadWeight.getText().toString();
                sDraught = draught.getText().toString();

                if (TextUtils.isEmpty(sJourneyDate) ||
                        TextUtils.isEmpty(sDestination) ||
                        TextUtils.isEmpty(sDeadWeight)) {

                    progressDialog.dismiss();
                    Toast.makeText(context, "Please fill up all the required fields", Toast.LENGTH_SHORT).show();
                } else {
                    updateJourneyContents();
                }
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
                            sJourneyDate = task.getResult().getString("sJourneyDate");
                            sDestination = task.getResult().getString("sDestination");
                            sDeadWeight = task.getResult().getString("sDeadWeight");
                            sDraught = task.getResult().getString("sDraught");


                            if (TextUtils.isEmpty(sJourneyDate) ||
                                    TextUtils.isEmpty(sDestination) ||
                                    TextUtils.isEmpty(sDeadWeight)
                            ) {}
                            else {
                                journeyDate.setText(sJourneyDate);
                                destination.setText(sDestination);
                                deadWeight.setText(sDeadWeight);
                                draught.setText(sDraught);
                            }


                            progressDialog.dismiss();
                        } catch (Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Failed to retrive data from database", Toast.LENGTH_SHORT).show();
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

    private void updateJourneyContents() {

        Map<String, Object> updateHistory = new HashMap<>();
        updateHistory.put("sJourneyDate", sJourneyDate);
        updateHistory.put("sDestination", sDestination);
        updateHistory.put("sDeadWeight", sDeadWeight);
        updateHistory.put("sDraught", sDraught);
        firebaseFirestore.collection("users").document(uid).update(updateHistory).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, "Journey History Updated successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, JourneyHistory.class);
                finish();
                progressDialog.dismiss();
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(context, "Error occurred, please try again", Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
