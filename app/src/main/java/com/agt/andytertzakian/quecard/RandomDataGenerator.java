package com.agt.andytertzakian.quecard;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;


/**
 * Created by andytertzakian on 2016-11-10.
 */

public class RandomDataGenerator {

    private static SecureRandom sRandom = new SecureRandom();
    private static int delay = 5*1000; //5 seconds

    //private constructor for private class
    private RandomDataGenerator(){

    }

    public static void generateRandomUsers() throws InterruptedException {
        while(true){
            User user = generateRandomUser();
            fakeUpload(user);
            Thread.sleep(delay);
        }
    }

    /*
      This method returns a randomly generated user.
      The created user will just be a value to associate
      purchases with for testing purposes. They will be added
      a list called "Fake Users" so that they can all be deleted
      once testing meets all required standards.
     */
    private static User generateRandomUser(){
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail(UUID.randomUUID().toString());
        user.setName(UUID.randomUUID().toString());
        return user;
    }

    private static void fakeUpload(User user){
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference fakeUsers = FirebaseDatabase.getInstance().getReference("fakeUsers");
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            users.child(user.getId()).setValue(user);
            fakeUsers.child(user.getId()).setValue(user);
        }
    }

}
