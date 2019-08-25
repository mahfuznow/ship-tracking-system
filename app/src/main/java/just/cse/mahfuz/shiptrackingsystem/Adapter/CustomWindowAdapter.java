package just.cse.mahfuz.shiptrackingsystem.Adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import just.cse.mahfuz.shiptrackingsystem.Model.Users;
import just.cse.mahfuz.shiptrackingsystem.R;

public class CustomWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mymarkerview;
    private Context context;
    private List<Users> userModel;
    private Location currentMarker;

    CircleImageView image;
    TextView shipID, shipName, ownerName, ownerEmail, ownerPhone;

    public CustomWindowAdapter(Context context) {
        this.context = context;
        //currentMarker = new Location();
        mymarkerview = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_info_contents, null);
    }

    public View getInfoWindow(Marker marker) {
        render(marker, mymarkerview);
        return mymarkerview;
    }

    public View getInfoContents(Marker marker) {
        View infoWindow = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_info_contents, null);

        // Getting the position from the marker
        LatLng latLng = marker.getPosition();
        // Getting reference to the TextView to set latitude

        image = infoWindow.findViewById(R.id.image);
        shipName = ((TextView) infoWindow.findViewById(R.id.shipName));
        ownerName = ((TextView) infoWindow.findViewById(R.id.ownerName));
        ownerEmail = ((TextView) infoWindow.findViewById(R.id.ownerEmail));
        ownerPhone = ((TextView) infoWindow.findViewById(R.id.ownerPhone));

        return infoWindow;
    }

    private void render(Marker marker, View view) {

        // Add the code to set the required values
        // for each element in your custominfowindow layout file
    }

    public void setModels(List<Users> userModel) {
        this.userModel = userModel;
    }
}
