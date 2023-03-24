package com.example.myhosteldemo;

import static com.example.myhosteldemo.Utility.GlobalData.changeColorOfStatusBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.myhosteldemo.Adapter.Main_viewpager_adapter;
import com.example.myhosteldemo.Utility.AlertUtil;
import com.example.myhosteldemo.Utility.GlobalData;
import com.example.myhosteldemo.model.MainActivity_ImageModel;
import com.example.myhosteldemo.model.Main_Viewpager_Model;
import com.example.myhosteldemo.model.User;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //Firebase
    FirebaseAuth mAuth ;
    FirebaseUser fuser ;
    FirebaseDatabase database ;
    FirebaseStorage storage ;
    FirebaseMessaging messaging ;
    FirebaseFirestore firestore ;

    //SharedPreferences
    SharedPreferences signinpref ;
    SharedPreferences.Editor editor ;

    Toolbar toolbar ;
    DrawerLayout drawerLayout ;
    NavigationView navigationView ;
    CircleImageView header_pic ;
    TextView header_email , header_name ;
    View header ;

    AlertDialog.Builder alertDialog  ;

    //auto sliding image view
    static ViewPager2 viewPager2 ;
    WormDotsIndicator indicator ;
    FloatingActionButton fab ;
    static ArrayList<Main_Viewpager_Model> viewmodel ;
    Main_viewpager_adapter view_adapter ;
    AppBarLayout appBar ;
    TextView hi ;
    public static Handler pageHandeler = new Handler() ;
    static boolean forward = true ;

    //sharedPreferences
    SharedPreferences settingpref ;
    SharedPreferences.Editor set_editor ;

    LinearLayout messLayout , complaintLayout , resourceLayout , meritLayout ;

    static ProgressDialog uploadDialog ;
    View root ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_demo);

        changeColorOfStatusBar(MainActivity.this , R.color.blue) ;

        signinpref = getSharedPreferences("signin" , MODE_PRIVATE) ;
        editor = signinpref.edit() ;
        settingpref = getSharedPreferences("settings_pref" , MODE_PRIVATE) ;
        set_editor = settingpref.edit() ;

        mAuth = FirebaseAuth.getInstance() ;
        fuser = mAuth.getCurrentUser() ;
        database = FirebaseDatabase.getInstance() ;
        storage = FirebaseStorage.getInstance() ;
        messaging = FirebaseMessaging.getInstance() ;
        firestore = FirebaseFirestore.getInstance() ;

        uploadDialog = new ProgressDialog(MainActivity.this) ;
        viewmodel = new ArrayList<>() ;
        view_adapter = new Main_viewpager_adapter(this , viewmodel) ;
        root = LayoutInflater.from(this).inflate(R.layout.main_viewpager , null)  ;

        if(fuser == null){
            startActivity(new Intent(MainActivity.this , SignIn.class));
            finish();
        }

        editor.putBoolean(fuser.getUid() , true) ;
        editor.apply();

        performSettingPref() ;

        alertDialog = new AlertDialog.Builder(this) ;

        try{
            changeContentView();
        }
        catch (Exception e){
            alertDialog.setTitle("Loading Result") ;
            alertDialog.setMessage("Failed to load your data") ;
            alertDialog.setCancelable(false) ;
            alertDialog.setIcon(R.drawable.error) ;
            alertDialog.setPositiveButton("Retry" , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    changeContentView();
                }
            }) ;
            alertDialog.show() ;
        }

    }

    @Override
    protected void onResume() {
        if(GlobalData.user != null){
            preLoadHeader();
        }

        pageHandeler.postDelayed(runnable , 2000) ;
        super.onResume();
    }

    @Override
    protected void onPause() {
        pageHandeler.removeCallbacks(runnable);
        new Handler().postDelayed(this::closedrawer, 1000) ;

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            closedrawer();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode != RESULT_OK)
            return;
        if(data == null)
            return ;

        if(requestCode == 100){
            Uri uri = data.getData() ;
            uploadImage(uri) ;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void changeContentView(){
        if(GlobalData.user != null){
            initialiseAll();
            return ;
        }

        database.getReference().child("Users").child(fuser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class) ;
                            GlobalData.user = user ;
                            initialiseAll() ;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        alertDialog.setTitle("Loading Result") ;
                        alertDialog.setMessage("Failed to load your data") ;
                        alertDialog.setCancelable(false) ;
                        alertDialog.setIcon(R.drawable.error) ;
                        alertDialog.setPositiveButton("Retry" , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                changeContentView();
                            }
                        }) ;
                        alertDialog.show() ;
                    }
                });
    }

    @SuppressLint("MissingInflatedId")
    private void initialiseAll(){
        setContentView(R.layout.activity_main);
        changeColorOfStatusBar(MainActivity.this , R.color.cyandark) ;
        toolbar = findViewById(R.id.main_toolbar) ;
        drawerLayout = findViewById(R.id.drawer) ;
        navigationView = findViewById(R.id.nav) ;
        header = navigationView.getHeaderView(0) ;
        header_pic = header.findViewById(R.id.head_prof) ;
        header_email = header.findViewById(R.id.head_email) ;
        header_name = header.findViewById(R.id.head_name) ;
        viewPager2 = findViewById(R.id.main_viewpager) ;
        indicator = findViewById(R.id.main_view_indicator) ;
        fab = findViewById(R.id.main_view_add) ;
        appBar = findViewById(R.id.main_appbar) ;
        hi = findViewById(R.id.main_toolbar_hi) ;
        messLayout = findViewById(R.id.main_messlayout) ;
        complaintLayout = findViewById(R.id.main_complaintlayout) ;
        resourceLayout = findViewById(R.id.main_resourceslayout) ;
        meritLayout = findViewById(R.id.main_meritlayout) ;

        setUpLayoutListener() ;

        setUpNavigationDrawerMenu() ;

        navigationView.setNavigationItemSelectedListener(MainActivity.this);

        preLoadHeader() ;

        loadViewpager() ;

        appBar.addOnOffsetChangedListener((a,b) -> offsetChanged(a,b));
    }

    private void setUpNavigationDrawerMenu(){
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(MainActivity.this
                        , drawerLayout
                        , toolbar
                        , R.string.draweropen
                        , R.string.drawerclosed) ;

        drawerToggle.getDrawerArrowDrawable().setColor(this.getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //closedrawer() ;
        Intent intent ;

        switch (item.getItemId()){
            case R.id.nav_profile:
                intent = new Intent(MainActivity.this , SetupAccount.class) ;
                intent.putExtra("MainActivity" , true) ;
                startActivity(intent);
                break;
            case R.id.nav_update_password:
                intent = new Intent(MainActivity.this , ForgotPassword.class) ;
                startActivity(intent);
                break;
            case R.id.nav_logout:
                logout() ;
                break;
            case R.id.nav_settings:
                intent = new Intent(MainActivity.this , Settings.class) ;
                startActivity(intent);
                break;
            case R.id.nav_complaints:
                intent = new Intent(MainActivity.this , Complaints.class) ;
                startActivity(intent);
                break;
            case R.id.nav_resources:
                intent = new Intent(MainActivity.this , Resources.class) ;
                startActivity(intent);
                break;
            case R.id.nav_mess:
                intent = new Intent(MainActivity.this , Mess_Main.class) ;
                startActivity(intent);
                break;
            case R.id.nav_reportbug:
                intent = new Intent(MainActivity.this , Report_Bug.class) ;
                startActivity(intent);
                break;
            case R.id.nav_feedback:
                intent = new Intent(MainActivity.this , FeedBack.class) ;
                startActivity(intent);
                break;
            case R.id.nav_admin:
                intent = new Intent(MainActivity.this , AdminPanel.class) ;
                startActivity(intent);
                break;
            case R.id.nav_merit:
                intent = new Intent(MainActivity.this , Merit_Select.class) ;
                startActivity(intent);
                break;
            default:
                closedrawer();
        }

        return false;
    }

    private void showDrawer(){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void closedrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void preLoadHeader(){
        if(header_pic == null){
           //Toast.makeText(this, "Header pic is null", Toast.LENGTH_SHORT).show();
        }

        if(GlobalData.user != null){
            header_email.setText(GlobalData.user.getEmail());
            header_name.setText(GlobalData.user.getUsername());
        }
        else{
            setUserFromDatabase() ;
        }

        if(GlobalData.profile != null){
            header_pic.setImageDrawable(GlobalData.profile);
        }
        if(GlobalData.profile_uri != null){
            header_pic.setImageURI(GlobalData.profile_uri);
        }
        else{
            getProfileToImage(GlobalData.user.getProfile());
        }

    }

    private void getProfileToImage(String url){
        if(url != null && !url.equals("")){
            Glide.with(MainActivity.this)
                    .load(url)
                    .placeholder(R.drawable.profile_pic3)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //Toast.makeText(MainActivity.this, "Failed to load Profile Picture\n" + e, Toast.LENGTH_LONG).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            GlobalData.profile = resource ;
                            return false;
                        }
                    })
                    .into(header_pic);
        }
        else{
            loadImageFromAuth();
        }

    }

    private void loadImageFromAuth(){
        Glide.with(MainActivity.this)
                .load(fuser.getPhotoUrl())
                .placeholder(R.drawable.profile_pic3)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(MainActivity.this, "No Profile picture set\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        GlobalData.profile = resource ;
                        return false;
                    }
                })
                .into(header_pic) ;
    }

    private void setUserFromDatabase(){
        database.getReference().child("Users").child(fuser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class) ;
                            GlobalData.user = user ;
                            header_email.setText(user.getEmail());
                            header_name.setText(user.getUsername());
                            getProfileToImage(user.getProfile());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void logout(){
                ProgressDialog dialog = new ProgressDialog(MainActivity.this) ;
                dialog.setCancelable(false);
                dialog.setTitle("Logout");
                dialog.setTitle("Please wait....");
                dialog.setIcon(R.drawable.information);

                dialog.show() ;

                mAuth.signOut() ;
                GlobalData.user = null ;
                GlobalData.fuser = null ;
                GlobalData.profile = null ;
                GlobalData.profile_uri = null ;

                startActivity(new Intent(MainActivity.this , SignIn.class));
                Toast.makeText(MainActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
    }


    private  void loadViewpager(){
        firestore.collection("Images")
                .orderBy("uploadTime" , Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            viewmodel.set(0 , new Main_Viewpager_Model(root.findViewById(R.id.main_view_image) , null , R.drawable.college_image)) ;
                            viewPager2.setAdapter(view_adapter);
                            view_adapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Unable to load Sliding Images", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            updateViewpager(value) ;
                        }
                    }
                }) ;
    }

    public static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(viewPager2 != null){
                if(forward){
                    viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
                    if(viewPager2.getCurrentItem() >= viewmodel.size()-1){
                        forward = false ;
                    }
                }
                else{
                    viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1);
                    if(viewPager2.getCurrentItem() <= 0){
                        forward = true ;
                    }
                }
            }
        }
    } ;


    private void offsetChanged(AppBarLayout appBarLayout , int verticalOffset){
        if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
        {
            //  Collapsed
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.white));
            hi.setText("Welcome , " + GlobalData.user.getUsername());
            hi.setVisibility(View.VISIBLE);
            viewPager2.setVisibility(View.INVISIBLE);
            indicator.setVisibility(View.INVISIBLE);
        }
        else
        {
            //Expanded
            hi.setVisibility(View.GONE);
            viewPager2.setVisibility(View.VISIBLE);
            indicator.setVisibility(View.VISIBLE);
        }
    }

    private void performSettingPref(){
        if(settingpref.getBoolean("Done" , false)){
            //well done
        }
        else{
            messaging.subscribeToTopic("Mess_Menu") ;
            messaging.subscribeToTopic("College_Events") ;
            set_editor.putBoolean("Mess_Menu" , true) ;
            set_editor.putBoolean("College_Events" , true) ;
            set_editor.putBoolean("Done" , true) ;
            set_editor.apply();
        }
    }

    private void setUpLayoutListener(){
        messLayout.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this , Mess_Main.class));
        });

        complaintLayout.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this , Complaints.class));
        });

        resourceLayout.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this , Resources.class));
        });

        meritLayout.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this , Merit_Select.class));
        });

        fab.setOnClickListener(v -> {
            if(GlobalData.isAdmin){
                AlertDialog.Builder al = new AlertDialog.Builder(this) ;
                al.setTitle("Select from Options") ;
                al.setMessage("What you like to do ?") ;
                al.setPositiveButton("Add Image", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ImagePicker.Companion
                                .with(MainActivity.this)
                                .crop()
                                .maxResultSize(1080,1080)
                                .compress(1024)
                                .start(100);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }) ;

                al.show() ;
            }
            else{
                Toast.makeText(this, "Please login as admin first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage(Uri uri){
        long time = System.currentTimeMillis() ;

        uploadDialog.setTitle("Uploading....");
        uploadDialog.setMessage("Please wait");
        uploadDialog.setCancelable(true);
        uploadDialog.show();

        storage.getReference().child("Images")
                .child(String.valueOf(time))
                .putFile(uri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            task.getResult().getStorage().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            uploadToFirestore(uri.toString() , time) ;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            AlertUtil.showAlertDialog(MainActivity.this,
                                                    "Failure",
                                                    "Failed to upload Image",
                                                    true,
                                                    R.drawable.error,
                                                    "Ok");
                                            uploadDialog.dismiss();
                                        }
                                    }) ;
                        }
                        else{
                            AlertUtil.showAlertDialog(MainActivity.this,
                                    "Failure",
                                    "Failed to upload Image",
                                    true,
                                    R.drawable.error,
                                    "Ok");
                            uploadDialog.dismiss();
                        }
                    }
                }) ;
    }

    private void uploadToFirestore(String url , long time){
        MainActivity_ImageModel model = new MainActivity_ImageModel(url , time) ;

        firestore.collection("Images")
                .document(String.valueOf(time))
                .set(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        loadViewpager();
                        AlertUtil.showAlertDialog(MainActivity.this,
                                "Success",
                                "Image uploaded successfully",
                                true,
                                R.drawable.information,
                                "Ok");
                        uploadDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertUtil.showAlertDialog(MainActivity.this,
                                "Failure",
                                "Failed to upload Image",
                                true,
                                R.drawable.error,
                                "Ok");
                        uploadDialog.dismiss();
                    }
                }) ;
    }

    private void updateViewpager(QuerySnapshot value){
        viewmodel.clear();
        viewmodel.add(new Main_Viewpager_Model(root.findViewById(R.id.main_view_image) , null , R.drawable.college_image)) ;

        for(DocumentSnapshot data : value.getDocuments()){
            MainActivity_ImageModel model = data.toObject(MainActivity_ImageModel.class) ;
            viewmodel.add(new Main_Viewpager_Model(root.findViewById(R.id.main_view_image) , model.getUrl() , 0)) ;
        }

        viewPager2.setAdapter(view_adapter);
        indicator.attachTo(viewPager2);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setClipChildren(false);
        viewPager2.setClipToPadding(false);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer() ;
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position) ;
                page.setScaleY(0.85f + r * 0.14f);
            }
        });
        viewPager2.setPageTransformer(transformer);
        view_adapter.notifyDataSetChanged();



        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                pageHandeler.removeCallbacks(runnable);
                pageHandeler.postDelayed(runnable , 2000) ;
            }
        });

    }


}