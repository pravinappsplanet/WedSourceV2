package in.appsplanet.wedsource.utils;

import android.content.ContentValues;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class IOUtils {

    /**
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getQuery(ContentValues params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, Object> entry : params.valueSet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }

        Log.d("URL", "PARAM" + result.toString());

        return result.toString().replace(" ", "%20");
    }
}
