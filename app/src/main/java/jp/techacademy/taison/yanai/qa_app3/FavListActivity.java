package jp.techacademy.taison.yanai.qa_app3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FavListActivity extends AppCompatActivity {


    private DatabaseReference mDatabaseReference;
    private DatabaseReference mGenreRef;
    private ListView mListView;
    private ArrayList<Question> mQuestionArrayList;
    private QuestionsListAdapter mAdapter;


    //ここからお気に入りの一覧
    private ArrayList<String> favoriteList;
    DatabaseReference favoriteRef;
    private ChildEventListener mEventListenerFav = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            /*HashMap map = (HashMap) dataSnapshot.getValue();
            String questionid = (String) map.get("uuid");//データ構造が違かった
            favoriteList.add(questionid);*/
            String questionid = (String) dataSnapshot.getValue();
            favoriteList.add(questionid);

        }
        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    //ここまでお気に入り





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_list);


        //お気に入り一覧
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();

        favoriteList = new ArrayList<String>();
        favoriteList.clear();

        favoriteRef = dataBaseReference.child(Const.FavPATH).child(String.valueOf(user.getUid()));
        favoriteRef.addChildEventListener(mEventListenerFav);
                    /* 今、追加していただいたコードは、本来はユーザーがログインした後で追加するものですが
                    正しく理解して頂くために、onCreateに書いていただきましたので、その辺りはコメントとして書いておいてくださいね！*/
        //ここまで


        // Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // ListViewの準備
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new QuestionsListAdapter(this);
        mQuestionArrayList = new ArrayList<Question>();
        mAdapter.notifyDataSetChanged();
        // --- ここまで追加する ---
    }
}
