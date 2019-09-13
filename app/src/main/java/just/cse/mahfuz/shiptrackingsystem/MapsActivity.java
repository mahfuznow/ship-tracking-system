package just.cse.mahfuz.shiptrackingsystem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import just.cse.mahfuz.shiptrackingsystem.Adapter.CustomWindowAdapter;
import just.cse.mahfuz.shiptrackingsystem.Adapter.ShipListRecyclerAdapter;
import just.cse.mahfuz.shiptrackingsystem.Class.OnInfoItemTouchListener;
import just.cse.mahfuz.shiptrackingsystem.Model.Users;

import java.util.Arrays;

import static java.util.Arrays.asList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    Context context = MapsActivity.this;

    private GoogleMap mMap;
    int DEFAULT_ZOOM = 5;
    int CURENT_USER_MARKER_ZOOM = 10;
    //private GeoDataClient mGeoDataClient;

    Button mapType;

    Bitmap bitmap;
    String sImage, sShipName, sShipID, sCountry, sOwnerName, sOwnerEmail, sOwnerPhone;

    LatLng currentUserMarkerLocation;
    CircleImageView image;
    ImageView close;
    TextView location, shipID, shipName, ownerName, ownerEmail, ownerPhone, destination, deadWeight, draught, journeyDate;

    List<Users> userModel;

    FirebaseFirestore firebaseFirestore;

    ProgressDialog progressDialog;
    //ArrayList<String> markerPlaces;
    String markerPlace[];
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean isGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(context);

        currentUserMarkerLocation = new LatLng(0.0, 0.0);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mapType = findViewById(R.id.mapType);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
//

        //turning on GPS
        turnOnGps();

        try {
            sShipID = getIntent().getExtras().getString("sShipID");
        } catch (Exception e) {

        }

        //adding snapshot listener to database
        Query query = firebaseFirestore.collection("users");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.err.println("Listen failed: " + e);
                    return;
                }
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    userModel = queryDocumentSnapshots.toObjects(Users.class);
                    progressDialog.dismiss();
                    // Toast.makeText(context,userModel.get(0).getsShipID()+ " "+ userModel.get(3).getsOwnerName(),Toast.LENGTH_LONG).show();

                    //clearing map first
                    mMap.clear();
                    //clearing array (should be initiated here)
                    markerPlace = new String[50];
                    for (int i = 0; i < userModel.size(); i++) {
                        createMarker(i);
                    }

                } else {
                    progressDialog.dismiss();
                    System.out.print("Current data: null");
                }
            }
        });

        mapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMapTypeSelectorDialog();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //enabling my location button
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (!(currentUserMarkerLocation.latitude==0.0)) {
            //checking current user is offline
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentUserMarkerLocation, CURENT_USER_MARKER_ZOOM));
                    return false;
                }
            });
        }
        else  {
            mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentUserMarkerLocation, CURENT_USER_MARKER_ZOOM));
                }
            });
        }



        //setting custom layout for marker details
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public View getInfoContents(final Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                final View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, null);


                close = infoWindow.findViewById(R.id.close);

                image = infoWindow.findViewById(R.id.image);

                location = infoWindow.findViewById(R.id.location);
                shipID = ((TextView) infoWindow.findViewById(R.id.shipID));
                shipName = ((TextView) infoWindow.findViewById(R.id.shipName));
                ownerName = ((TextView) infoWindow.findViewById(R.id.ownerName));
                ownerEmail = ((TextView) infoWindow.findViewById(R.id.ownerEmail));
                ownerPhone = ((TextView) infoWindow.findViewById(R.id.ownerPhone));

                destination = ((TextView) infoWindow.findViewById(R.id.destination));
                deadWeight = ((TextView) infoWindow.findViewById(R.id.deadWeight));
                draught = ((TextView) infoWindow.findViewById(R.id.draught));
                journeyDate = ((TextView) infoWindow.findViewById(R.id.journeyDate));


                //checking marker id & find corresponding data
                int index = -1; //to check index not found
                for (int i = 0; i < markerPlace.length; i++) {
                    if (markerPlace[i].equals(marker.getId())) {
                        index = i;
                        break;
                    }
                }


                if (index != -1) {
                    if (!"".equals(userModel.get(index).getsImage()) && equals(userModel.get(index).getsImage() != null)) {
                        Glide.with(context)
                                .load(sImage)
                                .into(image);
                    }

                    location.setText("Lat: " + userModel.get(index).getsLatitude() + "\nLng: " + userModel.get(index).getsLongitude() + "\nSpeed: " + userModel.get(index).getsSpeed());
                    shipID.setText("Ship ID: " + userModel.get(index).getsShipID());
                    shipName.setText("Ship Name: " + userModel.get(index).getsShipName());
                    ownerName.setText("Owner's Name: " + userModel.get(index).getsOwnerName());
                    ownerEmail.setText("Owner's Email: " + userModel.get(index).getsOwnerEmail());
                    ownerPhone.setText("Owner's Phone: " + userModel.get(index).getsOwnerPhone());

                    destination.setText("Destination: " + userModel.get(index).getsDestination());
                    deadWeight.setText("DeadWeight: " + userModel.get(index).getsDeadWeight());
                    draught.setText("Draught: " + userModel.get(index).getsDraught());
                    journeyDate.setText("JourneyDate: " + userModel.get(index).getsJourneyDate());
                }

