package com.rahul.jhkadiindiaclient;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rahul.jhkadiindiaclient.databinding.ActivityUploadBinding;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;


public class UploadActivity extends AppCompatActivity {
    private ActivityUploadBinding binding;
    Uri uri;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            binding.uploadImage.setImageURI(uri);
                        } else {
                            Toast.makeText(UploadActivity.this, "Something went wrong no image upload selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        binding.uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photopicker = new Intent(Intent.ACTION_PICK);
                photopicker.setType("image/*");
                activityResultLauncher.launch(photopicker);
            }
        });

        binding.savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidation()){
                    saveData();
                }else{

                }
            }
        });
    }
    public void saveData(){
        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Android Images").child(Objects.requireNonNull(uri.getLastPathSegment()));

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask =  taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri uriImage = uriTask.getResult();
                imageUrl = uriImage.toString();
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void uploadData(){
        String productName = binding.productNameEdit.getText().toString().trim();
        String productRate = binding.productRateEdit.getText().toString();
        String productDiscountrate = binding.productDiscountrateEdit.getText().toString();
        String productDescription = binding.productDescriptionEdit.getText().toString();
        DataClass dataClass = new DataClass(productName,productRate,productDiscountrate,productDescription,imageUrl);

        // here we changing the child from title to currentDate
        // because we will be updating title as well and it may affect child value.

        String currentdate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
//Firebase Tutorials is the table name
        FirebaseDatabase.getInstance().getReference("Jhkadi Table").child(currentdate)
                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UploadActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(UploadActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();                    }
                });
    }

    public boolean isValidation() {
        if (binding.productNameEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(
                    this,
                    getString(R.string.product_name),
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        } else if (binding.productRateEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(
                    this,
                    getString(R.string.product_rate),
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        } else if (binding.productDiscountrateEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(
                    this,
                    getString(R.string.discount_rate),
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        } else if (binding.productDescriptionEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(
                    this,
                    getString(R.string.Description),
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        } else {
            return true;
        }
    }


}