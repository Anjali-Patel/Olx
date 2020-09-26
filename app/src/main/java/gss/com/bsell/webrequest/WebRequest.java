package gss.com.bsell.webrequest;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import gss.com.bsell.listener.AsyncListener;

public class WebRequest {
    public static final int MY_SOCKET_TIMEOUT_MS = 50000;
    public static void serverCall(final Context context, JSONObject jsonObject, final AsyncListener asc, String APIName){
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIName, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    Log.d("JSON", response.toString());
                asc.onTaskCompleted(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", "Error: " + error.getMessage());
                Toast.makeText(context,"Server Not Responding", Toast.LENGTH_SHORT).show();
                asc.onTaskCompleted(error.getMessage());
                }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


}
