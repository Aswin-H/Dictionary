package com.aswinhariram2005.dictionary;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.aswinhariram2005.dictionary.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        queue = Volley.newRequestQueue(this);
        binding.searchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    String val = textView.getText().toString();
                    if (!val.isEmpty()) {
                        binding.searchLay.setErrorEnabled(false);

                        getDef(val);

                    } else {
                        Toast.makeText(MainActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                        binding.searchLay.setErrorEnabled(true);
                        binding.lay.setVisibility(View.GONE);
                        binding.animationView.setVisibility(View.VISIBLE);

                    }

                    return true;
                }
                return false;
            }
        });
    }

    private void getDef(String word) {
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        String url = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject object = response.getJSONObject(0);
                    String word_val = null;
                    try {
                        word_val = object.getString("word");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    binding.word.setText(word_val);

                    JSONArray array = object.getJSONArray("meanings");
                    JSONObject object1 = array.getJSONObject(0);
                    JSONObject object2 = array.getJSONObject(1);

                    JSONArray noun_arr = object1.getJSONArray("definitions");
                    JSONArray verb_arr = object2.getJSONArray("definitions");

                    JSONObject verb_obj = verb_arr.getJSONObject(0);
                    JSONObject non_obj = noun_arr.getJSONObject(0);

                    String verb_def ="",non_def="",non_mean="",non_opp="";

                    try {
                        verb_def = verb_obj.getString("definition");
                         non_def = non_obj.getString("definition");
                         non_mean = object1.getString("synonyms");
                         non_opp = object1.getString("antonyms");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }



                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    binding.lay.setVisibility(View.VISIBLE);
                    binding.animationView.setVisibility(View.GONE);
                    binding.nounDef.setText(non_def);
                    binding.verbDef.setText(verb_def);
                    binding.meanTxt.setText(non_mean);
                    binding.oppTxt.setText(non_opp);
                    dialog.dismiss();

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Sorry result not found", Toast.LENGTH_SHORT).show();
                    binding.searchLay.setErrorEnabled(true);
                    binding.lay.setVisibility(View.GONE);
                    binding.animationView.setVisibility(View.VISIBLE);
                    dialog.dismiss();


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                binding.searchLay.setErrorEnabled(true);
                binding.lay.setVisibility(View.GONE);
                binding.animationView.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
        queue.add(request);

    }
}