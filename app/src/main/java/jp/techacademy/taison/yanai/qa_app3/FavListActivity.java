package jp.techacademy.taison.yanai.qa_app3;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
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
    private QuestionsListAdapter fAdapter;
    private ArrayList<Question> mFavList = new ArrayList<Question>();
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mGenreRef;
    //public static宣言だとstaticで共有してるから何回も加算される
    //public static ArrayList<Question> mQuestionArrayList = new ArrayList<Question>();
    //staticは便利だけど気を付けなきゃ気付けず苦しむ！！！
    private ArrayList<Question> mQuestionArrayList = new ArrayList<Question>();
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
            Log.d("debug", "uid"+uid );
            //String allQuestion = (String) map.get("mQuestionUid");
            //ここも同様だけどここでとってきた値は人間には意味不明な文字列で
            //この下のif文でそれを画像に変換している
            String imageString = (String) map.get("image");
            //宣言
            byte[] bytes;
            //文字列を画像に変換している
            //Log.d("debug", imageString);
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
            Question question = new Question(title, body, name, uid, dataSnapshot.getKey(), 0, bytes, answerArrayList);//mGenre->0
            Log.d("debug", "mQuestionArrayListのquestion = "+ question.getQuestionUid());
            //mQuestionArrayListに質問を追加する
            //mQuestionArrayList.add(question);//ここは修正


            /*log.dをするといことは、不要なコードを埋め込むことになり、
            それだけでエラーの元にもなります。
            しかも、もっとデータを見たくても、その時はまたlog.dを追加しなければなりません。
            最後には消さなければなりませんので、もっとも無駄なデバッグ方法だからです。
            勿論、非同期メソッド等を使用するような場合とか、余程特殊なものは別ですが。。。
            Log.d( "debug" , mQuestionArrayList );
            Log.d( "debug" , MainActivity.favList );
            Log.d( "debug" , mFavList );*/
            //ここで一致してるか確認す
            //ArrayList-Question   :   ArrayList-String        mFavList-Question
            //左側は、右側のリストから取り出したデータが入る変数を定義します
            //これはなんでもいいことの確認　size=1,2,3,4,5...増えていく
            //Log.d("debug", "MainActivity.favList.size() = " + MainActivity.favList.size());
            //Log.d("debug", "mQuestionArrayList.size() = " + mQuestionArrayList.size());
            //Firebase から質問を一つ一つ受信している
            //Question型の変数matchFavにmQuestionArrayListの中身を一つずついれる
            /*for( Question matchFav : mQuestionArrayList){
                //Log.d("debug", "mQuestionArrayList = " + matchFav.getQuestionUid());
                //String型のfavMatchにMainActivity.favListの中身を一つずついれる
                for(String favMatch : MainActivity.favList){
                    //Log.d("debug", "MainActivity.favList = " + favMatch);
                    //matchFavの質問id(String型)とfavMatch(String型)が同じとき
                    if(matchFav.getQuestionUid().equals(favMatch)){
                        if (mFavList.indexOf(matchFav) == -1){
                            //indexOf(matchFav) == -1の時リストに入っていないから
                            //質問(Question型)をmFavList(Question型)に追加する
                            mFavList.add(matchFav);
                        }
                    }
                }
            }*/
            for (String favMatch : MainActivity.favList) {
                if (question.getQuestionUid().equals(favMatch)) {
                    if (mFavList.indexOf(question) == -1) {
                        mFavList.add(question);
                    }
                }
            }

            //アダプタが内部で管理しているデータに変更が生じた後に、
            // アダプターを通じて ListView に再描画を促しているmQuestionArrayList という変数に
            // 入ったリストのインスタンスが入れ替わった場合、あるいは
            // mQuestionArrayList というインスタンスは同じだけど、そのリストの「中身」のデータに
            // 変化が生じる度に行います。
            fAdapter.notifyDataSetChanged();

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

                    fAdapter.notifyDataSetChanged();
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







    //ここまでお気に入り
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_list);
        //Adapterの初期化,ListViewも初期化
        //お気に入り一覧

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();


        //favoriteListというArrayListを作る
        //favoriteList = new ArrayList<String>();
        //mAdapterの作成
        fAdapter = new QuestionsListAdapter(FavListActivity.this);
        //mQuestionArrayList という変数に入ったインスタンスが入れ替わる度に行います。
        fAdapter.setQuestionArrayList(mFavList);


        //mListViewの作成
        mListView = (ListView) findViewById(R.id.fListView);
        //onCreate() の中で一回だけやれば良い処理です。
        mListView.setAdapter(fAdapter);






        /*for(int Genre =1; Genre<5; Genre++){
            mGenreRef = mDatabaseReference.child(Const.ContentsPATH).child(String.valueOf(mGenre));
            mGenre = Genre;
            Log.d("debug", "mGenre = "+ mGenre);
            mGenreRef.addChildEventListener(mEventListener);
        }*/


        for(int genre = 1; genre < 5; genre++) {
            mGenreRef = mDatabaseReference.child(Const.ContentsPATH).child(String.valueOf(genre));
            mGenreRef.addChildEventListener(mEventListener);
        }
    }
}



