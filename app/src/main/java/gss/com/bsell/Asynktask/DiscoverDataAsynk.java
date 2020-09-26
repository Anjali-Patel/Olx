package gss.com.bsell.Asynktask;

import android.os.AsyncTask;
import org.json.JSONObject;
import gss.com.bsell.webrequest.RestJsonClient;

public class DiscoverDataAsynk extends AsyncTask<String, String, JSONObject> {
        private JSONObject jsonResponse;

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                jsonResponse = RestJsonClient.connect(params[0]);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }
}
