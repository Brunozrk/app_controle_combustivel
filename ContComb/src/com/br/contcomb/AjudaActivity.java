package com.br.contcomb;

import com.br.econocomb.R;

import android.os.Bundle;

public class AjudaActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ajuda);
		menuEsquerda();
	}
	
	/**
	 * Botão voltar do android finaliza o app
	 */
	@Override
    public void onBackPressed() {
		this.finish();
	}

}
