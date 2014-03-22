package com.br.contcomb;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.br.econocomb.R;
import com.br.uteis.Uteis;

public class MainActivity extends BaseActivity {

	ImageButton btnCalc;
	
	EditText etEtanol, etGasolina;
	Uteis util = new Uteis();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		chamaTelaInicial();
		menuEsquerda();
	}

	/**
	 * Carrega tela inicial
	 */
	public void chamaTelaInicial() {
		setContentView(R.layout.activity_main);
		inicializaDados();
		
		btnCalc.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				String etanol = etEtanol.getText().toString().trim();
				String gasolina = etGasolina.getText().toString().trim();
				if (etanol.equals("") || gasolina.equals("")){
					util.mostraMensagem(CAMPO_OBRIGATORIO_TITULO, CAMPO_OBRIGATORIO, MainActivity.this);
				}else{
					if(util.convertStringParaDouble(etanol) == 0 || util.convertStringParaDouble(gasolina) == 0){
						util.mostraMensagem(VALOR_INVALIDO_TITULO, CAMPO_NAO_PODE_SER_ZERO, MainActivity.this);
					}
					else{
						Double calculo = util.convertStringParaDouble(etanol)/util.convertStringParaDouble(gasolina);
						Double calc_porcentagem = calculo*100;
						String porcentagem = util.tresCasasDecimais(calc_porcentagem);
						if (calculo <= 0.7){
							util.mostraMensagem(ETANOL_OU_GASOLINA_TITULO, ETANOL + "("+porcentagem+"%)", MainActivity.this);
						}
						else{
							util.mostraMensagem(ETANOL_OU_GASOLINA_TITULO,  GASOLINA + "("+porcentagem+"%)", MainActivity.this);
						}
					}
				}
			}
		});
	}
	
	/**
	 * Inicializar dados
	 */
	public void inicializaDados() {
		btnCalc = (ImageButton) findViewById(R.id.btnCalc);
		etEtanol = (EditText) findViewById(R.id.etEtanol);
		etGasolina= (EditText) findViewById(R.id.etGasolina);
	}
	
	/**
	 * Botão voltar do android finaliza o app
	 */
	@Override
    public void onBackPressed() {
		this.finish();
	}

}
