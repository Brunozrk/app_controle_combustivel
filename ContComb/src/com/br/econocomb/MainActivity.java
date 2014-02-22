package com.br.econocomb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.br.banco.BancoDeDados;
import com.br.uteis.Messages;
import com.br.uteis.Uteis;
import com.br.uteis.Variaveis;

public class MainActivity extends Activity {

	BancoDeDados banco_de_dados;
	Button btnCarros, btnAbastecimentos, btnCalc;
	
	EditText etEtanol, etGasolina;
	Uteis util = new Uteis();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		banco_de_dados = new BancoDeDados(MainActivity.this);
		chamaTelaInicial();
	}
	
	/**
	 * Carrega tela inicial
	 */
	public void chamaTelaInicial() {
		setContentView(R.layout.activity_main);
		inicializaDados();
		criaBotaoCarros();
		criaBotaoAbastecimentos();
		
		btnCalc.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String etanol = etEtanol.getText().toString().trim();
				String gasolina = etGasolina.getText().toString().trim();
				if (etanol.isEmpty() || gasolina.isEmpty()){
					util.mostraMensagem("Campo Obrigatório", Messages.CAMPO_OBRIGATORIO, MainActivity.this);
				}else{
					if(util.convertStringParaDouble(etanol) == 0 || util.convertStringParaDouble(gasolina) == 0){
						util.mostraMensagem("Valor Inválido", Messages.CAMPO_NAO_PODE_SER_ZERO, MainActivity.this);
					}
					else{
						Double calculo = util.convertStringParaDouble(etanol)/util.convertStringParaDouble(gasolina);
						Double calc_porcentagem = calculo*100;
						String porcentagem = util.tresCasasDecimais(calc_porcentagem);
						if (calculo <= 0.7){
							util.mostraMensagem("Etanol ou Gasolina?", "O valor do etanol é MENOR que 70% do valor da gasolina. Compensa abastecer com ETANOL. ("+porcentagem+"%)", MainActivity.this);
						}
						else{
							util.mostraMensagem("Etanol ou Gasolina?", "O valor do etanol é MAIOR que 70% do valor da gasolina. Compensa abastecer com GASOLINA. ("+porcentagem+"%)", MainActivity.this);
						}
					}
				}
			}
		});
	}
	
	/**
	 * Cria botão que carrega CarroActivity
	 */
	public void criaBotaoCarros(){
		btnCarros.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
	            Intent i = new Intent(getBaseContext(), CarroActivity.class);
                startActivity(i);
                banco_de_dados.fechaBancoDeDados(MainActivity.this);
                finish();
			}
		});
	}
	
	/**
	 * Cria botão que carrega AbastecimentoActivity
	 */
	public void criaBotaoAbastecimentos(){
		btnAbastecimentos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (banco_de_dados.buscaCarrosQuery(Variaveis.CAMPOS_CARRO).getCount() == 0){
					util.mostraToast(Messages.NAO_HA_CARRO_CADASTRADO, MainActivity.this);
				}else{
		            Intent i = new Intent(getBaseContext(), AbastecimentoActivity.class);
	                startActivity(i);
	                banco_de_dados.fechaBancoDeDados(MainActivity.this);
	                finish();
				}

			}
		});
	}
	
	/**
	 * Inicializar dados
	 */
	public void inicializaDados() {
		btnAbastecimentos = (Button) findViewById(R.id.btnAbastecimetos);
		btnCarros = (Button) findViewById(R.id.btnCarros);
		btnCalc = (Button) findViewById(R.id.btnCalc);
		
		etEtanol = (EditText) findViewById(R.id.etEtanol);
		etGasolina= (EditText) findViewById(R.id.etGasolina);
	}
	
	/**
	 * Botão voltar do android finaliza o app
	 */
	@Override
    public void onBackPressed() {
		banco_de_dados.fechaBancoDeDados(MainActivity.this);
		this.finish();
	}

}
