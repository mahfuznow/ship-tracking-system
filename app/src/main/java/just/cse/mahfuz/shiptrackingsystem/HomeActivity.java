package just.cse.mahfuz.shiptrackingsystem;

import android.app.AlertDialog;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context = HomeActivity.this;

    NavigationView navigationView;
    ImageView image,facebook,twitter,googlePlus,youtube;
    TextView shipName, ownerName, ownerEmail, ownerPhone;
    String sImage,sShipName,sShipID,sPassword,sCountry, sOwnerName, sOwnerEmail, sOwnerPhone;

    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    Button track,contacts,profile,journey;
    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;
    String uid;
    FirebaseFirestore firebaseFirestore;

    Date internetDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        track=findViewById(R.id.track);
        contacts=findViewById(R.id.contacts);
        profile=findViewById(R.id.profile);
        journey=findViewById(R.id.journey);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        uid = firebaseAuth.getUid();
        progressDialog = new ProgressDialog(context);

        track=findViewById(R.id.track);

        createNotificationChannel();

        //nav Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        facebook= drawer.findViewById(R.id.facebook);
        twitter= drawer.findViewById(R.id.twitter);
        googlePlus= drawer.findViewById(R.id.google_plus);
        youtube= drawer.findViewById(R.id.youtube);

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
                openLinkInBrowser("www.facebook.com");
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
        startTracking();

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                progressDialog.setCancelable(true);

                Intent intent = new Intent(context, ShipListActivity.class);
                intent.putExtra("type","track");
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

                Intent intent = new Intent(context, ShipListActivity.class);
                intent.putExtra("type","contacts");
                startActivity(intent);
                progressDialog.dismiss();
            }
        });


    }

    public  void  createNotificationChannel() {
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
                    }
                });

    }

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
        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();
//Close MainActivity//
        progressDialog.dismiss();
    }

    /*****************************************************/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do want to exit?");
            builder.setCancelable(true);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    moveTaskToBack(true);
                    finish();
                    // android.os.Process.killProcess(android.os.Process.myPid());
                    // System.exit(1);
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
            //super.onBackPressed();


        }
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

            Intent intent = new Intent(context, ShipListActivity.class);
            intent.putExtra("type","track");
            startActivity(intent);
            progressDialog.dismiss();

        } else if (id == R.id.nav_contacts) {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setCancelable(true);

            Intent intent = new Intent(context, ShipListActivity.class);
            intent.putExtra("type","contacts");
            startActivity(intent);
            progressDialog.dismiss();

        }
        else if (id == R.id.nav_profile) {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setCancelable(true);


            Intent intent = new Intent(context, ProfileActivity.class);
            startActivity(intent);
            progressDialog.dismiss();

        }
        else if (id == R.id.nav_journey) {


        }
        else if (id == R.id.nav_logout) {
            progressDialog.setMessage("Logging out....");
            progressDialog.show();
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

}
