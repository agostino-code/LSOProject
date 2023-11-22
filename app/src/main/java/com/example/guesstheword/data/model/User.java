package com.example.guesstheword.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class User implements JSONData{

    public static final int AVATAR_NULL = 0;
    public static final int AVATAR_1 = 1;
    public static final int AVATAR_2 = 2;
    public static final int AVATAR_3 = 3;
    public static final int AVATAR_4 = 4;
    public static final int AVATAR_5 = 5;
    public static final int AVATAR_6 = 6;
    public static final int AVATAR_7 = 7;
    public static final int AVATAR_8 = 8;
    public static final int AVATAR_9 = 9;
    public static final int AVATAR_10 = 10;
    public static final int AVATAR_11 = 11;
    public static final int AVATAR_12 = 12;
    public static final int AVATAR_13 = 13;
    public static final int AVATAR_14 = 14;
    public static final int AVATAR_15 = 15;
    public static final int AVATAR_16 = 16;
    public static final int NUMBER_OF_AVATARS = 16;
    @Nullable
    private final String email;
    @Nullable
    private String password;
    @Nullable
    private String username;
    private int avatar;

    /**
     * Constructor called to create the profile of the main User.
     *
     * @param email    must respect the RFC 5322 email format (unique attribute)
     * @param password at least 5 characters
     * @param username (this will be used as the User ID, as Primary key)
     * @param avatar   chosen between 16 images (must be a number between 1-16, you can use the MACROS of this class)
     */
    public User(@NonNull String email, @NonNull String password, @NonNull String username, int avatar) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.avatar = avatar;
    }

    /**
     * Constructor called to create an incomplete profile of the other Users of a room
     *
     * @param username (this will be used as the User ID, as Primary key)
     * @param avatar chosen between 16 images (must be a number between 1-16, you can use the MACROS of this class)
     */
    public User(@NonNull String username, int avatar) {
        this.username = username;
        this.avatar = avatar;
        email = null;
        password = null;
    }

    /**
     * Constructor called for the sign in, used for a temporary istance of this class
     * @param email    must respect the RFC 5322 email format (unique attribute)
     * @param password at least 5 characters
     */
    public User(@NonNull String email, @NonNull String password) {
        this.email = email;
        this.password = password;
        username = null;
        avatar = AVATAR_NULL;
    }

    /**
     * Constructor called to create a User from a JSON string
     *
     * @param jsonUser a JSON string representing a User
     */
    public User(String jsonUser) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonUser);
        this.email = jsonObject.getString("email");
        this.password = jsonObject.getString("password");
        this.username = jsonObject.getString("username");
        this.avatar = jsonObject.getInt("avatar");
    }

    /**
     * Copy constructor
     * @param user
     */
    public User(User user) {
        this.email = user.email;
        this.password = user.password;
        this.username = user.username;
        this.avatar = user.avatar;
    }

    /*
     * Getters
     */
    @Nullable
    public String getEmail() {
        return email;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    /**
     * @return avatar chosen between 16 images (a number between 1-16)
     */
    public int getAvatar() {
        return avatar;
    }

    /*
     * Setters
     */
    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    /**
     * @param avatar chosen between 16 images (must be a number between 1-16, you can use the MACROS of this class)
     */
    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    /**
     * @return a JSON string representing the User
     */
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonUser = new JSONObject();

        if (email != null) {
            jsonUser.put("email", email);
        } else {
            jsonUser.put("email", JSONObject.NULL);
        }
        if (password != null) {
            jsonUser.put("password", password);
        } else {
            jsonUser.put("password", JSONObject.NULL);
        }
        if (username != null) {
            jsonUser.put("username", this.username);
        } else {
            jsonUser.put("username", JSONObject.NULL);
        }
        if(avatar != AVATAR_NULL) {
            jsonUser.put("avatar", this.avatar);
        } else {
            jsonUser.put("avatar", JSONObject.NULL);
        }

        return jsonUser;
    }

    @Override
    public String toJson() throws JSONException {
        return this.toJSONObject().toString();
    }
}
