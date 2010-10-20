package net.androgames.blog.sample.rest.client;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class UserAdapter extends BaseAdapter {

	private List<User> users;
	private LayoutInflater inflater;

	public UserAdapter(final ListView list, Context context) {
		this.inflater = LayoutInflater.from(context);
		// on attache l'adapter à la ListView
		list.setAdapter(this);
		// raffraichissement de la liste lorsque
		// une donnée est modifiée
        registerDataSetObserver(new DataSetObserver() {
        	public void onChanged() {
        		list.invalidateViews();
        	}
		});
	}

	public int getCount() {
		if (users == null) {
			return 0;
		} else {
			return users.size();
		}
	}

	public Object getItem(int position) {
		return users.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		TaskHolder holder;
		
		// get holder
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.user, null);
			holder = new TaskHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.id = (TextView) convertView.findViewById(R.id.id);
			convertView.setTag(holder);
		} else {
			holder = (TaskHolder) convertView.getTag();
		}
		
		// affichage de l'utilisateur d'index demande
		// via l'utilisation du holder
		if (position < getCount()) {
			User user = (User) getItem(position);
			holder.name.setText(user.getPrenom() + " " +user.getNom());
			holder.id.setText(user.getId());
		}
		
		return convertView;
	}
	
	public void setUsers(List<User> users) {
		this.users = users;
		// mise à jour de la liste
		notifyDataSetChanged();
	}
	
	public void addUser(User user) {
		if (!users.contains(user)) {
			users.add(user);
			// mise à jour de la liste
			notifyDataSetChanged();
		}
	}

	public void removeUser(User user) {
		users.remove(user);
		// mise à jour de la liste
		notifyDataSetChanged();
	}
	
	/**
	 * Holder class :
	 * Permet de ne pas multiplier le nombre
	 * d'instance de View utilisées pour
	 * l'affichae des utilisateurs dans la
	 * ListView
	 */
	static class TaskHolder {
		TextView name;
		TextView id;
	}

}
