package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Registration extends AppCompatActivity {
    private Spinner spinner;
    private String name,phone,age,safety="Safe",gender="";
    private EditText n,a;
    private TextView t;
    private RadioGroup radioGroup;
    ArrayAdapter<String> myAdapter;
    private DatabaseReference rootref;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private ProgressBar progressBar;

    private int b=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        Intent i = getIntent();
        phone = i.getStringExtra("mkey");
        t=(TextView)findViewById(R.id.textView8);
        t.setText(phone);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        spinner = (Spinner) findViewById(R.id.Gender);
        n=(EditText)findViewById(R.id.editTextTextPersonName);
        a=(EditText)findViewById(R.id.editTextTextPersonAge);
        myAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.typeofgender));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);
        radioGroup = findViewById(R.id.radio_grou);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1){
                    gender="Male";
                }
                else if(position==2){
                    gender="Female";
                }
                else if(position==3){
                    gender="Others";
                }
                else{
                    gender="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(Registration.this,"Please select any one",Toast.LENGTH_LONG).show();
            }
        });
    }


    public void checkbox(View view)
    {
        int radioId = radioGroup.getCheckedRadioButtonId();

        if(radioId==R.id.radio_ye){
            safety="Safe";
        }
        else{
            safety="Unsafe";
        }

    }

    public void collectdata(){
        name = n.getText().toString();
        age = a.getText().toString();

    }

    public boolean check(){
        boolean b=true;
        if (TextUtils.isEmpty(name)) {
            n.setError("Required");
            b=false;
        }
        if (TextUtils.isEmpty(age)) {
            a.setError("Required");
            b=false;
        }
        if (TextUtils.isEmpty(gender)) {
            final TextView errorText = (TextView) spinner.getSelectedView();
            errorText.setError("Required");
            b=false;
        }
        return b;
    }

    public void retur(View view) {
        String str = "";
        Intent  i = new Intent(Registration.this,Main2Activity.class);
        i.putExtra("mkey",str);
        startActivity(i);
        finish();
    }

    public void register(final View view) {
        progressBar.setVisibility(View.VISIBLE);
        view.setEnabled(false);
        collectdata();
        if (check()) {
            rootref=FirebaseDatabase.getInstance().getReference();
            HashMap<String, Object> userdataMap=new HashMap<>();
            userdataMap.put("Name", name);
            userdataMap.put("uid", currentUserId);
            userdataMap.put("Age", age);
            userdataMap.put("Safety", safety);
            userdataMap.put("Gender", gender);
            userdataMap.put("image", "");
            rootref.child("users").child(phone).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Registration.this,"Registered",Toast.LENGTH_LONG).show();
                        b=0;
                        startActivity(new Intent(Registration.this,Mainpage.class));
                        finish();
                    }
                    else{
                        view.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(Registration.this,"Error: Please Retry",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(b==1){
        mAuth.getCurrentUser().delete();
        mAuth.signOut();}
    }

}