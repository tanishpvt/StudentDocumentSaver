package com.example.studentdatacard.EncryptionData;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.studentdatacard.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

public class MainLogic extends AppCompatActivity {

    Button btn_enc,btn_dec;
    ImageView imageView;

    private static final String FILE_NAME_DEC="demo.png";
    private  static  final  String FILE_NAME_ENC = "demo";
    File myDir;

    //key (manual key)
    String my_key="b12is6hWa90sm4nf";  //16char =  128bit
    String my_spec_key="J2it56S7a8m0v8be";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_logic);

        btn_enc = (Button)findViewById(R.id.encp);
        btn_dec = (Button)findViewById(R.id.decp);
        imageView = (ImageView)findViewById(R.id.image_v);

        //Init path
        myDir = new File(Environment.getExternalStorageDirectory().toString()+"/Images");
        // myDir = new File(Environment.getExternalStorageState())

        //asking permission
        Dexter.withActivity(this)
                .withPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                })
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        btn_dec.setEnabled(true);
                        btn_enc.setEnabled(true);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(MainLogic.this, "Enable Permission", Toast
                                .LENGTH_SHORT);
                    }
                }).check();

        //decoding
        btn_dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputFileDec = new File(myDir,FILE_NAME_DEC);
                File encFile = new File(myDir,FILE_NAME_ENC);
                try {
                    MyEncryption.decryptToFile(my_key,my_spec_key,new FileInputStream(encFile),
                            new FileOutputStream(outputFileDec));

                    //image View
                    imageView.setImageURI(Uri.fromFile(outputFileDec));

                    //delete file after decoding...
                    // outputFileDec.delete();

                    Toast.makeText(MainLogic.this,"Decrypted",Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                }
            }
        });


        //encoding
        btn_enc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //changing this ....
                Drawable drawable = ContextCompat.getDrawable(MainLogic.this,R.drawable.demo);

                //convert Drawable to bitmap
                BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap(); //return bitmap
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                InputStream is = new ByteArrayInputStream(stream.toByteArray());

                //creating file
                File outputFileEn = new File(myDir,FILE_NAME_ENC);
                try{
                    MyEncryption.encryptToFile(my_key,my_spec_key,is,new FileOutputStream(outputFileEn));
                    Toast.makeText(MainLogic.this,"Encrypted",Toast.LENGTH_SHORT).show();




                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                }
            }
        });



    }
    //{
    //  "error": {
    //    "code": 403,
    //    "message": "Permission denied. Could not perform this operation"
    //  }
    //}
}