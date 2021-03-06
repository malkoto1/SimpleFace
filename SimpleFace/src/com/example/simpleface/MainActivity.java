package com.example.simpleface;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.facebook.android.*;
import com.facebook.android.Facebook.*;

public class MainActivity extends Activity {

    Facebook facebook = new Facebook("336481539761026");
    private SharedPreferences mPrefs;
    String response = new String();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView friendsListView = (ListView) findViewById(R.id.friendsListView);
        setContentView(R.layout.activity_main);
        /*
         * Get existing access_token if any
         */
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        Bundle parameters = new Bundle();
        
        if(access_token != null) {
            facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
            facebook.setAccessExpires(expires);
        }
        
        /*
         * Only call authorize if the access_token has expired.
         */
        if(true) {

            facebook.authorize(this, new String[] {}, new DialogListener() {

                public void onComplete(Bundle values) {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString("access_token", facebook.getAccessToken());
                    editor.putLong("access_expires", facebook.getAccessExpires());
                    editor.commit();
                }
    
                public void onFacebookError(FacebookError error) {}
    
                public void onError(DialogError e) {}
    
                public void onCancel() {}
            });
        }
        
        parameters.putString("method", "friends.get");
        
        try {
			 response = facebook.request(parameters);
			 //System.out.println(response);
			 JSONArray friendsArray = new JSONArray(response);
			 
			 String[] values = new String[friendsArray.length()];
			 
			 for (int i = 0; i < friendsArray.length(); i++) {
				parameters.clear();
				//String[] stringArray = {"users.getInfo", friendsArray.getString(i)};
				parameters.putString("method", "users.getStandardInfo");
				response = facebook.request(parameters);
				System.out.println(response);
				JSONObject friendObject = new JSONObject(response);
				values[i] = friendObject.getString("name");
			 }
			 
			 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, android.R.id.text1, values);
			 
			 friendsListView.setAdapter(adapter);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }
    
    public void onResume() {    
        super.onResume();
        facebook.extendAccessTokenIfNeeded(this, null);
    }
    
    public void logOut(View view) throws MalformedURLException, IOException{
    	facebook.logout(getBaseContext());
    	//MainActivity.class.;
    }
}