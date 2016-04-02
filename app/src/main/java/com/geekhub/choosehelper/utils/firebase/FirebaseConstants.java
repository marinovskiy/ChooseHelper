package com.geekhub.choosehelper.utils.firebase;

public final class FirebaseConstants {

    // base firebase reference
    public static final String FB_REFERENCE_MAIN = "https://choosehelper.firebaseio.com/";

    // users' firebase references
    public static final String FB_REFERENCE_USERS = "users";
    public static final String FB_REFERENCE_USER_EMAIL = "email";
    public static final String FB_REFERENCE_USER_FULL_NAME = "fullName";
    public static final String FB_REFERENCE_USER_PHOTO_URL = "photoUrl";
    public static final String FB_REFERENCE_USER_BIRTHDAY = "birthday";
    public static final String FB_REFERENCE_USER_PLACE_LIVE = "placeLive";
    public static final String FB_REFERENCE_USER_ABOUT = "about";


    public static final String FB_REFERENCE_VARIANTS = "variants";

    // compares' firebase references
    public static final String FB_REFERENCE_COMPARES = "compares";
    public static final String FB_REFERENCE_COMPARES_DATE = "date";

    // comments' firebase references
    public static final String FB_REFERENCE_COMMENTS = "comments";
    public static final String FB_REFERENCE_COMMENTS_CID = "compareId";

    // likes' firebase references
    public static final String FB_REFERENCE_LIKES = "likes";
    public static final String FB_REFERENCE_LIKES_CID = "compareId";

}
