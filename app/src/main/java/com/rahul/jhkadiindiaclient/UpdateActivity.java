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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rahul.jhkadiindiaclient.databinding.ActivityUpdateBinding;

public class UpdateActivity extends AppCompatActivity {

    private ActivityUpdateBinding binding;

    String imageUrl;
    String key, oldImageUrl;
    Uri uri;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data!= null) {
                        uri = data.getData();
                        binding.updateImage.setImageURI(uri);
                    }else {
                        Toast.makeText(UpdateActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            binding.productNameEdit.setText(bundle.getString("productName"));
            Glide.with(this).load(bundle.getString("dataImage")).into(binding.updateImage);
            binding.productRateEdit.setText(bundle.getString("productRate"));
            binding.productDiscountrateEdit.setText(bundle.getString("discountRate"));
            binding.productDescriptionEdit.setText(bundle.getString("productDescription"));            key = bundle.getString("key");
            oldImageUrl = bundle.getString("dataImage");
            Toast.makeText(this, "Recived", Toast.LENGTH_SHORT).show();
        }
//Firebase Tutorials is the table name
        databaseReference = FirebaseDatabase.getInstance().getReference("Jhkadi Table").child(key);

        binding.updateImage.setOnClickListener(new View.OnClickListener() {
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
        if (uri != null) {
            storageReference= FirebaseStorage.getInstance().getReference().child("Android Images").child(uri.getLastPathSegment());
            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            builder.setTitle("Uploading....");
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
                    Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(UpdateActivity.this, "Please Select picture also .", Toast.LENGTH_SHORT).show();
         /*   Uri uri1 = Uri.parse(oldImageUrl);
            storageReference= FirebaseStorage.getInstance().getReference().child("Android Images").child(uri1.getLastPathSegment());

            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            builder.setTitle("Uploading....");
            AlertDialog dialog = builder.create();
            dialog.show();

            storageReference.putFile(uri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                    Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });*/
        }

    }

    public void uploadData(){
        String productName = binding.productNameEdit.getText().toString().trim();
        String productRate = binding.productRateEdit.getText().toString();
        String productDiscountrate = binding.productDiscountrateEdit.getText().toString();
        String productDescription = binding.productDescriptionEdit.getText().toString();
        DataClass dataClass = new DataClass(productName,productRate,productDiscountrate,productDescription,imageUrl);

        databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
                    reference.delete();
                    Toast.makeText(UpdateActivity.this, "Data Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UpdateActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
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