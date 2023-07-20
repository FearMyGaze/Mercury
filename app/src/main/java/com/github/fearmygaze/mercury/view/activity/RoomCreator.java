package com.github.fearmygaze.mercury.view.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.firebase.interfaces.OnUsersResponseListener;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.adapter.AdapterUser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class RoomCreator extends AppCompatActivity {

    ShapeableImageView goBack;
    MaterialButton create;

    TextInputLayout searchLayout;
    TextInputEditText searchBox;

    AdapterUser adapterUser;
    RecyclerView friendList;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_creator);

        goBack = findViewById(R.id.roomCreatorGoBack);
        create = findViewById(R.id.roomCreatorCreate);

        searchLayout = findViewById(R.id.roomCreatorSearchError);
        searchBox = findViewById(R.id.roomCreatorSearch);

        friendList = findViewById(R.id.roomCreatorUsers);

        user = AppDatabase.getInstance(RoomCreator.this).userDao().getUserByUserID(getIntent().getStringExtra(User.ID));

        goBack.setOnClickListener(v -> onBackPressed());

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapterUser.setFilteredUsers(s.toString().trim());
            }
        });

        setFriends();

        create.setOnClickListener(v -> {
//            Room.Exists(user, adapterUser.getSelectedUsers(), new Room.OnDataResultListener() {
//                @Override
//                public void onResult(int resultCode, Object object) {
//                    if (resultCode == 1) {//TODO: We need to pass the user roomData
//                        Toast.makeText(RoomCreator.this, "Room Created", Toast.LENGTH_SHORT).show();
//                        finish();
//                        startActivity(new Intent(RoomCreator.this, ChatRoom.class)
//                                .putExtra("roomID", object.toString()));
//                    } else if (resultCode == 2)
//                        Toast.makeText(RoomCreator.this, "Room already exists", Toast.LENGTH_SHORT).show();
//                    else if (resultCode == -1)
//                        Toast.makeText(RoomCreator.this, "Error", Toast.LENGTH_SHORT).show();
//                    else
//                        Toast.makeText(RoomCreator.this, resultCode + "", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onFailure(String message) {
//                    Toast.makeText(RoomCreator.this, message, Toast.LENGTH_SHORT).show();
//                }
//            });

        });

        adapterUser = new AdapterUser(new ArrayList<>(), user.getId(), AdapterUser.TYPE_ROOM, count -> create.setEnabled(count > 0));
        friendList.setLayoutManager(new LinearLayoutManager(RoomCreator.this, LinearLayoutManager.VERTICAL, false));
        friendList.setAdapter(adapterUser);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFriends();
    }

    private void setFriends() {
        Friends.getRequestedList(user, Friends.LIST_FOLLOWERS, RoomCreator.this, new OnUsersResponseListener() {
            @Override
            public void onSuccess(int code, List<User> list) {
                if (code == 0 && !list.isEmpty()) {
                    adapterUser.setData(list);
                } else {
                    Toast.makeText(RoomCreator.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(RoomCreator.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}