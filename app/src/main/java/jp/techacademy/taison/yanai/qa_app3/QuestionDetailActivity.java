package jp.techacademy.taison.yanai.qa_app3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Color.rgb;


public class QuestionDetailActivity extends AppCompatActivity {

    private ListView mListView;
    private Question mQuestion;
    private QuestionDetailListAdapter mAdapter;
    int position;
    Button mButton;





    //データベース内にmAnswerRef領域を定義
    private DatabaseReference mAnswerRef;
    //データの変更を受け取るリスナーの定義
    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        //質問が追加されたときの処理
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            //mapにdataSnapshot.getValue()をいれる<-どういうこと？
            //Firebaseのデータ構造がkey-value形式になっているので、
            // getValue()でその形式のデータ本体を取り出しているという意味
            HashMap map = (HashMap) dataSnapshot.getValue();

            //回答idを取得？
            String answerUid = dataSnapshot.getKey();

            //mQuestion.getAnswers()という質問データの中のAnswerデータクラスのデータを一つずつanswerに入れて回している
            for(Answer answer : mQuestion.getAnswers()) {
                // 同じAnswerUidのものが存在しているときは何もしない<-なんで？どういうとき？
                //onChildAddedというのは`mAnswerRef.addChildEventListener(mEventListener);` を実行したときにコールバックされるのですが、
                //回答１つにつき１回ずつ呼ばれます。
                //なので、回答のリストの中にすでに追加されたanswerがあれば重複されないように何もしないという保険的な実装をしています。

                if (answerUid.equals(answer.getAnswerUid())) {
                    return;
                    //何も返しません
                }
            }

            //key-value形式のデータのkeyを指定して、データの中身を取っているという意味
            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");

