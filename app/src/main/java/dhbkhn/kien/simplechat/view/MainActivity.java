package dhbkhn.kien.simplechat.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dhbkhn.kien.simplechat.R;
import dhbkhn.kien.simplechat.model.Mess;
import dhbkhn.kien.simplechat.model.User;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "UserList";
    private DatabaseReference mUserlistReference;
    private ValueEventListener mUserListListener;
    private DatabaseReference mMessListRef;
    private ValueEventListener mMessListListener;

    private ListView mLvUserList;
    private List<String> mListUsername;
    private ArrayAdapter<String> mAdapter;

    private ListView mLvMessList;
    private List<String> mMessList;
    private ArrayAdapter<String> mAdapterMess;

    private EditText mEdtMess;
    private Button mBtnSend;
    private Button mBtnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserlistReference = FirebaseDatabase.getInstance().getReference().child("users");
        mMessListRef = FirebaseDatabase.getInstance().getReference().child("mess_list/chung");

        addControls();
        addEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (!data.getValue(User.class).getName().equals(usernameOfCurrentUser())) {
                        mListUsername.add(data.getValue(User.class).getName());
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ",databaseError.toException());
            }
        };

        mUserlistReference.addValueEventListener(userListener);
        mUserListListener = userListener;

        final ValueEventListener messListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMessList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data != null) {
                        mMessList.add(data.getValue(Mess.class).getText() + " - " + data.getValue(Mess.class).getAuthor());
                        mAdapterMess.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ",databaseError.toException());
            }
        };

        mMessListRef.addValueEventListener(messListener);
        mMessListListener = messListener;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mUserListListener != null) {
            mUserlistReference.removeEventListener(mUserListListener);
        }
        if (mMessListListener != null) {
            mMessListRef.removeEventListener(mMessListListener);
        }
    }

    private String usernameOfCurrentUser() {
        String email = LoginActivity.mAuth.getCurrentUser().getEmail();
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private void addControls() {
        mLvUserList = (ListView) findViewById(R.id.lvUserList);
        mListUsername = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, mListUsername);
        mLvUserList.setAdapter(mAdapter);

        mLvMessList = (ListView) findViewById(R.id.lvMessList);
        mMessList = new ArrayList<>();
        mAdapterMess = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, mMessList);
        mLvMessList.setAdapter(mAdapterMess);

        mEdtMess = (EditText) findViewById(R.id.edtMess);
        mBtnSend = (Button) findViewById(R.id.btnSend);
        mBtnLogOut = (Button) findViewById(R.id.btnLogout);
    }

    private void addEvents() {
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeMessage(usernameOfCurrentUser(),mEdtMess.getText().toString().trim());
            }
        });

        mBtnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.mAuth.getInstance().signOut();
                Intent iLogin = new Intent(MainActivity.this, LoginActivity.class);
                Toast.makeText(MainActivity.this, "Logout!!!", Toast.LENGTH_SHORT).show();
                startActivity(iLogin);
                finish();
            }
        });
    }

    private void writeMessage(String author, String text){
        if (text != null && !text.isEmpty()) {
            String key = mMessListRef.push().getKey();
            Mess mess = new Mess(author, text);
            Map<String,Object> messValue = mess.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/mess_list/chung/" + key, messValue);
            childUpdates.put("/mess_list/rieng/" + author + "/" + key, messValue);
            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
            mEdtMess.setText("");
        }else{
            Toast.makeText(this, "Ban chua nhap gi!", Toast.LENGTH_SHORT).show();
        }
    }
}
