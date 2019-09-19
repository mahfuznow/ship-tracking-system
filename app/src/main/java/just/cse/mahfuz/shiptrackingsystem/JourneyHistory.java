package just.cse.mahfuz.shiptrackingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import just.cse.mahfuz.shiptrackingsystem.Adapter.HistoryListRecyclerAdapter;
import just.cse.mahfuz.shiptrackingsystem.Adapter.ShipListRecyclerAdapter;
import just.cse.mahfuz.shiptrackingsystem.Model.History;
import just.cse.mahfuz.shiptrackingsystem.Model.Users;

public class JourneyHistory extends AppCompatActivity {

    Context context = JourneyHistory.this;
    TextView title;
    RecyclerView recyclerView;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String uid;
    HistoryListRecyclerAdapter historyListRecyclerAdapter;

    ProgressDialog progressDialog;

    String type=" ";
    private SearchView searchView;

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        getSupportActionBar().setTitle("Journey History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title=findViewById(R.id.title);
        recyclerView = findViewById(R.id.recyclerView);

        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        uid= firebaseAuth.getUid();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        progressDialog = new ProgressDialog(context);

        progressDialog.setMessage("Loading..");
        progressDialog.show();



        Query query = firebaseFirestore.collection("users").document(uid).collection("journeyHistory").orderBy("timestamp", Query.Direction.DESCENDING);


        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        try {
                            List<History> historyModel = task.getResult().toObjects(History.class);
                            historyListRecyclerAdapter = new HistoryListRecyclerAdapter(context, historyModel,type);
                            recyclerView.setAdapter(historyListRecyclerAdapter);
                            progressDialog.dismiss();
                        }
                        catch (Exception e) {
                            Toast.makeText(context,"An Error Occurred",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"An Error Occurred",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });


//        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//
//                if (e != null) {
//                    return;
//                }
//                List<Users> userModel = queryDocumentSnapshots.toObjects(Users.class);
//
//                shipListRecyclerAdapter = new ShipListRecyclerAdapter(context, userModel, "contacts");
//                recyclerView.setAdapter(shipListRecyclerAdapter);
//            }
//        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                historyListRecyclerAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                historyListRecyclerAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        if (id==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
