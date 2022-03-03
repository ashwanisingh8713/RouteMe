package com.route.apis;

import android.util.Base64;
import android.util.Log;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ApiPathUtil {

    public static final String DATABASE_NAME = "RouteMeData";
    public static final String COLLECTION_NAME = "RoutesData";
    public static final String XMS_VERSION = "2016-07-11";

    private static final int COSMOS_PORT_NUM = 443;
    private static final String COSMOS_DB_URL = "https://routeme.documents.azure.com";
    private static final String PRIMARY_KEY = "zGZVcvEnFSvNKa61Yr3UeofdlVhYhQlMIIGHkWaMJZnghohRYcv5l1h18JnKS8VixLrKaVcxPLbMxn0xvZuVkQ==";

    public static final String CONNECTION_HOST = COSMOS_DB_URL + ":" + COSMOS_PORT_NUM;  // https://routeme.documents.azure.com:443/

    static String TAG = "GenAuth";

    public static String headerDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        //According to the spec the format matters here.  Make sure to use this format on the header dates.
        return formatter.format(new Date()).toLowerCase();
    }

    public static String genAuth(String method, String UTCstring, String url) {
        String mastKey = PRIMARY_KEY;

        //String url = "https://routeme.documents.azure.com:443/dbs";
        Log.i(TAG, "request Url = " +url);
        String strippedurl = url.replace("^https?://[^/]+/","/");
        strippedurl = "/dbs";
        Log.i(TAG, "stripped Url = " +strippedurl);

        // push the parts down into an array so we can determine if the call is on a specific item
        // or if it is on a resource (odd would mean a resource, even would mean an item)
        String[] strippedparts = strippedurl.split("/");
        int truestrippedcount = (strippedparts.length - 1);
        Log.i(TAG, "truestrippedcount :: " +truestrippedcount);

        // define resourceId/Type now so we can assign based on the amount of levels
        String resourceId = "";
        String resType = "";

        // its odd (resource request)
        if (truestrippedcount % 2 != 0) {
            Log.i(TAG,"odd");
            // assign resource type to the last part we found.
            resType = strippedparts[truestrippedcount];
            Log.i(TAG,"resType :: "+resType);

            if (truestrippedcount > 1) {
                // now pull out the resource id by searching for the last slash and substringing to it.
                int lastPart = strippedurl.lastIndexOf("/");
                resourceId = strippedurl.substring(1,lastPart);
                Log.i(TAG,"resourceId :: "+resourceId);
            }
        }
        else { // its even (item request on resource)
            Log.i(TAG,"even");
            // assign resource type to the part before the last we found (last is resource id)
            resType = strippedparts[truestrippedcount - 1];
            Log.i(TAG,"resType :: "+resType);
            // finally remove the leading slash which we used to find the resource if it was
            // only one level deep.
            strippedurl = strippedurl.substring(1);
            Log.i(TAG,"strippedurl :: "+strippedurl);
            // assign our resourceId
            resourceId = strippedurl;
            Log.i(TAG,"resourceId :: "+resourceId);
        }

        // assign our verb
        String verb = method.toLowerCase();
        // assign our RFC 1123 date
        String date = UTCstring.toLowerCase();

        // build up the request text for the signature so can sign it along with the key
        String text = verb.toLowerCase() + "\n" +
                resType.toLowerCase() + "\n" +
                resourceId + "\n" +
                date.toLowerCase() + "\n" +
                "" + "\n";
        Log.i(TAG,"text :: "+text);

        //Decode the master key, and setup the MAC object for signing.
        byte[] masterKeyBytes = Base64.decode(mastKey, Base64.NO_WRAP);
        try {
            Mac mac = Mac.getInstance("HMACSHA256");
            mac.init(new SecretKeySpec(masterKeyBytes, "HMACSHA256"));

            //Sign and encode the auth string.
            //String signature = Base64.encodeToString(mac.doFinal(text.toLowerCase().getBytes("UTF8")), Base64.NO_WRAP);
            byte[] signature1 = mac.doFinal(text.toLowerCase().getBytes("UTF8"));
            String base64Bits = Base64.encodeToString(signature1, Base64.NO_WRAP);
            Log.i(TAG, "base64bits = " + base64Bits);

            // format our authentication token and URI encode it.
            String MasterToken = "master";
            String TokenVersion = "1.0";
            String auth = URLEncoder.encode("type=" + MasterToken + "&ver=" + TokenVersion + "&sig=" + base64Bits, "UTF8");
            Log.i(TAG, "auth = " + auth);

            return auth;

        } catch (Exception e) {
            Log.i(TAG,"error masterKeyBytes :: "+e.getMessage());
        }
        return "";
    }

}
