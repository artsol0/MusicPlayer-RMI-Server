package com.artsolo.musicplayer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsonManager {

    private final Gson gson = new Gson();

    public String[] getLoginData(String data) {
        String[] loginData = new String[2];
        JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
        loginData[0] = jsonObject.get("username").getAsString();
        loginData[1] = jsonObject.get("password").getAsString();
        return loginData;
    }

    public String[] getRegistrationData(String data) {
        String[] registrationData = new String[3];
        JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
        registrationData[0] = jsonObject.get("username").getAsString();
        registrationData[1] = jsonObject.get("email").getAsString();
        registrationData[2] = jsonObject.get("password").getAsString();
        return registrationData;
    }



}
