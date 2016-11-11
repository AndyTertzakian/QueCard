package com.agt.andytertzakian.quecard;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by andytertzakian on 2016-11-10.
 */

public class Purchase {

    private String purchaseId;
    private String storeId;
    private String clientId;
    private Date date;
    private double totalPrice;
    private ArrayList<Item> items;

    public void upload() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference purchases = database.getReference("Purchases");

        String purchaseId = purchases.push().getKey();
        setPurchaseId(purchaseId);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setClientId(userId);

        purchases.child(purchaseId).setValue(this);

        DatabaseReference users = database.getReference("Users");
        users.child(userId).child("purchases").child(purchaseId).setValue(true);
    }

    public void setClientId(String userId) {
        this.clientId = userId;
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference users = database.getReference("Users");
//        users.child(userId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
    }


    public Date getDate() {
        return date;
    }


    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
