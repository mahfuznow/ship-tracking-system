package just.cse.mahfuz.shiptrackingsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivityNew extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context = HomeActivityNew.this;

    NavigationView navigationView;
    CircleImageView imageView;
    ImageView image, facebook, twitter, googlePlus, youtube;
    TextView shipName, ownerName, ownerEmail, ownerPhone;
    String sImage, sShipName, sShipID, sPassword, sCountry, sOwnerName, sOwnerEmail, sOwnerPhone;
    String sJourneyDate, sDestination, sDeadWeight, sDraught;

    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    Button trigger, track, contacts, profile, journey;
    Boolean triggerState = false;
    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;
    String uid;
    FirebaseFirestore firebaseFirestore;

    Date internetDate;
    private boolean isGPS;


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
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Home");

        imageView = findViewById(R.id.imageView);

        trigger = findViewById(R.id.trigger);
        track = findViewById(R.id.track);
        contacts = findViewById(R.id.contacts);
        profile = findViewById(R.id.profile);
        journey = findViewById(R.id.journey);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        uid = firebaseAuth.getUid();
        progressDialog = new ProgressDialog(context);

        track = findViewById(R.id.track);

        createNotificationChannel();

        //Turning on gps if not, then start tracking
        turnOnGps();

        //nav Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        facebook = drawer.findViewById(R.id.facebook);
        twitter = drawer.findViewById(R.id.twitter);
        googlePlus = drawer.findViewById(R.id.google_plus);
        youtube = drawer.findViewById(R.id.youtube);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkInBrowser("www.facebook.com");
            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkInBrowser("www.twitter.com");
            }
        });
        googlePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkInBrowser("plus.google.com");
            }
        });
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkInBrowser("www.youtube.com");
            }
        });


        //nav header
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);

        image = hView.findViewById(R.id.image);
        shipName = hView.findViewById(R.id.shipName);
        ownerName = hView.findViewById(R.id.ownerName);
        ownerEmail = hView.findViewById(R.id.ownerEmail);
        ownerPhone = hView.findViewById(R.id.ownerPhone);

        loadContents();

        trigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!triggerState) {
                    turnOnGps();
                    if (isGPS) {
                        showJourneyDetailsDialouge();
                    } else {
                        Toast.makeText(HomeActivityNew.this, "Please Enable GPS", Toast.LENGTH_SHORT).show();
                        turnOnGps();
                    }
                } else {
                    showConfirmAlertDialouge("Do you want to End the journey?", new Runnable() {
                        @Override
                        public void run() {

                            progressDialog.setMessage("Please wait..");

                            //adding journey history to database
                            String startDate= sJourneyDate;
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                            String endDate= (simpleDateFormat.format(Calendar.getInstance().getTime()));
                            String destination= sDestination;
                            String deadWeight= sDeadWeight;
                            String draught= sDraught;

                            Map<String,String> addHistory = new HashMap<>();
                            addHistory.put("sStartDate",startDate);
                            addHistory.put("sEndDate",endDate);
                            addHistory.put("sDestination",destination);
                            addHistory.put("sDeadWeight",deadWeight);
                            addHistory.put("sDraught",draught);
                            addHistory.put("timestamp",String.valueOf(System.currentTimeMillis()));

                            firebaseFirestore.collection("users").document(uid).collection("journeyHistory").add(addHistory)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            //stopping the tracking service
                                            stopService(new Intent(HomeActivityNew.this, TrackingService.class));
                                            setTriggerIsOff();
                                            Toast.makeText(HomeActivityNew.this, "Journey Ended Successfully", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(HomeActivityNew.this, "An Error occurred, please check internet connection and try again", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                  }
                    });

                }
            }
        });

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                progressDialog.setCancelable(true);

                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("sShipID", sShipID);
                startActivity(intent);
                progressDialog.dismiss();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                progressDialog.setCancelable(true);


                Intent intent = new Intent(context, ProfileActivity.class);
                startActivity(intent);
                progressDialog.dismiss();
            }
        });
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                progressDialog.setCancelable(true);

                Intent intent = new Intent(context, ContactActivity.class);
                startActivity(intent);
                progressDialog.dismiss();
            }
        });
        journey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                progressDialog.setCancelable(true);


                Intent intent = new Intent(context, JourneyDetails.class);
                startActivity(intent);
                progressDialog.dismiss();
            }
        });


    }


    private void showJourneyDetailsDialouge() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

        View view = LayoutInflater.from(context).inflate(R.layout.activity_edit_journey_details, null);
        builder.setView(view);
        builder.setCancelable(true);
        final  AlertDialog alertDialog = builder.create();
        final TextView journeyDate;
        final EditText destination, deadWeight, draught;
        LinearLayout done;

        journeyDate = view.findViewById(R.id.journeyDate);
        destination = view.findViewById(R.id.destination);
        deadWeight = view.findViewById(R.id.deadWeight);
        draught = view.findViewById(R.id.draught);
        done = view.findViewById(R.id.done);

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
                    alertDialog.dismiss();
                    updateJourneyContents();
                }
            }
        });


        alertDialog.show();

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
             //starting tracking service
              startTracking();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(context, "Error occurred, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createNotificationChannel() {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel mChannel = new NotificationChannel("AIS", "A-AIS", NotificationManager.IMPORTANCE_DEFAULT);

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            // Configure the notification channel.
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setSound(alarmSound, attributes); // This is IMPORTANT

            NotificationManager mNotificationManager = getSystemService(NotificationManager.class);

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }


        }
    }

    public void openLinkInBrowser(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
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
                            sImage = task.getResult().getString("sImage");
                            sShipName = task.getResult().getString("sShipName");
                            sShipID = task.getResult().getString("sShipID");
                            sPassword = task.getResult().getString("sPassword");
                            sCountry = task.getResult().getString("sCountry");
                            sOwnerName = task.getResult().getString("sOwnerName");
                            sOwnerEmail = task.getResult().getString("sOwnerEmail");
                            sOwnerPhone = task.getResult().getString("sOwnerPhone");

                            sJourneyDate = task.getResult().getString("sJourneyDate");
                            sDestination = task.getResult().getString("sDestination");
                            sDeadWeight = task.getResult().getString("sDeadWeight");
                            sDraught = task.getResult().getString("sDraught");


                            //setting contents to the navigation drawer

                            if (!"".equals(image) && sImage != null) {
                                //picture in home
                                Glide.with(context)
                                        .load(sImage)
                                        //.override(80, 80)
                                        //.thumbnail(0.1f)
                                        .into(imageView);
                                //picture in navigation
                                Glide.with(context)
                                        .load(sImage)
                                        //.override(80, 80)
                                        //.thumbnail(0.1f)
                                        .into(image);
                            }
                            shipName.setText(sShipName);
                            ownerName.setText(sOwnerName);
                            ownerEmail.setText(sOwnerEmail);
                            ownerPhone.setText(sOwnerPhone);

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

    /*****************************************************************/
    private void startTracking() {
        progressDialog.setMessage("Initializing Tracking...");
        progressDialog.show();
        progressDialog.setCancelable(true);
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            startTrackerService();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
//If the permission has been granted...//
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//...then start the GPS tracking service//
            startTrackerService();
        } else {
//If the user denies the permission request, then display a toast with some more information//
            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

    private void startTrackerService() {
        startService(new Intent(this, TrackingService.class));
//Notify the user that tracking has been enabled//
        Toast.makeText(this, "GPS tracking enabled, Check Notification area to see status", Toast.LENGTH_LONG).show();
//Close MainActivity//
        setTriggerIsOn();
        progressDialog.dismiss();
    }


    public void setTriggerIsOn() {
        trigger.setText("End Journey");
        trigger.setBackground(getDrawable(R.drawable.round_white_cyan));
        triggerState = true;
    }

    public void setTriggerIsOff() {
        //stopping service
        stopService(new Intent(HomeActivityNew.this, TrackingService.class));

        trigger.setText("Start Journey");
        trigger.setBackground(getDrawable(R.drawable.round_white));
        triggerState = false;

        progressDialog.dismiss();
    }

    /*****************************************************/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            //super.onBackPressed();
            showConfirmAlertDialouge("Do You want to exit?", new Runnable() {
                @Override
                public void run() {
                    moveTaskToBack(true);
                    HomeActivityNew.this.finish();
                    // android.os.Process.killProcess(android.os.Process.myPid());
                    // System.exit(1);
                }
            });

        }
    }

    public void showConfirmAlertDialouge(String msg, final Runnable r) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                r.run();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /********************************************************/

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {


        } else if (id == R.id.nav_track) {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setCancelable(true);

            Intent intent = new Intent(context, MapsActivity.class);
            startActivity(intent);
            progressDialog.dismiss();

        } else if (id == R.id.nav_contacts) {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setCancelable(true);

            Intent intent = new Intent(context, ContactActivity.class);
            intent.putExtra("type", "contacts");
            startActivity(intent);
            progressDialog.dismiss();

        } else if (id == R.id.nav_profile) {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setCancelable(true);


            Intent intent = new Intent(context, ProfileActivity.class);
            startActivity(intent);
            progressDialog.dismiss();

        } else if (id == R.id.nav_journey) {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setCancelable(true);


            Intent intent = new Intent(context, JourneyDetails.class);
            startActivity(intent);
            progressDialog.dismiss();

        } else if (id == R.id.nav_logout) {
            progressDialog.setMessage("Logging out...");
            progressDialog.show();

            //stopping tracking service
            stopService(new Intent(HomeActivityNew.this, TrackingService.class));

            //Writing into firestore offline so that user no longer visible in the map
            Map<String, Object> setOffline = new HashMap<>();
            setOffline.put("sLatitude", "offline");
            setOffline.put("sLongitude", "offline");
            setOffline.put("sSpeed", "offline");
            firebaseFirestore.collection("users").document(uid).update(setOffline);

            firebaseAuth.signOut();
            finish();
            Intent intent = new Intent(context, LogInActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_contact) {
            Intent emailIntent = new Intent();
            emailIntent.setAction(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:contact@email.com"));
            startActivity(emailIntent);

        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share play store link via"));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onResume() {
        navigationView.setCheckedItem(R.id.nav_home);
        super.onResume();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //for turning on gps
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
                //Toast.makeText(context,String.valueOf(isGPS),Toast.LENGTH_SHORT).show();
                startTracking();
            }
        }
    }

    public void turnOnGps() {
        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                isGPS = isGPSEnable;
            }
        });
//        Intent viewIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivity(viewIntent);
    }

}
