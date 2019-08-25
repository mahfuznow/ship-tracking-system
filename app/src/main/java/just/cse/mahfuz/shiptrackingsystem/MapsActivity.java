package just.cse.mahfuz.shiptrackingsystem;

import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Context;
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
import just.cse.mahfuz.shiptrackingsystem.Model.Users;

import java.util.Arrays;

import static java.util.Arrays.asList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    Context context = MapsActivity.this;

    private GoogleMap mMap;
    int DEFAULT_ZOOM = 5;
    //private GeoDataClient mGeoDataClient;

    Bitmap bitmap;
    String sImage, sShipName, sShipID, sCountry, sOwnerName, sOwnerEmail, sOwnerPhone;

    CircleImageView image;
    TextView location,shipID, shipName, ownerName, ownerEmail, ownerPhone;

    List<Users> userModel;

    FirebaseFirestore firebaseFirestore;

    ProgressDialog progressDialog;
    //ArrayList<String> markerPlaces;
    String markerPlace[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(context);


        markerPlace= new String[50];

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
//

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
                    for (int i = 0; i < userModel.size(); i++) {
                        createMarker(i);
                    }
                } else {
                    progressDialog.dismiss();
                    System.out.print("Current data: null");
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //setting custom layout for marker details
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, null);

                image = infoWindow.findViewById(R.id.image);

                location = infoWindow.findViewById(R.id.location);
                shipName = ((TextView) infoWindow.findViewById(R.id.shipName));
                ownerName = ((TextView) infoWindow.findViewById(R.id.ownerName));
                ownerEmail = ((TextView) infoWindow.findViewById(R.id.ownerEmail));
                ownerPhone = ((TextView) infoWindow.findViewById(R.id.ownerPhone));


               //checking marker id & find corresponding data
                int index = -1; //to check index not found
                for (int i=0;i<markerPlace.length;i++) {
                    if (markerPlace[i].equals(marker.getId())) {
                        index = i;
                        break;
                    }
                }


                if (index!=-1) {
                    if (!"".equals(userModel.get(index).getsImage()) && equals(userModel.get(index).getsImage() != null)) {
                        Glide.with(context)
                                .load(sImage)
                                .into(image);
                    }

                    location.setText("Lat: "+userModel.get(index).getsLatitude()+"\nLng: "+userModel.get(index).getsLongitude()+"\nSpeed: "+userModel.get(index).getsSpeed());
                    shipName.setText(userModel.get(index).getsShipName());
                    ownerName.setText(userModel.get(index).getsOwnerName());
                    ownerEmail.setText(userModel.get(index).getsOwnerEmail());
                    ownerPhone.setText(userModel.get(index).getsOwnerPhone());
                }



                return infoWindow;
            }
        });
//


        //default location is Jamuna bridge
        LatLng mDefaultLocation = new LatLng(24.3997, 89.7772);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation,DEFAULT_ZOOM));


    }

    public void createMarker(int i) {

        LatLng location = new LatLng(Double.valueOf(userModel.get(i).getsLatitude()), Double.valueOf(userModel.get(i).getsLongitude()));
        Marker marker = mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon)));
        markerPlace[i]=marker.getId();

    }

}
