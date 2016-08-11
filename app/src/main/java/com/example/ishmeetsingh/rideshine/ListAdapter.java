package com.example.ishmeetsingh.rideshine;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.ishmeetsingh.rideshine.AppConfig.GOOGLE_BROWSER_API_KEY;
import static com.example.ishmeetsingh.rideshine.AppConfig.OK;
import static com.example.ishmeetsingh.rideshine.AppConfig.STATUS;
import static com.example.ishmeetsingh.rideshine.AppConfig.TAG;
import static com.example.ishmeetsingh.rideshine.R.*;

/**
 * Created by ishmeet.singh on 10-08-2016.
 */
class ListAdapter extends ArrayAdapter<ResultOfQuery> {

    public String Phoneno;
    private ArrayList<ResultOfQuery> res;

    public ListAdapter(Context context, ArrayList<ResultOfQuery> res) {
        super(context, R.layout.list_element, res);
        this.res = res;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layout.list_element, parent, false);
        }
        ResultOfQuery data = res.get(position);
        String name = data.resName;
        String ID = data.resPlaceId;
        String Address = data.getResVicinity();
        TextView NAME = (TextView) convertView.findViewById(id.nameOfPlace);
        TextView Vicinity = (TextView) convertView.findViewById(id.viscinityOfPlace);
        TextView PhoneNo = (TextView) convertView.findViewById(id.phoneNoOfPlace);
        NAME.setText(name);
        Vicinity.setText(Address);
        StringBuilder goggleDetailsCall = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        goggleDetailsCall.append("placeid=").append(ID).append("&key=" + GOOGLE_BROWSER_API_KEY);
        JsonObjectRequest req = new JsonObjectRequest(goggleDetailsCall.toString(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {

                        Log.i(TAG, "onResponse: Result= " + result.toString());
                        try {
                            locationDetail(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });
        AppController.getInstance().addToRequestQueue(req);
        return convertView;
    }

    private void locationDetail(JSONObject result) throws JSONException {
        if (result.getString(STATUS).equalsIgnoreCase(OK)) {
            JSONObject detail = result.getJSONObject("detail");
            String phoneno = detail.getString("international_phone_number");


        }
    }

    public void publishNewList(ArrayList<ResultOfQuery> res) {
        this.res.addAll(res);
        notifyDataSetChanged();
    }


}