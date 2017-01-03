package com.corazza.fosco.lumenGame.helpers.restTasks;

import android.os.AsyncTask;
import android.provider.SyncStateContract;

import com.corazza.fosco.lumenGame.helpers.APIHelper;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Simone Chelo on 29/10/2016.
 */

public class SendMailTask extends AsyncTask<String, Void, JSONObject>
{
    Exception mException = null;

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        this.mException = null;
    }

    @Override
    protected JSONObject doInBackground(String... params)
    {


        HttpURLConnection urlConnection;
        URL url;
        JSONObject object = null;

        try
        {
            String sender = URLEncoder.encode(params[0], "UTF-8");
            String subj = "subj=" + URLEncoder.encode(params[1], "UTF-8");

            byte[] postData       = subj.getBytes("UTF-8");
            int    postDataLength = postData.length;

            url = new URL(APIHelper.GetSignedRestString("sendMail", "sender", sender));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            urlConnection.setInstanceFollowRedirects( false );
            urlConnection.setRequestMethod( "POST" );
            urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty( "charset", "utf-8");
            urlConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            urlConnection.setUseCaches( false );
            DataOutputStream wr = new DataOutputStream( urlConnection.getOutputStream());
            wr.write( postData );

            urlConnection.connect();
            InputStream inStream = urlConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
            String temp, response = "";
            while ((temp = bReader.readLine()) != null)
                response += temp;
            bReader.close();
            inStream.close();
            urlConnection.disconnect();
            object = (JSONObject) new JSONTokener(response).nextValue();
        }
        catch (Exception e)
        {
            this.mException = e;
        }

        return (object);
    }

    @Override
    protected void onPostExecute(JSONObject result)
    {
        super.onPostExecute(result);

        if (this.mException != null){}

    }
}
