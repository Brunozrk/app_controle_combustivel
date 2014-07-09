package com.br.contcomb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.br.econocomb.R;
import com.br.uteis.Pages;
import com.br.uteis.Variaveis;
import com.client.exceptions.InvalidCredentialsException;
import com.client.exceptions.ValidationException;

public class ContCombOnActivity extends BaseActivity {
//	String api = "http://10.0.2.2:8000";
	String api = "http://contcombapi.herokuapp.com";
	
	ProgressDialog loadingdialog;
	Cursor cursor_abastecimentos;
	String login, password, authentication;
	TextView tvDownload, tvImportar;
	Spinner spCarros;
	Cursor cursorSpinnerCarro = null;
	Button btnImportar;
	EditText etLogil, etPassword, etKmAtual, etMotor, etAno, etKmAndados;
	int idCarro = 0;
	int pagina_atual = 0;
	String errorMessage = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		chamaInicio();
		menuEsquerda();
	}
	
	public void chamaInicio(){
		pagina_atual = Pages.CONTCOMBON;
		supportInvalidateOptionsMenu();
		setContentView(R.layout.contcombon);
		abreContCombOn();
		abreImportar();
	}
	
	public void abreContCombOn(){
		tvDownload = (TextView) findViewById(R.id.tvDownload);
		tvDownload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.br.econocomb")));
				} catch (android.content.ActivityNotFoundException anfe) {
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.br.econocomb")));
				}
			}
		});
	}

	public void abreImportar(){
		tvImportar = (TextView) findViewById(R.id.tvImportar);
		tvImportar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pagina_atual = Pages.CONTCOMBON_FORM;
				setContentView(R.layout.form_importar);
				spCarros = (Spinner) findViewById(R.id.spCarros);
				carregaSpinnerCarro();
				capturaItemSelecionadoSpinnerParaGravar();
				desabilitaDrawer();
				
				btnImportar = (Button) findViewById(R.id.btnImportar);
				btnImportar.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						loadingdialog = ProgressDialog.show(ContCombOnActivity.this,
															"",
															getResources().getString(R.string.enviando),
															true);

						loadingdialog.setCanceledOnTouchOutside(true);

			            new Thread() {
			                @Override
			                public void run() {
			                    super.run();

								try {
									etLogil = (EditText) findViewById(R.id.etLogin);
									etPassword = (EditText) findViewById(R.id.etPassword);
									etKmAtual = (EditText) findViewById(R.id.etKmAtual);
									etKmAndados = (EditText) findViewById(R.id.etKmAndados);
									etMotor = (EditText) findViewById(R.id.etMotor);
									etAno = (EditText) findViewById(R.id.etAno);
									login = etLogil.getText().toString();
									password = etPassword.getText().toString();
									String carro = cursorSpinnerCarro.getString(cursorSpinnerCarro.getColumnIndex("marca"));
									JSONArray abastecimentos = new JSONArray(); 
									cursor_abastecimentos = banco_de_dados.filtraAbastecimentoPorCarroDataQuery(idCarro, "", ContCombOnActivity.this);
									cursor_abastecimentos.moveToFirst();
									
									Map<String,String> keys= new HashMap<String,String>();
							    	keys.put("odometro", "odometer");
							    	keys.put("strftime('%d/%m/%Y',date)", "date");
							    	keys.put("litros", "liters");
							    	keys.put("obs", "obs");
							    	keys.put("media", "media");
									while (cursor_abastecimentos.isAfterLast() == false) {
							       	    int totalColumn = cursor_abastecimentos.getColumnCount();
							       	    JSONObject rowObject = new JSONObject();
							       	    for( int i=0 ;  i< totalColumn ; i++ ){
							       	    	if( cursor_abastecimentos.getColumnName(i) != null ){
							       	    		try{
								       	    		if( cursor_abastecimentos.getString(i) != null ){
								           	    		Log.d("TAG_NAME", cursor_abastecimentos.getString(i) );
								           	    		rowObject.put(keys.get(cursor_abastecimentos.getColumnName(i)) ,  cursor_abastecimentos.getString(i) );
								           	    	}
								           	    	else{
								           	    		rowObject.put( cursor_abastecimentos.getColumnName(i) ,  "" ); 
								           	    	}
							       	    		}
							       	    		catch( Exception e ){
							       	    			Log.d("TAG_NAME", e.getMessage()  );
							       	    		}
							       	    	}
							 
							       	    }
							       	    abastecimentos.put(rowObject);
							       	    cursor_abastecimentos.moveToNext();
							        }
									
									HashMap<String, String> data = new HashMap<String, String>();
									
									data.put("current_km", etKmAtual.getText().toString());
									data.put("walked_km", etKmAndados.getText().toString());
									data.put("model", carro);
									data.put("motor", etMotor.getText().toString());
									data.put("manufactured", etAno.getText().toString());
									data.put("supplies", abastecimentos.toString());
									
									post(api + "/supply/import/old", data);
									handler.sendEmptyMessage(200);
									
								} catch (Exception e) {
									handler.sendEmptyMessage(500);
									errorMessage = e.getMessage();
								}
		                	}
           			 	}.start();
					}
				});
				
			}
		});
	}
	
	/**
	 * CALLBACKS
	 */
    private Handler handler = new Handler() {
        @Override
         public void handleMessage(Message msg) {
         	loadingdialog.dismiss();
        	switch (msg.what) {
        		case 200: util.mostraToast(getResources().getString(R.string.importado_sucesso), ContCombOnActivity.this);
        				  chamaInicio();
        				  habilitaDrawer(); 
        				  break;
        		case 500: util.mostraToast(errorMessage, ContCombOnActivity.this); break;
        	}
        }
    };
	
	private void carregaSpinnerCarro(){
		cursorSpinnerCarro = banco_de_dados.buscaCarrosQuery(Variaveis.CAMPOS_CARRO);
		List<String> nomes = new ArrayList<String>();
		cursorSpinnerCarro.moveToFirst();
		idCarro = cursorSpinnerCarro.getInt(cursorSpinnerCarro.getColumnIndex("_id"));
		while(!cursorSpinnerCarro.isAfterLast()){
			nomes.add(cursorSpinnerCarro.getString(cursorSpinnerCarro.getColumnIndex("marca")));
			cursorSpinnerCarro.moveToNext();
		}
		
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nomes);
		ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		spCarros.setAdapter(spinnerArrayAdapter);
	}
	
	private void capturaItemSelecionadoSpinnerParaGravar(){
		spCarros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
 
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				cursorSpinnerCarro.moveToPosition(position);
    			idCarro = cursorSpinnerCarro.getInt(cursorSpinnerCarro.getColumnIndex("_id"));
			}
 
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
 
			}
		});
	}
	
	// CLIENTE //

	private static final int POST = 1;
	
    public String post(String url, HashMap<String, String> data) throws Exception {
        return executeHTTPConnection(POST, url, data);
    }
    
    private String executeHTTPConnection(int method, String url, HashMap<String, String> data) throws IllegalStateException, IOException, JSONException, ValidationException, Exception {
    	try {
    		
    		if (!isConnected()){
    			throw new Exception("Sem conexão.");
    		}
    		
    		HttpClient httpClient = new DefaultHttpClient();
            HttpConnectionParams.setSoTimeout(httpClient.getParams(), 25000);
            HttpResponse response = null;
            switch (method) {
            case POST:
                HttpPost httpPost = new HttpPost(url);
                httpPost = setPostParams(httpPost, data);
                httpPost = (HttpPost) setHeaders(httpPost);
                response = httpClient.execute(httpPost);
                break;
            default:
                throw new IllegalArgumentException("Unknown Request.");
            }  
            
            return validResponse(response);

		} catch (HttpHostConnectException e) {
			Log.i("Error: ", e.getMessage());
			throw new Exception("Houve um erro na comunicação. Tente novamente mais tarde.");
		}
    	
    }
    
    private HttpRequestBase setHeaders(HttpRequestBase http) throws Exception{
		authentication = login+":"+password;
		authentication = Base64.encodeToString(authentication.getBytes("UTF-8"), Base64.URL_SAFE|Base64.NO_WRAP);
		http.addHeader(new BasicHeader("Accept", "application/json"));
		http.setHeader("Authorization", "Basic "+ authentication);   
		return http;
    }
    
    private boolean isConnected(){
        ConnectivityManager connectivity = (ConnectivityManager)ContCombOnActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null) 
          {
              NetworkInfo netInfo = connectivity.getActiveNetworkInfo();
              
              if (netInfo == null) {
                return false;
              }
              
              int netType = netInfo.getType();

              if (netType == ConnectivityManager.TYPE_WIFI || 
                    netType == ConnectivityManager.TYPE_MOBILE) {
                  return netInfo.isConnected();

              } else {
                  return false;
              }
          }else{
            return false;
          }
    }
    
    private HttpPost setPostParams(HttpPost httpPost, HashMap<String, String> data) throws UnsupportedEncodingException{
        ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, "UTF-8");
        httpPost.setEntity(entity);
        return httpPost;
    }
	
    private String validResponse(HttpResponse response) throws JSONException, ValidationException, Exception {
    	
    	String responseString;
    	
		if (response.getStatusLine().getStatusCode() == 401){
			throw new InvalidCredentialsException();
    	}else{
    		responseString = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
    		Log.i("Response: ", responseString);
    		JSONObject responseJson = util.convertStringToJson(responseString);
    		
    		if (responseJson != null && responseJson.has("exception") && responseJson.getBoolean("exception")){
    			
    			if(responseJson.get("name").equals("ValidationException")){
    				throw new ValidationException(getErrors(responseJson.getJSONObject("error_list")));
    			}
    			else{
    				throw new Exception("Ocorreu um erro não esperado.");
    			}
    		}
    	}
		return responseString;
	}
    
    private String getErrors(JSONObject json) throws JSONException{
    	
    	Map<String,String> keys_errors = new HashMap<String,String>();
    	keys_errors.put("username", "Usuário");
    	keys_errors.put("password", "Senha");
    	
    	String response = "";
        Iterator<String> keys = json.keys();
        JSONArray jsonArray = null;
        while(keys.hasNext()){
            String key = keys.next();
            String val = null;
            String messages = "";
            try{
                 JSONObject value = json.getJSONObject(key);
                 getErrors(value);
            }catch(Exception e){
                val = json.getString(key);
            	jsonArray = json.getJSONArray(key);
            }
            
            if(val != null){
                int i;
                for(i = 0; i < jsonArray.length(); i++){
                	messages += jsonArray.getString(i);
                }
                String key_var = keys_errors.get(key);
                key = key_var != null ? key_var : key; 
                response +=  key + ": " +  messages + "\n";
            }
        }
        return response;
    }
    
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	        case android.R.id.home:
	    		if (pagina_atual == Pages.CONTCOMBON){
	    			return(super.onOptionsItemSelected(item));
	    		}
	        	redirecionaVoltar();
	            return(true);
		}
	    return(super.onOptionsItemSelected(item));
	}
    
    
	@Override
    public void onBackPressed() {
		redirecionaVoltar();
    }
	
	/**
	 * Carrega MainActivity
	 */
	public void chamaTelaInicial() {
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
        this.finish();
	}
	
	public void redirecionaVoltar(){
		switch (pagina_atual) {
			case Pages.CONTCOMBON:
				chamaTelaInicial();
				break;
			case Pages.CONTCOMBON_FORM:
				chamaInicio();
				habilitaDrawer();
				menuEsquerda();
				break;
			default:
				finish();
				break;
		}
	}
	
}
