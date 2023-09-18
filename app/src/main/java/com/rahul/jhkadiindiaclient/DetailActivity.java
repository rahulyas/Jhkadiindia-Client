package com.rahul.jhkadiindiaclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rahul.jhkadiindiaclient.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    Boolean isAllFabsVisible;
    String key="";
    String imageUrl="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            binding.productTitle.setText(bundle.getString("productName"));
            Glide.with(this).load(bundle.getString("dataImage")).into(binding.productImage);
            binding.productRate.setText(bundle.getString("productRate"));
            binding.productDiscountrate.setText(bundle.getString("discountRate"));
            binding.productDescription.setText(bundle.getString("productDescription"));
            key = bundle.getString("key");
//            Log.d("TAG", "Detail: key:== "+key);
            imageUrl = bundle.getString("dataImage");
            Toast.makeText(this, "Recived", Toast.LENGTH_SHORT).show();
        }
        isAllFabsVisible = false;
        binding.addFab.shrink();

        binding.addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAllFabsVisible){
                    binding.delete.show();
                    binding.delete.setVisibility(View.VISIBLE);
                    binding.update.setVisibility(View.VISIBLE);
                    binding.addFab.extend();
                    isAllFabsVisible = true;
                }else{
                    binding.delete.hide();
                    binding.delete.setVisibility(View.GONE);
                    binding.update.setVisibility(View.GONE);
                    binding.addFab.shrink();
                    isAllFabsVisible = false;
                }
            }
        });
        //Firebase Tutorials is the table name
        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Jhkadi Table");
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();
                        Toast.makeText(DetailActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
            }
        });

        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, UpdateActivity.class);
                intent.putExtra("productName", binding.productTitle.getText().toString());
                intent.putExtra("productRate", binding.productRate.getText().toString());
                intent.putExtra("discountRate", binding.productDiscountrate.getText().toString());
                intent.putExtra("productDescription", binding.productDescription.getText().toString());
                intent.putExtra("dataImage", imageUrl);
                intent.putExtra("key", key);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Your custom logic here
        // For example, you can show a dialog or navigate to another screen
    }

}