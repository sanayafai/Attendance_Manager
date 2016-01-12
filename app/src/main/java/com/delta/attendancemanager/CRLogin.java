package com.delta.attendancemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CRLogin extends ActionBarActivity {
    MySqlAdapter handler;
    EditText username,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        handler = new MySqlAdapter(this,null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crlogin);
        Button crlogin = (Button) findViewById(R.id.crlogin);
        List<String[]> all =new ArrayList<>();
        all = handler.get_days();
        final boolean isEmpty = handler.get_days().size() == 0;


        crlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 username = (EditText) findViewById(R.id.crbranch);
                 password = (EditText) findViewById(R.id.crpass);
                SharedPreferences share1=getSharedPreferences("user",Context.MODE_PRIVATE);
                String rno=share1.getString("crrno",":)");
                if(rno.equals(":)")){
                    if(isEmpty){
                        handler.add_day("Monday","","","","","","","","");
                        handler.add_day("Tuesday","","","","","","","","");
                        handler.add_day("Wednesday","","","","","","","","");
                        handler.add_day("Thursday","","","","","","","","");
                        handler.add_day("Friday","","","","","","","","");
                    }
                }
                CrAuth auth = new CrAuth();
                auth.execute(username.getText().toString(),password.getText().toString());

            }
        });
    }

    class CrAuth extends AsyncTask<String,Void,Boolean>{
        JSONParser jp=new JSONParser();
         String usernameString;
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                JSONObject js=new JSONObject();
                usernameString = params[0];
                js.put("username",params[0]);
                js.put("password", params[1]);
                JSONObject jd=jp.makeHttpRequest(MainActivity.URL+"/crlogin","POST",js);
                Log.i("ls", js.toString());
                int success=jd.getInt("Signed Up");
                String secret = jd.getString("secret");
                SharedPreferences prefs = getSharedPreferences("user",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("secret", secret);
                editor.apply();
                return success==1;                                                //authentication
            }  catch (Exception e) {
                e.printStackTrace();

            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                String rno = usernameString;
                SharedPreferences share=getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=share.edit();
                editor.putString("crrno", rno);
                editor.commit();
                Intent i = new Intent(CRLogin.this, CRhome.class);
                startActivity(i);
            }
            else {
                username.setText("");
                password.setText("");
                TextView err = (TextView) findViewById(R.id.wrongPassword);
                err.setText("Wrong Roll No or Password");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crlogin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_POST) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
