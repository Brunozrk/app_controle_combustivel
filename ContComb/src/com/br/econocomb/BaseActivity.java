package com.br.econocomb;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.br.banco.BancoDeDados;
import com.br.uteis.Messages;
import com.br.uteis.Uteis;
import com.br.uteis.Variaveis;

public abstract class BaseActivity extends ActionBarActivity {
	
	BancoDeDados banco_de_dados;
	
	private String[] drawerListViewItems;
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    Uteis util = new Uteis();
	
    MenuItem menu_novo, menu_grava, menu_envia;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        banco_de_dados = new BancoDeDados(BaseActivity.this);
    }
    
	/**
	 * Carrega menu esquerda
	 */
	public void menuEsquerda(){
		// get list items from strings.xml
		drawerListViewItems = getResources().getStringArray(R.array.items);

		// get ListView defined in activity_main.xml
		drawerListView = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
		drawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_listview_item, drawerListViewItems));
      
		// 2. App Icon 
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		// 2.1 create ActionBarDrawerToggle
		actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
                );
		// 2.2 Set actionBarDrawerToggle as the DrawerListener
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        
        // 2.3 enable and show "up" arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); 
        
        // just styling option
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		
		drawerListView.setOnItemClickListener(new DrawerItemClickListener());
	}

	public void desabilitaDrawer(){
		actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
	}
	
	public void habilitaDrawer(){
		actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
         actionBarDrawerToggle.syncState();
    }
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		 // call ActionBarDrawerToggle.onOptionsItemSelected(), if it returns true
        // then it has handled the app icon touch event

		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Controle do menu da esquerda (Drawer) 
	 */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	    	
	    	switch (position) {
	    	// Página Inicial
			case 0:	Intent iMain = new Intent(getBaseContext(), MainActivity.class);
		            startActivity(iMain);
		            finish();
		            break;
		    // Carros
			case 1: Intent iCarro = new Intent(getBaseContext(), CarroActivity.class);
		            startActivity(iCarro);
		            finish();
		            break;
		    // Abastecimentos
			case 2: if (banco_de_dados.buscaCarrosQuery(Variaveis.CAMPOS_CARRO).getCount() == 0){
						util.mostraToast(Messages.NAO_HA_CARRO_CADASTRADO, BaseActivity.this);
					}else{
						Intent iAbastecimento = new Intent(getBaseContext(), AbastecimentoActivity.class);
						startActivity(iAbastecimento);
						finish();
					}
					break;
			// Contato
			case 3: Intent iContato = new Intent(getBaseContext(), ContatoActivity.class);
		            startActivity(iContato);
		            finish();
		            break;					
            // Ajuda
			case 4: Intent iAjuda = new Intent(getBaseContext(), AjudaActivity.class);
					startActivity(iAjuda);
					finish();
					break;					
			// Sair
			case 5: finish();
					break;
			// Nenhum dos outros
			default: finish();
					 break;
			}
	    	drawerLayout.closeDrawer(drawerListView);
	    }
	}
	
//	SimpleCursorAdapter.ViewBinder binder = new SimpleCursorAdapter.ViewBinder() {
//	    @Override
//	    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
//	    	int cont = cursor.getPosition();
//	    	if ((cont) % 2 != 1) {
//	    	    view.setBackgroundColor(Color.parseColor("#80f9f9f9"));
//	    	} 
//	        return false;
//	    }
//	};
}