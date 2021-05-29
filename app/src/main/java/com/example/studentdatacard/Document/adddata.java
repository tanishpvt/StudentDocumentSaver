package com.example.studentdatacard.Document;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.studentdatacard.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class adddata extends AppCompatActivity
{
    EditText name,course,email,purl;
    Button submit,back;
    ImageView selected_img;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //image picked will be saved in this
    Uri image_rui=null;

    //permission constants
    private static final int CAMERA_REQUEST_CODE =100;
    private static final int STORAGE_REQUEST_CODE =200;


    //permission constants
    private static final int IMAGE_PICK_CAMERA_CODE =300;
    private static final int IMAGE_PICK_GALLERY_CODE=400;

    //permission array
    String[] cameraPermessions;
    String[] storagePermessions;

    //progresses bar
    ProgressDialog pd;
    model model;
    String mUid;
    FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null) {
            mUid=mAuth.getUid();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddata);

        name=(EditText)findViewById(R.id.add_name);
        email=(EditText)findViewById(R.id.add_email);
        course=(EditText)findViewById(R.id.add_course);
        purl=(EditText)findViewById(R.id.add_purl);
        selected_img =findViewById(R.id.selected_img);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        assert bundle != null;
        model= (model) bundle.getSerializable("Object");
        submit=(Button)findViewById(R.id.add_submit);
        if(!model.getPurl().isEmpty()){
            //set
            name.setText(model.getName());
            email.setText(model.getEmail());
            course.setText(model.getCourse());
            purl.setText(model.getPurl());
//        bookId.setText(book.getbId());
            Picasso.get().load(model.getPurl()).into(selected_img);
            submit.setText("Update");
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateinsert();
                }
            });
        }else{
            submit.setText("Upload");
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    processinsert();
                }
            });
        }
        back=(Button)findViewById(R.id.add_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),DocumentActivity.class));
                finish();
            }
        });




        pd= new ProgressDialog(this);

        //init firebase
        firebaseDatabase=FirebaseDatabase.getInstance();

        //init permissions
        cameraPermessions=new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermessions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //image
        selected_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show image dialog
                //    Toast.makeText(AddPostActivity.this, "hey", Toast.LENGTH_SHORT).show();

                showImageDialog();
                //showDialogtoSelect();
            }
        });
    }
// encoding process plus uploading
    private void updateinsert(){
        pd.setMessage("Updating Image");
        pd.setCancelable(false);
        pd.show();
        StorageReference mPictureRef = FirebaseStorage.getInstance().getReferenceFromUrl(model.getPurl());
        mPictureRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.setMessage("publishing post...");
                        pd.show();
                        final String timestamp = String.valueOf(System.currentTimeMillis());
                        String filePathName = "Posts/" + "post_" + timestamp;


                        Bitmap bitmap = ((BitmapDrawable) selected_img.getDrawable()).getBitmap();

                        ByteArrayOutputStream bout = new ByteArrayOutputStream();
                        //image compress
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bout);
                        byte[] data = bout.toByteArray();

                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathName);
                        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful()) ;

                                String downloadUri = uriTask.getResult().toString();

                                if (uriTask.isSuccessful()) {
                                    Map<String,Object> map=new HashMap<>();
                                    map.put("name",name.getText().toString());
                                    map.put("iD",model.getiD());
//                                    map.put("userId",mUid);
                                    map.put("course",course.getText().toString());
                                    map.put("email",email.getText().toString());


//                                    String uri = Uri.parse("http://...")
//                                            .buildUpon()
//                                            .appendQueryParameter("key", "val")
//                                            .build().toString();
                                    map.put("purl",downloadUri); // encoding method



                                    FirebaseDatabase.getInstance().getReference().child("students").child(model.getiD())
                                            .updateChildren(map)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    name.setText("");
                                                    course.setText("");
                                                    email.setText("");
                                                    purl.setText("");
                                                    //reset view
                                                    selected_img.setImageURI(null);
                                                    image_rui = null;
                                                    Toast.makeText(getApplicationContext(),"Inserted Successfully",Toast.LENGTH_LONG).show();
                                                    pd.dismiss();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e)
                                                {
                                                    Toast.makeText(getApplicationContext(),"Could not insert",Toast.LENGTH_LONG).show();
                                                }
                                            });

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(adddata.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void processinsert() {

        pd.setMessage("publishing post...");
        pd.setCancelable(false);
        pd.show();
        final String timestamp= String.valueOf(System.currentTimeMillis());
        String filePathName="Posts/"+"post_"+timestamp;


        Bitmap bitmap=((BitmapDrawable)selected_img.getDrawable()).getBitmap();

        ByteArrayOutputStream bout=new ByteArrayOutputStream();
        //image compress
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bout);
        byte[] data=bout.toByteArray();

        StorageReference ref= FirebaseStorage.getInstance().getReference().child(filePathName);


        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());

                String downloadUri=uriTask.getResult().toString();

                if(uriTask.isSuccessful()){
                    //uri is received upload post to firebase database
                    String iD= FirebaseDatabase.getInstance().getReference().push().getKey();
                    Map<String,Object> map=new HashMap<>();
                    map.put("name",name.getText().toString());
                    map.put("iD",iD);
                    map.put("course",course.getText().toString());
                    map.put("email",email.getText().toString());
                    map.put("purl",downloadUri);
                    FirebaseDatabase.getInstance().getReference().child("students").child(iD)
                            .setValue(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    name.setText("");
                                    course.setText("");
                                    email.setText("");
                                    purl.setText("");
                                    Toast.makeText(getApplicationContext(),"Inserted Successfully",Toast.LENGTH_LONG).show();
                                    pd.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Toast.makeText(getApplicationContext(),"Could not insert",Toast.LENGTH_LONG).show();
                                }
                            });

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(adddata.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void showImageDialog() {


        String[] options={"Camera","Gallery"};

        //dialog box
        AlertDialog.Builder builder=new AlertDialog.Builder(adddata.this);

        builder.setTitle("Choose Action");



        Toast.makeText(this, " reached", Toast.LENGTH_SHORT).show();
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which==0){
                    //camera clicked
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                if(which==1){
                    //camera clicked

                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    private void pickFromCamera() {

        ContentValues cv=new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");
        image_rui=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);


        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_rui);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {

        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }


    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermessions,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return result&&result1;
    }


    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermessions,CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{

                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1]== PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted&&storageAccepted){

                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(this, "camera  & gallery both permission needed", Toast.LENGTH_SHORT).show();
                    }
                }
                else{

                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>1){
                    boolean storageAccepted=false;
                    try {
                        storageAccepted=grantResults[1]== PackageManager.PERMISSION_GRANTED;
                    }catch (ArrayIndexOutOfBoundsException e){
                        Toast.makeText(this, ""+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                    if(storageAccepted){

                        pickFromGallery();
                    }
                    else {
                        //Toast.makeText(this, "gallery both permission needed", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    boolean storageAccepted=false;
                    try {
                        storageAccepted=grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    }catch (ArrayIndexOutOfBoundsException e){
                        Toast.makeText(this, ""+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                    if(storageAccepted){

                        pickFromGallery();
                    }
                    else {
                        //Toast.makeText(this, "gallery both permission needed", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==IMAGE_PICK_GALLERY_CODE){
                image_rui=data.getData();

                selected_img.setImageURI(image_rui);
            }
            else if(requestCode==IMAGE_PICK_CAMERA_CODE){

                selected_img.setImageURI(image_rui);

            }
        }
    }

}