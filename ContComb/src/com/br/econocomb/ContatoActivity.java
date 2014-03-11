package com.br.econocomb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.br.uteis.Messages;
import com.br.uteis.Uteis;

public class ContatoActivity extends BaseActivity {

	
	Uteis util = new Uteis();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.form_contato);
		menuEsquerda();
	}
	
	/**
	 * Criação do menu no action bar
	 * @param menu
	 * @return
	 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * Preparação do menu no action bar
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
    	menu_envia = menu.findItem(R.id.menu_envia); 
		menu_grava = menu.findItem(R.id.menu_grava); 
		menu_novo = menu.findItem(R.id.menu_novo); 
	
		menu_novo.setVisible(false);
		menu_grava.setVisible(false);
		menu_envia.setVisible(true);
		return true;
    }
    
	EditText etMensagem;
	
	/**
	 * Ações dos botões na action bar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_envia: enviaMensagem();
						
		}
	    return(super.onOptionsItemSelected(item));
	}
	
	/**
	 * Prepara email e envia mensagem
	 */
	public void enviaMensagem(){
		etMensagem = (EditText) findViewById(R.id.etMensagem);
		String assunto = "ContComb";
		String message = etMensagem.getText().toString();
		String to = "brunozrk@gmail.com";
		if (message.equals("")){
			util.mostraMensagem("Campo Obrigatório", Messages.CAMPO_OBRIGATORIO, ContatoActivity.this);
		}else{
			Intent emailActivity = new Intent(Intent.ACTION_SEND);
			
			//set up the recipient address
			emailActivity.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
			
			//set up the email subject
			emailActivity.putExtra(Intent.EXTRA_SUBJECT, assunto);
			
			//you can specify cc addresses as well
			// email.putExtra(Intent.EXTRA_CC, new String[]{ ...});
			// email.putExtra(Intent.EXTRA_BCC, new String[]{ ... });
			
			//set up the message body
			emailActivity.putExtra(Intent.EXTRA_TEXT, message);
			
			emailActivity.setType("message/rfc822");
			
			startActivity(Intent.createChooser(emailActivity, "Selecione seu provedor de Email"));
		}
	}
	
	/**
	 * Botão voltar do android finaliza o app
	 */
	@Override
    public void onBackPressed() {
		this.finish();
	}

}
