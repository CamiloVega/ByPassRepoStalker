package com.bypassmobile.octo.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bypassmobile.octo.MainActivity;
import com.bypassmobile.octo.R;
import com.bypassmobile.octo.adapter.UserListAdapter;
import com.bypassmobile.octo.model.User;
import com.squareup.phrase.Phrase;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * A fragment that contains a list of Users
 */

//Annotation that enables the inflation of the layout for this Fragment
@EFragment(R.layout.user_list_fragment_layout)
public class UserListFragment extends Fragment {

    //Annotation that enables the user to pass the boolean as part of the constructor.
    @FragmentArg
    boolean shouldDisplayBackButton;

    //Annotation that automates the onSaveInstance and restoreInstanceState
    @InstanceState
    protected UserListAdapter userListAdapter;

    @InstanceState
    protected User selectedUser;

    protected List<User> userList;

    protected UserListListener listener;

    //Annotation that automates the view binding.
    @ViewById(R.id.user_listview)
    ListView userListView;

    @ViewById(R.id.current_user_label)
    TextView currentUserName;

    @ViewById(R.id.current_user_cell)
    LinearLayout currentUserCell;

    @ViewById(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;

    /**
     * Method annotated with afterviews
     * This method is called after all views are bound to the local variables
     */

    @AfterViews
    protected void afterViews() {

        //Handles the back button of the toolbar for this particular fragment.
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(shouldDisplayBackButton);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onFragmentBackPressed();
                }
            }
        });

        if (selectedUser != null) {
            currentUserCell.setVisibility(View.VISIBLE);
            currentUserName.setText(Phrase.from(getActivity(), R.string.follows)
                    .put("name", selectedUser.getName())
                    .format().toString());
        } else {
            currentUserCell.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (userListAdapter == null) {
            userListAdapter = new UserListAdapter(userList);
        }
        userListAdapter.setContext(getActivity());
        userListView.setAdapter(userListAdapter);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.onUserClicked(((User) userListAdapter.getItem(position)));
                }
            }
        });
        userListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof MainActivity){
            setListener((MainActivity) getActivity());
        }
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public void setListener(UserListListener listener) {
        this.listener = listener;
    }

    public void filterUsersByString(String filterString) {
        if (userListAdapter != null) {
            userListAdapter.filterDataByString(filterString);
        }
    }

    public void resetFilter() {
        userListAdapter.resetFilter();
    }

    public void updateListAdapter(){
        if (userListAdapter != null) {
            userListAdapter.notifyDataSetChanged();
        }
    }
    /**
     * Interface that allows interaction between the fragment and the activity
     */
    public interface UserListListener {

        /**
         * Method to be called after a user has been selected from the list
         * @param user the user that was selected
         */
        void onUserClicked(User user);

        /**
         * Method to be called if the back button is pressed in the fragments toolbar
         */
        void onFragmentBackPressed();
    }
}
