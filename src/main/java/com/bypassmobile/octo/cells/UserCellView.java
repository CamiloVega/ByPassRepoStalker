package com.bypassmobile.octo.cells;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bypassmobile.octo.R;
import com.bypassmobile.octo.image.ImageLoader;
import com.bypassmobile.octo.model.User;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup (R.layout.user_cell_view_layout)
public class UserCellView extends LinearLayout{

    @ViewById(R.id.profile_pic)
    ImageView profilePic;

    @ViewById(R.id.user_name_label)
    TextView userNameLabel;

    @ViewById(R.id.following_badge)
    TextView followingCounter;

    @Bean
    ImageLoader imageLoader;


    public UserCellView(Context context) {
        super(context);
    }

    public UserCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserCellView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void bind (User user) {
        imageLoader.loadCircularImage(user.getProfileURL(), profilePic);
        userNameLabel.setText(user.getName());
        if (user.getNumberOfFollowers() > 0){
            followingCounter.setVisibility(VISIBLE);
            followingCounter.setText(String.valueOf(user.getNumberOfFollowers()));
        } else {
            followingCounter.setVisibility(GONE);
        }
    }
}
