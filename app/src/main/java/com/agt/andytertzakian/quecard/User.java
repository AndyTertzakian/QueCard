package com.agt.andytertzakian.quecard;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Andy Tertzakian on 2016-10-16.
 */
public class User {

    private String uuid;
    private String name;
    private String email;
    private String[] purchases;


    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uuid, String name, String email){
        setId(uuid);
        setName(name);
        setEmail(email);
    }

    public void upload(){
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            users.child(uid).setValue(this);
        }
    }


    public String getId(){
        return uuid;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public void setId(String uuid){
        this.uuid = uuid;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setEmail(String email){
        this.email = email;
    }

}