            //answerにbody, name, uid, answerUidをいれる
            Answer answer = new Answer(body, name, uid, answerUid);
            //質問に対するすべての回答を取得し，この回答を追加する
            mQuestion.getAnswers().add(answer);
            //回答に変化があったことをmAdapterに通知する
            mAdapter.notifyDataSetChanged();
        }

        @Override
        //リスト内のアイテムに対する変更がないかリッスンします。onChildAdded() や
        // onChildRemoved() と併用して、リストに対する変更をモニタリングします
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        //リストから削除されるアイテムがないかリッスンします。onChildAdded() や
        // onChildChanged() と併用して、リストに対する変更をモニタリングします。
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        //並べ替えリストの項目順に変更がないかリッスンします。 onChildMoved() イベントは常に、
        // （現在の order-by メソッドに基づく）並べ替え変更が原因の onChildChanged() イベントに後続します
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        //通信に失敗したり、データを読み書きするのに失敗したときに呼ばれるメソッド
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    //アプリ起動時に始まるメソッド
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);




        //ここで表示非表示
        //if(MainActivity.flag_login = false){
            //fav_button.setVisibility(Button.Gone);
        //}

        //現在ログイン中のユーザー(author)を取得してuserに渡す
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //もしログインしていなかったらlayoutファイルは2,ログインしていたらlayoutファイルは1をセットする
        /*if (MainActivity.flag_login == true){
        setContentView(R.layout.activity_question_detail);
        }else{
        setContentView(R.layout.activity_question_detail2);
        }*/



        // 渡ってきたQuestionのオブジェクトを保持する
        Bundle extras = getIntent().getExtras();
        mQuestion = (Question) extras.get("question");

        setTitle(mQuestion.getTitle());



        // ListViewの準備
        //UIのインスタンスをメンバ変数に保持する
        mListView = (ListView) findViewById(R.id.listView);
        //mAdapterにはmQuestionのデータをいれます
        mAdapter = new QuestionDetailListAdapter(this, mQuestion);
        //mListViewにmAdapterをセットする
        mListView.setAdapter(mAdapter);
        //データに変更があったことをmAdapterに通知する
        mAdapter.notifyDataSetChanged();


        //宣言　UIのインスタンスをメンバ変数に保持する　fabという名のFloatingActionButtonを作成
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //リスナーの登録
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ログイン済みのユーザーを取得する
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {
                    // ログインしていなければログイン画面に遷移させる
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    // Questionを渡して回答作成画面を起動する
                    // TODO:
                    //ApplicationContextを取得してAnswerSendActivity.classに送る
                    Intent intent = new Intent(getApplicationContext(), AnswerSendActivity.class);
                    //"question"というキーでmQuestionを呼べるようにした
                    intent.putExtra("question", mQuestion);
                    //回答画面に移ります
                    startActivity(intent);
                }
            }
        });







        //宣言　UIのインスタンスをメンバ変数に保持する
        mButton = (Button)findViewById(R.id.fav_button);
        if(isFav(mQuestion.getQuestionUid())){
            //mButtonの表示を「★」にする
            mButton.setBackgroundColor(rgb(0,200,100));

        } else {
            //mButtonの表示を「☆」にする
            mButton.setBackgroundColor(rgb(0,100,200));

        }

        //リスナーの登録
        mButton.setOnClickListener(new View.OnClickListener() {//ここの時点でmButtonがnullになっている
            @Override
            public void onClick(View v) {





                FirebaseAuth mAuth;
                DatabaseReference mDataBaseReference;
                //mQuestionArrayList = new ArrayList<Question>();
                // リストを作成，初期化
                mAuth = FirebaseAuth.getInstance();
                //authorを定義
                String mQuestionUid;
                //String型のmQuestionUidを宣言

                mDataBaseReference = FirebaseDatabase.getInstance().getReference();
                //authorの情報を取得
                FirebaseUser user = mAuth.getCurrentUser();
                //現在ログインしているアカウントauthorをuserとする
                if(isFav(mQuestion.getQuestionUid())){
                    //mButtonの表示を「★」にする
                    mButton.setBackgroundColor(rgb(0,100,200));
                    mQuestionUid = mQuestion.getQuestionUid();
                    DatabaseReference favRef = mDataBaseReference.child(Const.FavPATH).child(user.getUid()).child(mQuestionUid);

                    favRef.removeValue();
                } else {
                    //mButtonの表示を「☆」にする
                    mButton.setBackgroundColor(rgb(0,200,100));
                    //ここの3行でfirebaseに保存している
                    mQuestionUid = mQuestion.getQuestionUid();
                    DatabaseReference favRef = mDataBaseReference.child(Const.FavPATH).child(user.getUid());
                    favRef.push().setValue(mQuestionUid);
                }
                //mQuestionUid = mQuestion.getQuestionUid();
                /*
                //MainActivityでmQuestionArrayListをsta ticで宣言しているからMainActivity.mQuestionArrayListで
                //MainActivityで使ってるmQuestionArrayListをひっぱってこれる！
                Question qObject = MainActivity.mQuestionArrayList.get(position);//position取れてなくね？
                //すべての質問が入ってるmQuestionArrayListを使い今のpositionのobject(質問)を取得
                mQuestionUid = qObject.getQuestionUid();
                //そのobject(質問)の質問idを取得

                //この時点で、変数「mQuestion」にQuestionが設定されているから
                //147行目　intent.putExtra("question", mQuestion);ここで先に指定している

                ↓
                */




                //firebaseのデータ構造のトップから，Const.FavPATH -> user.getUid() -> mQuestion.getQuestionUid()とデータ構造ツリーを辿り，その位置を示すリファレンスを取得しています．
                //各定数あるいはメソッドはそれぞれ評価されてなんらかの値になっています．
                //Const.FavPATH = "fav"
                //user.getUid() = 1
                //mQuestion.getQuestionUid() = "abcde"

                //仮にこうならば，favRefは，"fav" -> "1" -> "abcde" と辿っていったfirebaseのデータを指し示しているわけです
                //DatabaseReference favRef = mDataBaseReference.child(Const.FavPATH).child(user.getUid());//Firebase 上の「フォルダ名」として使用している
                //child(Const.FavPATH)は、 Firebase のデータ階層に一つ「フォルダ」を作っています。そのフォルダの下に、
                // ユーザーIDでさらに「フォルダ」を作って、そのユーザーIDの「フォルダ」の下に
                // お気に入りの質問IDを入れ込んでいるのが現状ですね




                /*Map<String, String> data = new HashMap<String, String>();
                data.put("qid", mQuestionUid);
                //favRef.setValue(data);//一つしか保存できない
                favRef.push().setValue(data);
                //ユーザーIDの「フォルダ」の下にもう一階層「フォルダ」ができます
                // push() というメソッドはランダムな文字列で自動的にフォルダ名が生成します

                ↓お気に入りの質問idしか保存しない場合　　　　*/

                //favRef.push().setValue(mQuestionUid);



            }
        });
    }
    private boolean isFav(String mQuestionUid){
        boolean ret = false;
        if(MainActivity.favList!=null){
            for(int i=0; i<MainActivity.favList.size(); i++){
                if(MainActivity.favList.get(i).equals(mQuestionUid)){
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }

}