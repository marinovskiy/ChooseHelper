package com.geekhub.choosehelper.utils;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;

import org.json.JSONArray;
import org.json.JSONException;

public class AuthorizationUtil {

    public static void setVkUserInfo() {
        VKRequest vkRequest = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS, Prefs.getUserId(), VKApiConst.FIELDS, "photo_200"));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONArray jsonArray = response.json.getJSONArray("response");
                    VKApiUser vkApiUser = new VKApiUser(jsonArray.getJSONObject(0));
                    Prefs.setUserName(String.format("%s %s", vkApiUser.first_name, vkApiUser.last_name));
                    Prefs.setUserAvatarUrl(vkApiUser.photo_200);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

