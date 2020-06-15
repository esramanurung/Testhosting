package com.example.projectmppl.fragment.metode;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectmppl.R;
import com.example.projectmppl.activity.KantongActivity;
import com.example.projectmppl.model.Kantong;
import com.example.projectmppl.model.KantongNonOrganik;
import com.example.projectmppl.model.RiwayatSampah;
import com.example.projectmppl.model.Transaksi;
import com.example.projectmppl.ui.ViewModelFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MetodeJemputFragment extends Fragment implements View.OnClickListener {


    @BindView(R.id.input_lokasi)
    EditText lokasiPenjemputan;
    @BindView(R.id.btn_request_jemput)
    Button btnRequest;
    @BindView(R.id.tambah_gambar)
    Button btnTambahGambar;
    @BindView(R.id.image_sampah)
    ImageView imageSampah;
    @BindView(R.id.textView2)
    TextView textView;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    private ArrayList<Kantong> listKey;
    private ArrayList<KantongNonOrganik> kantongNonOrganiks;
    private ArrayList<Kantong> inputPakaian;
    private int totalPoint;
    private ArrayList<String> list;


    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    private String cameraPermissions[];
    private String storagePermissions[];

    private ArrayList<String> listKeyRiwayat;
    private String keyRiwayat;




    //for checking profile photo
    String profilePhoto;

    public MetodeJemputFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_metode_jemput, container, false);
        ButterKnife.bind(this, view);
        btnRequest.setOnClickListener(this);
        btnTambahGambar.setOnClickListener(this);
        // Init arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};


        sendData();
        hideProgress();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFirebase();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tambah_gambar) {
            showImagePict();
        }
    }

    private void addTransaksi(Transaksi transaksi) {
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
        showProgress();
        saveRiwayat();
        firebaseDatabase
                .getReference()
                .child("transaksipenukaransampah")
                .child(currentUser)
                .push()
                .setValue(transaksi)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgress();
                        Intent intent = new Intent(getActivity(), KantongActivity.class);
                        intent.putExtra("saveData","removeData");
                        intent.putExtra("removeData","remove");
                        startActivity(intent);
                    }
                });

    }


    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("transaksipenukaransampah");
        firebaseDatabase = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("upload");
    }


    private void sendData() {
        initFirebase();
            btnRequest.setOnClickListener(view -> {
                loadDataFirebase();

            });

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = Objects.requireNonNull(getActivity()).getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Tag", "FragmentJemput.onResume() has been called.");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Tag", "FragmentJemput.onPause() has been called.");

    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        btnTambahGambar.setEnabled(false);
        lokasiPenjemputan.setEnabled(false);
        btnRequest.setEnabled(false);
    }

    private void loadDataFirebase() {
            initFirebase();
            showProgress();
            String lokasi = lokasiPenjemputan.getText().toString();
            String metode = "Jemput";
            String status = "Menunggu";
            String datetime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
            ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
            LiveData<DataSnapshot> liveData = viewModel.getdataSnapshotLiveData();

            kantongNonOrganiks = new ArrayList<>();
            listKey = new ArrayList<>();
            inputPakaian = new ArrayList<>();
            totalPoint = 0;

            liveData.observe(this, dataSnapshot -> {
                if (dataSnapshot != null) {

                    for (DataSnapshot dataItem : dataSnapshot.child(currentUser).child("data").child("elektronik").getChildren()) {
                        Kantong kantong = dataItem.getValue(Kantong.class);
                        listKey.add(kantong);
                        totalPoint = totalPoint + kantong.getJumlahPoint();
                    }

                    for (DataSnapshot dataItem : dataSnapshot.child(currentUser).child("data").child("nonOrganik").getChildren()) {
                        KantongNonOrganik kantongNonOrganik = dataItem.getValue(KantongNonOrganik.class);
                        kantongNonOrganiks.add(kantongNonOrganik);
                        totalPoint = totalPoint + kantongNonOrganik.getJumlahPoint();
                    }
                    for (DataSnapshot dataItem : dataSnapshot.child(currentUser).child("data").child("pakaian").getChildren()) {
                        Kantong kantongPakaian = dataItem.getValue(Kantong.class);
                        inputPakaian.add(kantongPakaian);
                        totalPoint = totalPoint + kantongPakaian.getJumlahPoint();
                    }
                    if (totalPoint != 0) {
                        if (TextUtils.isEmpty(lokasiPenjemputan.getText().toString())){
                            Toast.makeText(getContext(),"Silahkan masukkan lokasi",Toast.LENGTH_SHORT).show();
                        }
                        else if (mImageUri != null) {

                            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                                    + "." + getFileExtension(mImageUri));

                            fileReference.putFile(mImageUri)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!uriTask.isSuccessful());
                                        Uri downloadUri = uriTask.getResult();

                                        Handler handler = new Handler();
                                        handler.postDelayed(() -> {
                                        }, 500);
                                        Transaksi transaksi = new Transaksi(downloadUri.toString(), listKey, metode, status, datetime, totalPoint, lokasi, kantongNonOrganiks, inputPakaian);
                                        addTransaksi(transaksi);

                                    }).addOnFailureListener(e -> {

                            }).addOnProgressListener(taskSnapshot -> {
                                showProgress();
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressBar.setProgress((int) progress);

                            });

                        } else {
                            Toast.makeText(getContext(), "Tidak ada foto yang dipilih", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        mImageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void showImagePict(){
        if (!checkCameraPermission()) {
            requestCameraPermission();
        }
        else {
            pickFromCamera();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length >0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(getActivity(), "Enable Kamera dan Storage Permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length >0) {
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    }
                    else {
                        Toast.makeText(getActivity(), "Enable Storage Permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                // image is picked from gallery, get uri of image
                btnTambahGambar.setVisibility(View.VISIBLE);
                Picasso.get().load(mImageUri).into(imageSampah);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                // image is picked from camera, get uri of image
                btnTambahGambar.setVisibility(View.GONE);
                Picasso.get().load(mImageUri).into(imageSampah);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void saveRiwayat() {
        listKeyRiwayat = new ArrayList<>();
        keyRiwayat = "";
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("\\.", "_");
        ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
        LiveData<DataSnapshot> liveData = viewModel.getdataTransaksi();
        liveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    for (DataSnapshot dataItem : dataSnapshot.child(currentUser).getChildren()) {
                        keyRiwayat = dataItem.getKey();
                    }
                    RiwayatSampah riwayatSampah = new RiwayatSampah(keyRiwayat);
                    saveIdTransaksi(currentUser,riwayatSampah);
                }
            }
        });
    }

    private void saveIdTransaksi(String currentUser, RiwayatSampah riwayatSampah){
        HashMap<String, Object> result = new HashMap<>();
        result.put(currentUser,listKeyRiwayat);

        firebaseDatabase.getReference()
                .child("riwayatsampah")
                .child(currentUser)
                .push()
                .setValue(riwayatSampah)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
