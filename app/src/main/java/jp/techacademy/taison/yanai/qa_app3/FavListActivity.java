package jp.techacademy.taison.yanai.qa_app3;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
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
    private QuestionsListAdapter mAdapter;
    private ArrayList<String> favoriteList;
    static ArrayList<Question> favList;
    private int mGenre;
    DatabaseReference favoriteRef;
    private ChildEventListener mEventListenerFav = new ChildEventListener() {
        @Override
        //FavReにはいってるmQuestionUidを1つずつ表示している
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            String questionid = (String) dataSnapshot.getValue();

            for(String favoriteQuestionId : favoriteList) {
                for (Question question : MainActivity.allQuestionsArrayList) {
                    if (favoriteQuestionId == question.getQuestionUid()) {
                        // お気に入りに含まれているIDの場合
                        HashMap<String, Object> map = (HashMap) dataSnapshot.getValue();
                        String title = (String) map.get("title");
                        String body = (String) map.get("body");
                        String name = (String) map.get("name");
                        String uid = (String) map.get("uid");
                        String imageString = (String) map.get("image");
                        byte[] bytes;
                        if (imageString != null) {
                            bytes = Base64.decode(imageString, Base64.DEFAULT);
                        } else {
                            bytes = new byte[0];
                        }

                        ArrayList<Answer> answerArrayList = new ArrayList<Answer>();
                        HashMap answerMap = (HashMap) map.get("answers");
                        if (answerMap != null) {
                            for (Object key : answerMap.keySet()) {
                                HashMap temp = (HashMap) answerMap.get((String) key);
                                String answerBody = (String) temp.get("body");
                                String answerName = (String) temp.get("name");
                                String answerUid = (String) temp.get("uid");
                                Answer answer = new Answer(answerBody, answerName, answerUid, (String) key);
                                answerArrayList.add(answer);
                            }
                        }
                        question = new Question(title, body, name, uid, dataSnapshot.getKey(), mGenre, bytes, answerArrayList);
                        favList.add(question);
                    }
                }
            }





            /*favoriteList.add(questionid);*/
            // 質問のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
            //mQuestionArrayList.clear();
            //Adapterの初期化
            mAdapter.setQuestionArrayList(MainActivity.allQuestionsArrayList);
            mListView.setAdapter(mAdapter);
            favoriteList.add(questionid);
            mAdapter.notifyDataSetChanged();
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
        //Adapterの初期化,ListViewも初期化
        //お気に入り一覧
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        //favoriteListというArrayListを作る
        favoriteList = new ArrayList<String>();
        //mAdapterの作成
        mAdapter = new QuestionsListAdapter(FavListActivity.this);
        //mListViewの作成
        mListView = (ListView) findViewById(R.id.fListView);
        //mAdapterに変化があったら通知する
        mAdapter.notifyDataSetChanged();
        //favoriteList.clear();
        DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
        //Favの中のuidにfavoriteRefという領域を作る
        favoriteRef = dataBaseReference.child(Const.FavPATH).child(String.valueOf(user.getUid()));
        //監視対象(favoriteRef)の場所にaddChildEventListener()を呼ぶことで監視できる
        favoriteRef.addChildEventListener(mEventListenerFav);
        //ここまで
    }
}