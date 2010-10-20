package net.androgames.blog.sample.rest.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class UserConsumer extends Activity implements OnItemClickListener {
	
	private static final DefaultHttpClient httpClient = new DefaultHttpClient();
	static {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		httpClient.setParams(params); 
	}
	
	private static final Gson gson = new Gson();

	private static final String JSON_CONTENT_TYPE 	= "application/json; charset=UTF-8";
	private static final String ENCODING_UTF_8 		= "UTF-8";

	private static final int DIALOG_ERROR 	= 0;
	private static final int DIALOG_LOADING = 1;
	
	private EditText name, forName;
	private Button insertOrUpdate, initialize, delete;
	
	private User currentUser;		// utilisateur courant
	private UserAdapter adapter;	// utilisé comme référentiel local
	
	private Object lock = new Object();
	
	/**
	 * Classe abstraite pour l'envoi de requêtes asynchrones au serveur.
	 */
	private abstract class AbstractTask 
			extends AsyncTask<HttpRequestBase, Void, HttpResponse> {
		
		/**
		 * Appelé avant le lancement du traitement en arrière plan.
		 * Cette méthode est exécuté dans le Thread appelant.
		 */
		protected void onPreExecute() {
			showDialog(DIALOG_LOADING);
		}
		
		/**
		 * Traitement en arrière plan.
		 * Cette méthode est exécuté dans un Thread différent
		 * du Thread appelant.
		 */
		protected HttpResponse doInBackground(final HttpRequestBase...requests) {
			HttpResponse response = null;
			synchronized (lock) {
				try {
					response = httpClient.execute(requests[0]);
				} catch (Exception e) {
					Log.e(UserConsumer.class.getSimpleName(), 
							"Erreur d'appel au serveur", e);
				}
			}
			return response;
	    }

		/**
		 * Appelé après la fin du traitement en arrière plan.
		 */
		protected void onPostExecute(final HttpResponse response) {
			dismissDialog(DIALOG_LOADING);
			if (response == null 
					|| !(response.getStatusLine()
							.getStatusCode() == HttpStatus.SC_OK)) {
				showDialog(DIALOG_ERROR);
			} else {
				try {
					handleJson(response.getEntity().getContent());
				} catch (IOException e) {
					Log.e(UserConsumer.class.getSimpleName(), 
							"Erreur de flux", e);
				}
			}
		}
		
		/**
		 * Traitement spécifique du JSON
		 * @param in Le contenu de la réponse HTTP OK
		 */
		protected abstract void handleJson(final InputStream in);

	};
	
	/**
	 * Récupération de la liste des User
	 */
	private class ListUsersTask extends AbstractTask {

		protected void handleJson(final InputStream in) {
			final Type collectionType = new TypeToken<List<User>>(){}.getType();
			List<User> users = null;
			synchronized (lock) {
				users = gson.fromJson(new InputStreamReader(in), collectionType);
			}
			// La liste récupérée initialement est la référence
			// des User pour l'application Android
			adapter.setUsers(users);
		}

	};
	
	/**
	 * Recuperation d'un utilisateur
	 * déjà référencé localement
	 */
	private class GetUserTask extends AbstractTask {

		protected void handleJson(final InputStream in) {
			synchronized (lock) {
				updateCurrentUser(gson.fromJson(new InputStreamReader(in), User.class));
			}
		}

	};
	
	/**
	 * Récupération d'un nouvel utilisateur
	 * non encore référencé localement
	 */
	private class AddUserTask extends AbstractTask {

		protected void handleJson(final InputStream in) {
			synchronized (lock) {
				updateCurrentUser(gson.fromJson(new InputStreamReader(in), User.class));
			}
			adapter.addUser(currentUser);
		}

	};
	
	/**
	 * Suppression d'un utilisateur
	 * référencé localement
	 */
	private class DeleteUserTask extends AbstractTask {

		protected void handleJson(final InputStream in) {
			User fakeUser = new User();
			try {
				fakeUser.setId(new BufferedReader(
						new InputStreamReader(in, ENCODING_UTF_8)).readLine());
			} catch (Exception e) {}
			adapter.removeUser(fakeUser);
			updateCurrentUser(null);
		}

	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        adapter = new UserAdapter((ListView) findViewById(R.id.users), this);
        insertOrUpdate = (Button) findViewById(R.id.insertOrUpdate);
        initialize = (Button) findViewById(R.id.initialize);
        delete = (Button) findViewById(R.id.delete);
        name = (EditText) findViewById(R.id.name);
        forName = (EditText) findViewById(R.id.forName);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	// recuperation de tous les utilisateurs
		(new ListUsersTask()).execute(new HttpGet(getString(R.string.user_endpoint)));
    	// initialisation des actions
    	((ListView) findViewById(R.id.users)).setOnItemClickListener(this);
    	// initialisation de l'IHM
    	// currentUser peut ne pas être null
    	// si l'activité a été résumée
    	updateCurrentUser(currentUser);
    }
    
    /**
     * Clique sur le bouton Créer ou Enregistrer
     * @param v
     */
    public void insertOrUpdate(View v) {
    	if (currentUser == null) {
    		// nouvel utilisateur
    		User user = new User();
    		user.setNom(name.getEditableText().toString());
    		user.setPrenom(forName.getEditableText().toString());
    		// création d'une requête de type POST
    		// l'URL contient l'ID du User à mettre à jour
    		HttpPut request = new HttpPut(getString(R.string.user_endpoint));
    		// précision du Content-Type
    		request.setHeader("Content-Type", JSON_CONTENT_TYPE);
    		synchronized (lock) {
    			try {
    				// l'objet de type User sérialisé est envoyé dans le corps
    				// de la requête HTTP et encodé en UTF-8 (cf. Jackson)
					request.setEntity(new StringEntity(
							gson.toJson(user), ENCODING_UTF_8));
				} catch (UnsupportedEncodingException e) {}
    		}
    		(new AddUserTask()).execute(request);
    	} else {
    		// mise a jour du currentUser
    		User updatedUser = new User();
    		updatedUser.setNom(name.getEditableText().toString());
    		updatedUser.setPrenom(forName.getEditableText().toString());
    		// création d'une requête de type POST
    		// l'URL contient l'ID du User à mettre à jour
    		HttpPost request = new HttpPost(
    				getString(R.string.user_endpoint) + "/" + currentUser.getId());
    		// précision du Content-Type
    		request.setHeader("Content-Type", JSON_CONTENT_TYPE);
    		synchronized (lock) {
    			try {
    				// l'objet de type User sérialisé est envoyé dans le corps
    				// de la requête HTTP et encodé en UTF-8 (cf. Jackson)
					request.setEntity(new StringEntity(
							gson.toJson(updatedUser), ENCODING_UTF_8));
				} catch (UnsupportedEncodingException e) {}
    		}
    		(new GetUserTask()).execute(request);
    	}
    }
    
    /**
     * Clique sur le bouton Supprimer
     * @param v
     */
    public void delete(View v) {
    	// envoi d'une requête DELETE au serveur
    	// sur l'URL correspondant au User à supprimer
		(new DeleteUserTask()).execute(new HttpDelete(
				getString(R.string.user_endpoint) + "/" + currentUser.getId()));
    }
    
    /**
     * Clique sur le bouton Annuler
     * @param v
     */
    public void initialize(View v) {
    	// raz du formulaire
		updateCurrentUser(null);
    }

    // Met a jour le formulaire avec les
    // informations de l'utilisateur passé
    // en paramètre. Si le formulaire est 
    // positionné sur les données d'un utilisateur
    // de même id, le référentiel local est mis à jour
	private void updateCurrentUser(User user) {
		if (user == null) {
			currentUser = null;
			name.setText("");
			forName.setText("");
			delete.setVisibility(View.GONE);
			initialize.setVisibility(View.GONE);
			insertOrUpdate.setText(R.string.create);
		} else {
			if (!user.equals(currentUser)) {
				// changement de User
				currentUser = user;
			} else {
				// mise a jour des informations du User
				// dans le référentiel local
				currentUser.setNom(user.getNom());
				currentUser.setPrenom(user.getPrenom());
				adapter.notifyDataSetChanged();
			}
			// mise à jour du formulaire
			name.setText(currentUser.getNom());
			forName.setText(currentUser.getPrenom());
			delete.setVisibility(View.VISIBLE);
			initialize.setVisibility(View.VISIBLE);
			insertOrUpdate.setText(R.string.update);
		}
	}
    
    @Override
    public Dialog onCreateDialog(int dialogId) {
    	Dialog dialog = null;
    	AlertDialog.Builder builder = null;
    	switch (dialogId) {
    	
	    	case DIALOG_LOADING : // recuperation en cours...
	    		dialog = new ProgressDialog(this);
	    		((ProgressDialog) dialog).setIndeterminate(true);
	    		((ProgressDialog) dialog).setMessage(getString(R.string.loading));
	    		break;
	    		
	    	case DIALOG_ERROR : // message d'erreur
	    		builder = new AlertDialog.Builder(this);
	    		builder.setTitle(R.string.error)
	    			   .setMessage(R.string.error_message)
	    			   .setNegativeButton(R.string.close, new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
	    		dialog = builder.create();
	    		break;
    	}
    	return dialog;
    }
    
    /**
     * L'utilisateur à cliqué sur un User de la liste,
     * le formulaire est mis à jour avec les informations
     * du User récupérés depuis le serveur
     */

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// mise a jour du formulaire avec les informations
		// du User sélectionné par l'utilisateur
		updateCurrentUser((User) adapter.getItem(position));
		
		// on aurait également pu demander l'utilisateur 
		// au serveur à chaque fois, mais on considere
		// que la liste initialement chargée est notre
		// référence afin de garder un jeu de donnée cohérent
		
		// (new GetUserTask()).execute(new HttpGet(
		//		getString(R.string.user_endpoint) + 
		//		"/" + ((User) adapter.getItem(position)).getId()));
	}
    
}