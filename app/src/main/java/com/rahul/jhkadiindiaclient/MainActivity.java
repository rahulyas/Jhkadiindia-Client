package com.rahul.jhkadiindiaclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.rahul.jhkadiindiaclient.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    List<DataClass> dataList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    RecyclerViewAdapter radapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();

        if (isInternetAvailable()) {
            // Internet connection is available, proceed with your logic
//            databaseReference = FirebaseDatabase.getInstance().getReference("Jhkadi Table");

            GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this,1);
            binding.recyclerview.setLayoutManager(gridLayoutManager);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_loading);
            AlertDialog dialog = builder.create();
            dialog.show();

            dataList = new ArrayList<>();

            radapter = new RecyclerViewAdapter(MainActivity.this,dataList);
            binding.recyclerview.setAdapter(radapter);

            databaseReference = FirebaseDatabase.getInstance().getReference("Jhkadi Table");
            dialog.show();

            // here we recived the reference
            eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dataList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                        dataClass.setKey(dataSnapshot.getKey());
                        dataList.add(dataClass);
                    }
                    radapter.notifyDataSetChanged();
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialog.dismiss();
                }
            });

            binding.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                    startActivity(intent);
                }
            });

            binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchList(newText);
                    return true;
                }
            });

        } else {
            // No internet connection, show an AlertDialog or take appropriate action
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Internet Connection");
            builder.setMessage("Please connect to the internet to use this app.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Close the app or take appropriate action
                    finish();
                }
            });
            builder.show();
        }

    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void searchList(String txt){
        ArrayList<DataClass> searchList = new ArrayList<>();
        for(DataClass dataClass : dataList){
            if(dataClass.getProductName().toLowerCase().contains(txt.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        radapter.searchDataList(searchList);
    }

}