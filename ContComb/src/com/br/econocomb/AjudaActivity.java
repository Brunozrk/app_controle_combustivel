package com.br.econocomb;

import android.os.Bundle;

public class AjudaActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ajuda);
		menuEsquerda();
	}
	
	/**
	 * Bot�o voltar do android finaliza o app
	 */
	@Override
    public void onBackPressed() {
		this.finish();
	}

}
