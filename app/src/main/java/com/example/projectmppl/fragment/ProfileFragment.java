package com.example.projectmppl.fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectmppl.R;
import com.example.projectmppl.activity.LoginRegister;
import com.example.projectmppl.ui.ViewModelFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.keluar)
    Button keluar;

    @BindView(R.id.edit_nama)
    EditText editNama;
    @BindView(R.id.btn_edit_nama)
    ImageButton btnNama;
    @BindView(R.id.edit_email)
    TextView editEmail;
    @BindView(R.id.edit_noHp)
    EditText editNoHp;
    @BindView(R.id.btn_edit_noHp)
    ImageButton btnNoHp;
    @BindView(R.id.pekerjaan)
    Spinner pekerjaann;
    @BindView(R.id.btn_edit_pekerjaan)
    ImageButton btnPekerjaan;
    @BindView(R.id.jenis_kelamin)
    TextView jenisKelamin;
    @BindView(R.id.edit_password)
    EditText editPassword;
    @BindView(R.id.btn_edit_sandi)
    ImageButton btnSandi;
    @BindView(R.id.btn_simpan)
    Button simpan;
    @BindView(R.id.img_profile)
    CircularImageView fotoProfil;
    @BindView(R.id.my_poin)
    TextView myPoin;
    @BindView(R.id.my_kupon)
    TextView myKupon;


    ProgressDialog pd;

    private View view;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;

    public static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    public static DatabaseReference mChildReferenceForInputHistory = databaseReference.child("penukarSampah");

    StorageReference storageReference;
    //path where images of user profile will be stored
    String storagePath = "Users_Profile_Imgs/";

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private int jumlahKupon;

    String cameraPermissions[];
    String storagePermissions[];

    //uri of picked image
    Uri image_uri;

    //for checking profile photo
    String profilePhoto;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        storageReference = getInstance().getReference();

        // Init arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        initFirebase();
        loadDataFirebase();
        loadKuponUser();
        loadPoinUser();

        pd = new ProgressDialog(getContext());

        btnNama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpan.setEnabled(true);
                editNama.setEnabled(true);
                editNama.requestFocus();
                editNama.setFocusableInTouchMode(true);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editNama, InputMethodManager.SHOW_FORCED);
            }
        });

        btnNoHp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpan.setEnabled(true);
                editNoHp.setEnabled(true);
                editNoHp.requestFocus();
                editNoHp.setFocusableInTouchMode(true);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editNoHp, InputMethodManager.SHOW_FORCED);
            }
        });

        pekerjaann.setEnabled(false);
        btnPekerjaan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpan.setEnabled(true);
                pekerjaann.setEnabled(true);

                String[] pekerjaannn = new String[]{
                        "Staf",
                        "Mahasiswa"
                };

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, pekerjaannn) {
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        convertView = super.getDropDownView(position, convertView,
                                parent);

                        convertView.setVisibility(View.VISIBLE);
                        ViewGroup.LayoutParams p = convertView.getLayoutParams();
                        p.height = 100; // set the height
                        convertView.setLayoutParams(p);

                        return convertView;
                    }
                };

                pekerjaann.setAdapter(adapter);
                pekerjaann.performClick();


            }
        });

        btnSandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpan.setEnabled(true);
                editPassword.setEnabled(true);
                editPassword.requestFocus();
                editPassword.setFocusableInTouchMode(true);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editPassword, InputMethodManager.SHOW_FORCED);
            }
        });

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNama.setEnabled(false);
                editNoHp.setEnabled(false);
                pekerjaann.setEnabled(false);
                editPassword.setEnabled(false);

                ClickEditNama("nama");
                ClickEditPekerjaan("pekerjaan");
                ClickEditNoHP("noHP");
                ClickEditPassword("password");

                Fragment fragment = new HomeFragment();
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_content_dashboard, fragment)
                        .commit();
                Toast.makeText(getActivity(), "Profil berhasil di edit", Toast.LENGTH_SHORT).show();
            }
        });

        fotoProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePhoto = "image";
                showImagePictDialog();
            }
        });


        keluar.setOnClickListener(this::onClick);
        return view;
    }

    private void loadKuponUser(){
        jumlahKupon = 0;
        ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
        LiveData<DataSnapshot> liveData = viewModel.getdataUser();
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
        liveData.observe(this, dataSnapshot -> {
            String kupon = dataSnapshot.child(currentUser).child("kupon").getValue().toString();
            try {
                jumlahKupon = Integer.parseInt(kupon);
                myKupon.setText(kupon);
            }
            catch (Exception e){

            }
        });
    }

    private void loadPoinUser(){
        ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
        LiveData<DataSnapshot> liveData = viewModel.getdataUser();
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replaceAll("\\.", "_");
        liveData.observe(this, dataSnapshot -> {
            // Untuk menampilkan dataUser
            String poin = dataSnapshot.child(currentUser).child("poin").getValue().toString();

            myPoin.setText(poin);

        });
    }


    private void initFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void loadDataFirebase() {

        ViewModelFirebase viewModel = ViewModelProviders.of(this).get(ViewModelFirebase.class);
        LiveData<DataSnapshot> liveData = viewModel.getdataUser();
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("\\.", "_");
        liveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                // Untuk menampilkan dataUser
                String namaa = dataSnapshot.child(currentUser).child("nama").getValue().toString();
                String email = dataSnapshot.child(currentUser).child("email").getValue().toString();
                String noHp = dataSnapshot.child(currentUser).child("noHP").getValue().toString();
                String pkrjaan = dataSnapshot.child(currentUser).child("pekerjaan").getValue().toString();
                String jenisKelaminn = dataSnapshot.child(currentUser).child("jenisKelamin").getValue().toString();
                String pw = dataSnapshot.child(currentUser).child("password").getValue().toString();
                String image = dataSnapshot.child(currentUser).child("image").getValue().toString();

                editNama.setText(namaa);
                editEmail.setText(email);
                editNoHp.setText(noHp);
                jenisKelamin.setText(jenisKelaminn);
                editPassword.setText(pw);

                try {
                    Picasso.get().load(image).into(fotoProfil);
                }
                catch (Exception e){
                    Picasso.get().load(R.drawable.icon_upload).into(fotoProfil);
                }

                final List<String> areas = new ArrayList<String>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    areas.add(pkrjaan);


                }

                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                pekerjaann.setAdapter(areasAdapter);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.keluar:
                new AlertDialog.Builder(getActivity())
                        .setTitle(getResources().getString(R.string.ClearCookies))
                        .setMessage(
                                getResources().getString(R.string.ClearCookieQuestion))
                        .setIcon(
                                getResources().getDrawable(
                                        android.R.drawable.ic_dialog_alert))
                        .setPositiveButton(
                                getResources().getString(R.string.PostiveYesButton),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        //Do Something Here
                                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                        firebaseAuth.signOut();
                                        Intent intent = new Intent(getActivity(), LoginRegister.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }
                                })
                        .setNegativeButton(
                                getResources().getText(R.string.NegativeNoButton),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        //Do Something Here
                                    }
                                }).show();

        }

    }

    public void ClickEditNama(String key) {
        String value = editNama.getText().toString().trim();
        if (!TextUtils.isEmpty(value)){
            showDialog();
            HashMap<String, Object> result = new HashMap<>();
            result.put(key, value);

            firebaseDatabase.getReference()
                    .child("penukarSampah")
                    .child(firebaseAuth.getCurrentUser().getEmail().replaceAll("\\.", "_")).updateChildren(result)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideDialog();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
        else {
            Toast.makeText(getActivity(), "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
        }
    }

    public void ClickEditNoHP(String key) {
        String value = editNoHp.getText().toString().trim();
        if (!TextUtils.isEmpty(value)){
            showDialog();
            HashMap<String, Object> result = new HashMap<>();
            result.put(key, value);

            firebaseDatabase.getReference()
                    .child("penukarSampah")
                    .child(firebaseAuth.getCurrentUser().getEmail().replaceAll("\\.", "_")).updateChildren(result)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideDialog();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
        else {
            Toast.makeText(getActivity(), "No HP tidak boleh kosong", Toast.LENGTH_SHORT).show();
        }
    }

    public void ClickEditPekerjaan(String key) {
        String value = pekerjaann.getSelectedItem().toString().trim();
        if (!TextUtils.isEmpty(value)){
           showDialog();
            HashMap<String, Object> result = new HashMap<>();
            result.put(key, value);

            firebaseDatabase.getReference()
                    .child("penukarSampah")
                    .child(firebaseAuth.getCurrentUser().getEmail().replaceAll("\\.", "_")).updateChildren(result)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideDialog();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
        else {
            Toast.makeText(getActivity(), "No HP tidak boleh kosong", Toast.LENGTH_SHORT).show();
        }
    }

    public void ClickEditPassword(String key) {
        final String value = editPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(value)){
            showDialog();
            HashMap<String, Object> result = new HashMap<>();
            result.put(key, value);

            firebaseDatabase.getReference()
                    .child("penukarSampah")
                    .child(firebaseAuth.getCurrentUser().getEmail().replaceAll("\\.", "_")).updateChildren(result)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideDialog();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });

            firebaseAuth.getCurrentUser().updatePassword(value);
        }
        else {
            Toast.makeText(getActivity(), "Kata sandi tidak boleh kosong", Toast.LENGTH_SHORT).show();
        }
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

    private void showImagePictDialog() {
        String options[] = {"Kamera", "Galeri"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pilih foto profil melalui :");
        builder.setItems(options, ((dialog, which) -> {
            if (which == 0) {
                // Camera clicked
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                }
                else {
                    pickFromCamera();
                }
            }
            else if (which == 1) {
                // Gallery clicked
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                }
                else {
                    pickFromGallery();
                }

            }
        }));
        builder.create().show();
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
                image_uri = data.getData();
                uploadProfilePhoto(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                // image is picked from camera, get uri of image
                uploadProfilePhoto(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePhoto(Uri uri) {
        showDialog();
        String filePathAndName = storagePath+ "" + profilePhoto + "_" + firebaseDatabase.getReference()
                .child("penukarSampah")
                .child(firebaseAuth.getCurrentUser().getEmail().replaceAll("\\.", "_"));

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        //check if image is uploaded or not
                        if (uriTask.isSuccessful()) {
                            //image uploaded
                            HashMap<String, Object> results = new HashMap<>();
                            results.put(profilePhoto, downloadUri.toString());

                            firebaseDatabase.getReference()
                                    .child("penukarSampah")
                                    .child(firebaseAuth.getCurrentUser().getEmail().replaceAll("\\.", "_")).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            hideDialog();
                                            Toast.makeText(getActivity(), "Gambar berhasil diperbaharui...", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            hideDialog();
                                            Toast.makeText(getActivity(), "Error diperbaharui...", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else {
                            //error
                            hideDialog();
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideDialog();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void showDialog(){
        pd.setMessage("Sedang Memuat..");
        pd.show();
    }

    private void hideDialog(){
        pd.dismiss();
        pd.hide();
    }

}
