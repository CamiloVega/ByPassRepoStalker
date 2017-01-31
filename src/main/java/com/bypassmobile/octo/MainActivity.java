package com.bypassmobile.octo;

import android.app.ProgressDialog;

import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.bypassmobile.octo.fragments.UserListFragment;
import com.bypassmobile.octo.fragments.UserListFragment_;
import com.bypassmobile.octo.model.User;
import com.squareup.phrase.Phrase;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

//Annotation that automates the layout inflation
@EActivity (R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends BaseActivity implements UserListFragment.UserListListener {

    private static final String ORG_NAME = "bypasslane";

    MenuItem searchMenuItem;

    SearchView searchView;

    ProgressDialog progress;

    /**
     * Method annotated with afterviews
     * This method is called after all views are bound to the local variables
     */

    @AfterViews
    protected void afterViews() {
        //If there are no fragments in the stack, populate it using the members of ORG_NAME
        if (getFragmentCount() == 0) {
            requestOrganizationMembers();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        searchMenuItem = menu.findItem(R.id.menu_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        if (searchView != null) { // safety check

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    ((UserListFragment) getCurrentFragment()).filterUsersByString(newText);
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();
                    return true;
                }
            });
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    ((UserListFragment) getCurrentFragment()).resetFilter();
                    return false;
                }
            });

            //Modify the edit text color for contrast
            EditText searchEditText = (EditText) searchView
                    .findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchEditText
                    .setBackgroundColor(getResources().getColor(R.color.dark_blue, null));
        }

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Method annotated with Background will be run in a background thread
     * Requests the member list from the organization defined by ORG_NAME
     * Upon return, creates a new fragment with the new data and displays it.
     */
    @Background
    protected void requestOrganizationMembers() {

        showProgressDialog(Phrase.from(this, R.string.fetch_organization_members)
                .put("name", ORG_NAME)
                .format().toString());

        getEndpoint().getOrganizationMember(ORG_NAME, new Callback<List<User>>() {
            @Override
            public void success(List<User> users, Response response) {
                //Updates all the users following count in the background.
                for (User user : users) {
                    updateUserFollowingCount(user);
                }
                UserListFragment fragment =  UserListFragment_.builder().shouldDisplayBackButton(false).build();
                fragment.setUserList(users);
                showFragment(fragment);
                dismissProgressDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                dismissProgressDialog();
            }
        });
    }


    /**
     * Method annotated with Background will be run in a background thread
     * Requests the following list from the User
     * Upon return, creates a new fragment with the new data and displays it.
     * @param user The User whose following list we want to retrieve
     */
    @Background
    protected void getUserFollowers(final User user) {

        showProgressDialog(Phrase.from(this, R.string.fetch_user_stalker)
                .put("name", user.getName())
                .format().toString());
        getEndpoint().getFollowingUser(user.getName(), new Callback<List<User>>() {
            @Override
            public void success(List<User> users, Response response) {
                for (User user : users) {
                    updateUserFollowingCount(user);
                }
                UserListFragment fragment =  UserListFragment_.builder().shouldDisplayBackButton(true).build();
                fragment.setUserList(users);
                fragment.setSelectedUser(user);
                showFragment(fragment);
                dismissProgressDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                dismissProgressDialog();
            }
        });
    }

    /**
     * Method annotated with Background will be run in a background thread
     * Requests the complete user object
     * Upon return, updates the missing fields.
     * @param userIncomplete The User whose following list we want to retrieve
     */
    @Background
    protected void updateUserFollowingCount(final User userIncomplete) {

        getEndpoint().getUser(userIncomplete.getName(), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                userIncomplete.setNumberOfFollowers(user.getNumberOfFollowers());
                if (getCurrentFragment() != null){
                    ((UserListFragment) getCurrentFragment()).updateListAdapter();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    /**
     * Method annotated with UIThread will be run in the main thread
     * Adds the fragment to the backstack and displays it in the fragment container
     * @param fragment the fragment to be displayed
     */
    @UiThread
    public void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_fragment_container, fragment, Integer.toString(getFragmentCount()))
                .addToBackStack(null)
                .commit();

    }

    @UiThread
    protected void showProgressDialog(String message){
        progress = ProgressDialog.show(this, getString(R.string.fetching_data),
                message, true);
    }

    @UiThread
    protected void dismissProgressDialog(){
        progress.dismiss();
    }

    protected int getFragmentCount() {
        return getSupportFragmentManager().getBackStackEntryCount();
    }

    private Fragment getFragmentAt(int index) {
        return getFragmentCount() > 0 ? getSupportFragmentManager().findFragmentByTag(Integer.toString(index)) : null;
    }

    protected Fragment getCurrentFragment() {
        return getFragmentAt(getFragmentCount() - 1);
    }

    /**
     * Action to be performed when a user is selected on the UserListFragment
     * @param user the user that was selected
     */
    @Override
    public void onUserClicked(User user) {
        searchView.setIconified(true);
        getUserFollowers(user);
    }

    @Override
    public void onFragmentBackPressed() {
        getSupportFragmentManager().popBackStack();
    }


}
