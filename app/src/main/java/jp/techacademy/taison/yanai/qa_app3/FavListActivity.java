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
    //public static ArrayList<String> favList = new ArrayList<String>();
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mGenreRef;
    public static ArrayList<Question> mQuestionArrayList = new ArrayList<Question>();
    private ArrayList<Answer> answerArrayList;
    private int mGenre = 0;

    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        //要素(質問)が追加されたときに呼ばれるメソッド
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            //これは、Firebaseのデータ構造がkey-value形式になっているので、
            // getValue()でその形式のデータ本体を取り出しているという意味になります。
            HashMap map = (HashMap) dataSnapshot.getValue();
            //key-value形式のデータのkeyを指定して、それに対応するデータの中身を(String型にして)取っている
            String title = (String) map.get("title");
            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");
            //String allQuestion = (String) map.get("mQuestionUid");
            //ここも同様だけどここでとってきた値は人間には意味不明な文字列で
            //この下のif文でそれを画像に変換している
            String imageString = (String) map.get("image");
            //宣言
            byte[] bytes;
            //文字列を画像に変換している
            if (imageString != null) {
                //文字列をBASE64エンコードというデータに変換する仕組みを使って画像データとして復元している
                //QuestionSendActivity.javaでやったことの逆！
                //なんでBASE64とかいう面倒なことを行っているかというと、firebaseなので(key-valueのvalueに)文字列で格納したいからです。
                // BASE64というのはemailで添付ファイルを送る時に使ったりします
                bytes = Base64.decode(imageString, Base64.DEFAULT);
            } else {
                bytes = new byte[0];
            }



            //新しいリストのanswerArrayListを宣言
            answerArrayList = new ArrayList<Answer>();




            //これは、Firebaseのデータ構造がkey-value形式になっているので、
            // getValue()でその形式のデータ本体(answers)を取り出しているという意味になります。
            HashMap answerMap = (HashMap) map.get("answers");
            //答えがあるとき
            if (answerMap != null) {
                //answerMap.keySet()というリストデータのなかのObjectデータクラスのデータを
                // ひとつずつkeyに入れてfor文を回しているという処理ですね
                //なぜ？？？
                for (Object key : answerMap.keySet()) {
                    //なんで2度目？？
                    // Question1つに複数のAnswerが内包されるため、このような書き方となります。
                    HashMap temp = (HashMap) answerMap.get((String) key);
                    //key-value形式のデータのkeyを指定して、(String型にして)データの中身を取っている
                    String answerBody = (String) temp.get("body");
                    String answerName = (String) temp.get("name");
                    String answerUid = (String) temp.get("uid");
                    //AnswerクラスのanswerにanswerBody, answerName, answerUid, (String) keyを渡す
                    Answer answer = new Answer(answerBody, answerName, answerUid, (String) key);
                    //answerArrayListに追加
                    answerArrayList.add(answer);
                }
            }



            //Questionクラスのquestionにtitle, body, name,uid, dataSnapshot.getKey(),
            // mGenre, bytes, answerArrayLisを渡す
            Question question = new Question(title, body, name, uid, dataSnapshot.getKey(), mGenre, bytes, answerArrayList);


            //ここで一致してるか確認す
            for( String matchQuestion : MainActivity.favList){
                if(question.getQuestionUid().equals(MainActivity.favList)){
                    //mQuestionArrayListに質問を追加する
                    mQuestionArrayList.add(question);
                    //アダプタが内部で管理しているデータに変更が生じた後に、
                    // アダプターを通じて ListView に再描画を促している
                    mAdapter.notifyDataSetChanged();
                }
            }

            //mQuestionArrayList.clear();
            mAdapter.setQuestionArrayList(mQuestionArrayList);
            mListView.setAdapter(mAdapter);




            /*//Questionクラスのquestionにtitle, body, name,uid, dataSnapshot.getKey(),
            // mGenre, bytes, answerArrayLisを渡す
            Question question = new Question(title, body, name, uid, dataSnapshot.getKey(), mGenre, bytes, answerArrayList);
            //mQuestionArrayListに質問を追加する
            mQuestionArrayList.add(question);
            //アダプタが内部で管理しているデータに変更が生じた後に、
            // アダプターを通じて ListView に再描画を促している
            mAdapter.notifyDataSetChanged();*/


        }

        @Override
        //要素に変化があった時,今回は質問に対して回答が投稿された時に呼ばれる
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            //これは、Firebaseのデータ構造がkey-value形式になっているので、
            // getValue()でその形式のデータ本体(answers)を取り出しているという意味になります。
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
            }


        }


        //インターフェースで定義されたメソッドは必ず実装しなければならない
        // ルールになっているのですね。今回の場合、データが削除されるケースは
        // 想定していないので、このメソッドが呼ばれても何もする必要はないのですが、
        // とにかく  ChildEventListener インターフェースを実装したクラスでは
        // onChildRemoved() を実装しなくてはいけないのです。
        // 一見意味がないようにも見えるのですが、Java の文法上の規則なので、
        // 中身が空のメソッドを定義しているのですね

        @Override
        //リスト内のアイテムに対する変更がないかリッスンします。onChildAdded()や
        //onChildRemoved() と併用して、リストに対する変更をモニタリングします
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            //インタフェースを継承したクラスは、Override必須のメソッドを必ず書く必要があります。
            //これは、インタフェース側で「実装は継承したクラスで書きますからね」という
            // 宣言をしているからです。もちろん、中身は空でかまいません。
        }

        @Override
        //リストから削除されるアイテムがないかリッスンします。onChildAdded() や
        // onChildChanged() と併用して、リストに対する変更をモニタリングします。
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        //onCancelledは通信に失敗したり、データを読み書きするのに失敗したときに呼ばれるメソッド
        public void onCancelled(DatabaseError databaseError) {

        }

    };




    //mEventListenerと同期させるためにMainActivityに移動60くらい
    /*
    DatabaseReference favoriteRef;
    private ChildEventListener mEventListenerFav = new ChildEventListener() {
        @Override
        //FavRefにはいってるmQuestionUidを1つずつ表示している
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            String questionId = (String) dataSnapshot.getValue();
            favList.add(questionId);



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
    */






    //ここまでお気に入り
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_list);
        //Adapterの初期化,ListViewも初期化
        //お気に入り一覧

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        //favoriteListというArrayListを作る
        //favoriteList = new ArrayList<String>();
        //mAdapterの作成
        mAdapter = new QuestionsListAdapter(FavListActivity.this);
        //mListViewの作成
        mListView = (ListView) findViewById(R.id.fListView);
        //mAdapterに変化があったら通知する
        //mAdapter.notifyDataSetChanged();
        //favoriteList.clear();





        ///ここから
        /*
        //Favの中のuidにfavoriteRefという領域を作る
        favoriteRef = mDatabaseReference.child(Const.FavPATH).child(String.valueOf(user.getUid()));
        //監視対象(favoriteRef)の場所にaddChildEventListener()を呼ぶことで監視できる
        favoriteRef.addChildEventListener(mEventListenerFav);
        */
        ///ここまでMainActivity350







        for(int Genre =1; Genre<5; Genre++){
            mGenreRef = mDatabaseReference.child(Const.ContentsPATH).child(String.valueOf(mGenre));
            mGenre = Genre;
            mGenreRef.addChildEventListener(mEventListener);
        }


    }
}