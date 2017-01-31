package com.bypassmobile.octo.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.bypassmobile.octo.cells.UserCellView;
import com.bypassmobile.octo.cells.UserCellView_;
import com.bypassmobile.octo.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Custom Base adapter to manage the information to be displayed in the User List View
 */

public class UserListAdapter extends BaseAdapter implements Filterable, Serializable {

    private List<User> userList;
    private List<User> filteredUserList;
    private transient Context context;

    public UserListAdapter(List<User> userList) {
        this.userList = userList;
        this.filteredUserList = userList;
    }

    public void resetFilter(){
        filteredUserList = userList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredUserList.size();
    }

    @Override
    public Object getItem(int i) {
       return filteredUserList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        resetFilter();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = UserCellView_.build(context);
        }
        ((UserCellView) view).bind(filteredUserList.get(i));
        return view;
    }

    public void filterDataByString(String filterString) {
        getFilter().filter(filterString);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredUserList = (List<User>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<User> filteredResults = filterArray(constraint);
                Collections.sort(filteredResults, new Comparator<User>(){
                    public int compare(User user1, User user2) {
                        return user1.getName().compareToIgnoreCase(user2.getName());
                    }
                });
                FilterResults results = new FilterResults();
                results.values = filteredResults;
                results.count = filteredResults.size();
                return results;
            }

            protected ArrayList<User> filterArray(CharSequence constraint) {
                ArrayList<User> filterArray = new ArrayList<>();
                for (User user : userList) {
                    if (user.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filterArray.add(user);
                    }
                }
                return filterArray;
            }
        };
    }

}