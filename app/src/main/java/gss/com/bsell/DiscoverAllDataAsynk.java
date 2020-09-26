package gss.com.bsell;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gss.com.bsell.webrequest.RestJsonClient;

public class DiscoverAllDataAsynk extends AsyncTask<String, String, JSONObject>{
    private JSONObject jsonResponse;


    @Override
    protected JSONObject doInBackground(String ... params) {
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            if (params[1] != null)
                nameValuePairs.add(new BasicNameValuePair("product_id", params[1]));
               // nameValuePairs.add(new BasicNameValuePair("userdemand", params[2]));

            jsonResponse = RestJsonClient.post(params[0], nameValuePairs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }


}
