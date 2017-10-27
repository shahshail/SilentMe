package com.corral.firebase.shailshah.silentme.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by shailshah on 10/26/17.
 */

public class SilentContract {

    public static final String AUTHORITY = "com.corral.firebase.shailshah.silentme";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class SilentEntry implements BaseColumns {
        public static final String PATH_PLACES = "places";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACES).build();

        public static final String TABLE_NAME = "places";
        public static final String COLUMN_PLACE_ID = "placeID";

    }
}
