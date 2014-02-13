package com.br.econocomb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class Uteis {
	
	// MOSTRA MENSAGEM NA TELA
	public void mostraMensagem(String msg, Context context) {
		AlertDialog.Builder mensagem = new AlertDialog.Builder(context);
		mensagem.setTitle("AVISO!");
		mensagem.setMessage(msg);
		mensagem.setNeutralButton("OK", null);
		mensagem.show();
	}
	

	// MOSTRA MENSAGEM EM TOAST
	public void mostraToast(String msg, Context context) {

		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	/**
	 * Display a confirm dialog. 
	 * @param activity
	 * @param title
	 * @param message
	 * @param positiveLabel
	 * @param negativeLabel
	 * @param onPositiveClick runnable to call (in UI thread) if positive button pressed. Can be null
	 * @param onNegativeClick runnable to call (in UI thread) if negative button pressed. Can be null
	 */
	public void confirm(
	        final Activity activity, 
	        final String title, 
	        final String message,
	        final String positiveLabel, 
	        final String negativeLabel,
	        final Runnable onPositiveClick,
	        final Runnable onNegativeClick) {

	            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
	            dialog.setTitle(title);
	            dialog.setMessage(message);
	            dialog.setCancelable (false);
	            dialog.setPositiveButton(positiveLabel,
	                    new DialogInterface.OnClickListener () {
	                public void onClick (DialogInterface dialog, int buttonId) {
	                    if (onPositiveClick != null) onPositiveClick.run();
	                }
	            });
	            dialog.setNegativeButton(negativeLabel,
	                    new DialogInterface.OnClickListener () {
	                public void onClick (DialogInterface dialog, int buttonId) {
	                    if (onNegativeClick != null) onNegativeClick.run();
	                }
	            });
	            dialog.setIcon (android.R.drawable.ic_dialog_alert);
	            dialog.show();

	        }
	
}
