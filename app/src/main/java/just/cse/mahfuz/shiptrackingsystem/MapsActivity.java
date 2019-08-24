package just.cse.mahfuz.shiptrackingsystem;

import android.app.VoiceInteractor;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback  {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private LatLng mDefaultLocation;
    private Location mLastKnownLocation;
    FusedLocationProviderClient mFusedLocationProviderClient;
    int DEFAULT_ZOOM=10;
    //private GeoDataClient mGeoDataClient;

    Bitmap bitmap;
    String sImage, sShipName, sShipID, sCountry, sOwnerName, sOwnerEmail, sOwnerPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

       // mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //getLocationPermission();



        try {
            byte[] b = getIntent().getExtras().getByteArray("image");
            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);

            sShipID=getIntent().getExtras().getString("shipID");
            sShipName=getIntent().getExtras().getString("shipName");
            sOwnerName=getIntent().getExtras().getString("ownerName");
            sOwnerEmail=getIntent().getExtras().getString("ownerEmail");
            sOwnerPhone=getIntent().getExtras().getString("ownerPhone");
        }
        catch (Exception e) {

        }


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //default location is Jamuna bridge
        mDefaultLocation = new LatLng(24.3997, 89.7772);



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

                try {
                    CircleImageView image= infoWindow.findViewById(R.id.image);
                    image.setImageBitmap(bitmap);

                    TextView shipName = ((TextView) infoWindow.findViewById(R.id.shipName));
                    shipName.setText(sShipName);

                    TextView ownerName = ((TextView) infoWindow.findViewById(R.id.ownerName));
                    ownerName.setText(sOwnerName);

                    TextView ownerEmail = ((TextView) infoWindow.findViewById(R.id.ownerEmail));
                    ownerEmail.setText(sOwnerEmail);

                    TextView ownerPhone = ((TextView) infoWindow.findViewById(R.id.ownerPhone));
                    ownerPhone.setText(sOwnerPhone);
                }
                catch (Exception e) {
                    CircleImageView image= infoWindow.findViewById(R.id.image);
                    image.setImageResource(R.drawable.ship_icon);

                    TextView shipName = ((TextView) infoWindow.findViewById(R.id.shipName));
                    shipName.setText("ShipName");

                    TextView ownerName = ((TextView) infoWindow.findViewById(R.id.ownerName));
                    ownerName.setText("OwnerName");

                    TextView ownerEmail = ((TextView) infoWindow.findViewById(R.id.ownerEmail));
                    ownerEmail.setText("sOwnerEmail");

                    TextView ownerPhone = ((TextView) infoWindow.findViewById(R.id.ownerPhone));
                    ownerPhone.setText("OwnerPhone");
                }


                return infoWindow;
            }
        });

        // Turn on the My Location layer and the related control on the map.
       // updateLocationUI();

        // Get the current location of the device and set the position of the map.
        //getDeviceLocation();


        mMap.addMarker(new MarkerOptions().position(mDefaultLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_ship)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation,DEFAULT_ZOOM));

    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device
                            try {
                                mLastKnownLocation = (Location) task.getResult();

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                            catch (Exception e) {

                            }

                        } else {
                            Log.d("Error", "Current location is null. Using defaults.");
                            Log.e("Error", "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
