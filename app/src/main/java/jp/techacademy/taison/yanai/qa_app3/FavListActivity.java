package jp.techacademy.taison.yanai.qa_app3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class FavListActivity extends AppCompatActivity {


    private ListView mListView;
    //private ArrayList<Question> mQuestionArrayList;
    private QuestionsListAdapter mAdapter;


    //ここからお気に入りの一覧
    private ArrayList<String> favoriteList;
    DatabaseReference favoriteRef;


    private ChildEventListener mEventListenerFav = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            //HashMap map = (HashMap) dataSnapshot.getValue();



            String questionid = (String) dataSnapshot.getValue();



            /*for(Question question : favoriteList){
                    if(questionid.equals()){


                    }
            }*/





            /*String title = (String) map.get("title");
            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");*/

            favoriteList.add(questionid);

            // 質問のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
            //mQuestionArrayList.clear();
            //Adapterの初期化

            //MainActivity.mQuestionArrayList = new ArrayList<Question>();
            mAdapter.setQuestionArrayList(MainActivity.mQuestionArrayList);
            mListView.setAdapter(mAdapter);
            //mAdapter.notifyDataSetChanged();




        }


        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            /*
            HashMap map = (HashMap) dataSnapshot.getValue();

            // 変更があったQuestionを探す
            for (Question question: mQuestionArrayList) {
                if (dataSnapshot.getKey().equals(question.getQuestionUid())) {
                    // このアプリで変更がある可能性があるのは回答(Answer)のみ
                    question.getAnswers().clear();
                    HashMap answerMap = (HashMap) map.get("answers");
                    if (answerMap != null) {
                        for (Object key : answerMap.keySet()) {
                            HashMap temp = (HashMap) answerMap.get((String) key);
                            String answerBody = (String) temp.get("body");
                            String answerName = (String) temp.get("name");
                            String answerUid = (String) temp.get("uid");
                            Answer answer = new Answer(answerBody, answerName, answerUid, (String) key);
                            question.getAnswers().add(answer);
                        }
                    }

                    mAdapter.notifyDataSetChanged();
                }
            }*/

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




        //Adapterの初期化,ListViewも初期化
        mAdapter = new QuestionsListAdapter(FavListActivity.this);
        mListView = (ListView) findViewById(R.id.fListView);




        //お気に入り一覧
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();






        favoriteList = new ArrayList<String>();
        //favoriteList.clear();




        favoriteRef = dataBaseReference.child(Const.FavPATH).child(String.valueOf(user.getUid()));
        favoriteRef.addChildEventListener(mEventListenerFav);
                    /* 今、追加していただいたコードは、本来はユーザーがログインした後で追加するものですが
                    正しく理解して頂くために、onCreateに書いていただきましたので、その辺りはコメントとして書いておいてくださいね！*/
        //ここまで


        //Adapterの初期化,ListViewも初期化137,8->
        /*mAdapter = new QuestionsListAdapter(FavListActivity.this);
        mListView = (ListView) findViewById(R.id.fListView);
        MainActivity.mQuestionArrayList = new ArrayList<Question>();
        mAdapter.notifyDataSetChanged();*/


    }
}