//                //force showing the info window when click on map (info window is opened)
//                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                    @Override
//                    public void onMapClick(LatLng latLng) {
//                        marker.showInfoWindow();
//                    }
//                });


//                close.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                                  }
//                });

//                close.setOnTouchListener(new OnInfoItemTouchListener(close,getResources().getDrawable(R.drawable.close),getResources().getDrawable(R.drawable.close)) {
//                    @Override
//                    protected void onClickConfirmed(View v, Marker marker) {
//                        marker.hideInfoWindow();
//                        Toast.makeText(MapsActivity.this,"sdfj",Toast.LENGTH_SHORT).show();
//                    }
//                });


                return infoWindow;
            }
        });

//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                if (marker.isInfoWindowShown()) {
//                    marker.hideInfoWindow();
//                }
//                else {
//                    marker.showInfoWindow();
//                }
//                return false;
//            }
//        });
//


        //default location is Jamuna bridge
        LatLng mDefaultLocation = new LatLng(24.3997, 89.7772);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));


    }

    public void createMarker(int i) {
        Marker marker;

        if ("offline".equals(userModel.get(i).getsLatitude()) || "offline".equals(userModel.get(i).getsLongitude())) {
            //do not create marker
            markerPlace[i] = "offline";
        }
        else {
            //creating marker
            //this is for the location of that user which is logged in
            LatLng location = new LatLng(Double.valueOf(userModel.get(i).getsLatitude()), Double.valueOf(userModel.get(i).getsLongitude()));

            if (userModel.get(i).getsShipID().equals(sShipID)) {
                marker = mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_green)));
                //storing the LatLng of the current user marker
                currentUserMarkerLocation = new LatLng(Double.valueOf(userModel.get(i).getsLatitude()), Double.valueOf(userModel.get(i).getsLongitude()));
            } else {
                marker = mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon)));
            }

            //keep tracking of the markers ID into an array
            markerPlace[i] = marker.getId();
        }



    }


    //method for choosing mapType
    private static final CharSequence[] MAP_TYPE_ITEMS = {"Normal", "Satellite", "Terrain", "Hybrid"};

    private void showMapTypeSelectorDialog() {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = mMap.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }


    /*****************************************************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }


    public void turnOnGps() {
        new GpsUtils(MapsActivity.this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                isGPS = isGPSEnable;
            }
        });
//        Intent viewIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivity(viewIntent);
    }

}