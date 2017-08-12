package jp.techacademy.taison.yanai.qa_app3;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
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

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private int mGenre = 0;

    // --- ここから ---
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mGenreRef;
    private DatabaseReference qIdRef;
    private ListView mListView;
    public static ArrayList<Question> mQuestionArrayList;
    private QuestionsListAdapter mAdapter;

    /*
    //ここからお気に入りの一覧
    private ArrayList<String> favoriteList;
    DatabaseReference favoriteRef;
    private ChildEventListener mEventListenerFav = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            /*HashMap map = (HashMap) dataSnapshot.getValue();
            String questionid = (String) map.get("uuid");//データ構造が違かった
            favoriteList.add(questionid);*/
            /*String questionid = (String) dataSnapshot.getValue();
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
    */


    //データに追加・変化があった時に受け取るリスナー
    //一番最初に更新したい！！
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
            //ここも同様だけどここでとってきた値は人間には意味不明な文字列で
            //この下のif文でそれを画像に変換している
            String imageString = (String) map.get("image");
            //宣言
            byte[] bytes;
            //文字列を画像に変換している
            if (imageString != null) {
                //文字列をBASE64エンコードというデータに変換する仕組みを使って画像データとして復元している
                //QuestionSendActivity.javaでやったことの逆！
                bytes = Base64.decode(imageString, Base64.DEFAULT);
            } else {
                bytes = new byte[0];
            }

            //新しいリストのanswerArrayListを宣言
            ArrayList<Answer> answerArrayList = new ArrayList<Answer>();
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
            //mQuestionArrayListに質問を追加する
            mQuestionArrayList.add(question);
            //アダプタが内部で管理しているデータに変更が生じた後に、
            // アダプターを通じて ListView に再描画を促している
            mAdapter.notifyDataSetChanged();


        }

        @Override
        //要素に変化があった時,今回は質問に対して回答が投稿された時に呼ばれる
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            //これは、Firebaseのデータ構造がkey-value形式になっているので、
            // getValue()でその形式のデータ本体(answers)を取り出しているという意味になります。
            HashMap map = (HashMap) dataSnapshot.getValue();

            // 変更があったQuestionを探す
            for (Question question : mQuestionArrayList) {
                if (dataSnapshot.getKey().equals(question.getQuestionUid())) {
                    // このアプリで変更がある可能性があるのは回答(Answer)のみだから
                    //質問に対する回答のみ再読み込みするために削除
                    question.getAnswers().clear();
                    //なんでまた？？
                    //再読み込み？
                    HashMap answerMap = (HashMap) map.get("answers");
                    if (answerMap != null) {
                        for (Object key : answerMap.keySet()) {
                            //3度目???
                            HashMap temp = (HashMap) answerMap.get((String) key);
                            //key-value形式のデータのkeyを指定して、(String型にして)データの中身を取っている
                            String answerBody = (String) temp.get("body");
                            String answerName = (String) temp.get("name");
                            String answerUid = (String) temp.get("uid");
                            //AnswerクラスのanswerにanswerBody, answerName, answerUid, (String) keyを渡す
                            Answer answer = new Answer(answerBody, answerName, answerUid, (String) key);
                            //質問に対する回答を取得してそこに回答を追加
                            //Firebase からやってきた回答を Question クラスのインスタンスに保管している
                            question.getAnswers().add(answer);
                        }
                    }
                    //アダプタが内部で管理しているデータに変更が生じた後に、
                    // アダプターを通じて ListView に再描画を促している
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
            //中身は？？メソッド自体にすでに意味がある？
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
    // --- ここまで追加する ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);



        //fabのリスナーの登録
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ジャンルを選択していないとどこの領域に保存すればいいかわからないからそれが前提
                // ジャンルを選択していない場合（mGenre == 0）はエラーを表示するだけ
                if (mGenre == 0) {
                    //スナップバーで通知
                    Snackbar.make(view, "ジャンルを選択して下さい", Snackbar.LENGTH_LONG).show();
                    //このreturnの必要性は？
                    //ジャンルを指定していない場合はそれ以上進みたくないからreturnで戻す
                    return;
                }


                // ログイン済みのユーザーを取得する
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {
                    // ログインしていなければログイン画面に遷移させる
                    //getApplicationContext()の意味は？
                    //アンドロイドのシステムが参照するための情報です。Androidは、
                    // Intent というクラスを処理する際、その Intent がどのアプリによって
                    // 作成されたものなのかを知りたいのです。なので、Intent にアプリの情報を
                    // 埋め込んでいく必要があるのですね
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    if(mGenre == 5){
                        Intent intent = new Intent(getApplicationContext(), FavListActivity.class);
                        startActivity(intent);
                    }else{
                        // ジャンルを渡して質問作成画面を起動する
                        Intent intent = new Intent(getApplicationContext(), QuestionSendActivity.class);
                        //ジャンルを渡す
                        intent.putExtra("genre", mGenre);
                        //質問作成画面へ
                        startActivity(intent);
                    }
                }
            }
        });


        // ナビゲーションドロワーの設定
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //この3行は何してんねん？？？
        //Drawer を操作したときのイベントを ActionBarDrawerToggle という部品に通知できるようにしています
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.app_name, R.string.app_name);
        //これは「イディオム」みたいなものですね。
        // Androidのドロワーを使うときは、こうしておくと良きに計らってくれるのです。
        // 207行目も同じように「イディオム」です。
        // toggle.syncState(); とすることによって、ドロワーの状態と
        // ActionBarDrawerToggle の状態が連動した状態になります。
        // Android のフレームワークを作った人たちが、
        // 必要としている処理を自分たちだけのプログラムの範囲ではできなかったので、
        // 各プログラマに協力して必要な設定をしてもらっているイメージです。
        // 各プログラマにわざわざ「イディオム」を書かせることを要求しているのは、
        // ちょっと出来が悪いフレームワークである気がしますが、
        // これは現状こうなってしまっているので
        // 、一種の「おまじない」のようなものだと思ってあきらめるしかありません。。。
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //ナビゲーションのみため
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //NavigationItemSelectedListenerをnavigationとしてセットしてる
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            //ドロワーでジャンルが選択された時に、Firebaseに対してそのジャンルの質問のデータの変化を受け取る
            //なんでboolean型なの？
            //onNavigationItemSelected() が呼ばれた時コード内で、クリックされたメニューの処理を
            // したかどうかを返す仕組みになっています。これもAndroid のフレームワークを
            // 作った人たちが決めた仕組みです
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_hobby) {
                    mToolbar.setTitle("趣味");
                    mGenre = 1;
                } else if (id == R.id.nav_life) {
                    mToolbar.setTitle("生活");
                    mGenre = 2;
                } else if (id == R.id.nav_health) {
                    mToolbar.setTitle("健康");
                    mGenre = 3;
                } else if (id == R.id.nav_compter) {
                    mToolbar.setTitle("コンピューター");
                    mGenre = 4;
                } else if(id == R.id.nav_fav){
                    //mToolbar.setTitle("お気に入り");
                    mGenre = 5;

                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                //選んだらドロワーを閉じる？GravityCompatはなに？
                //画面の左側の方向にドロワーを閉じるアニメーションを表示するということですね
                // 。LEFT ではなくて、 START という表現が用いられているのは
                // 端末によっては左側ではなくて、上側などを 「START」 の位置としているものも
                // あるからです。ただ、たいていの Android 端末では START といえば、「左側」を指しています。
                drawer.closeDrawer(GravityCompat.START);


                /*private ArrayList<String> favoriteList;
                private ChildEventListener mEventListenerFav = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        HashMap map = (HashMap) dataSnapshot.getValue();
                        String questionid = (String) map.get("uuid");
                        favoriteList.add(questionid);
                    }
                };*/


                // 質問のリストをクリアしてから再度Adapterにセットし、
                // AdapterをListViewにセットし直す
               /* mQuestionArrayList.clear();
                mAdapter.setQuestionArrayList(mQuestionArrayList);
                mListView.setAdapter(mAdapter);*/
                /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String mQuestionUid;
                int position = 0;
                Question qObject = mQuestionArrayList.get(position);
                mQuestionUid = qObject.getQuestionUid();*/





                if(mGenre == 5){
                    qIdRef = mDatabaseReference.child(Const.ContentsPATH);
                    qIdRef.addChildEventListener(mEventListener);
                    Intent intent = new Intent(getApplicationContext(), FavListActivity.class);
                    startActivity(intent);
                }else{
                    // 質問のリストをクリアしてから再度Adapterにセットし、
                    // AdapterをListViewにセットし直す
                    mQuestionArrayList.clear();
                    mAdapter.setQuestionArrayList(mQuestionArrayList);
                    mListView.setAdapter(mAdapter);
                    // 選択したジャンルにリスナーを登録する
                    if (mGenreRef != null) {
                        mGenreRef.removeEventListener(mEventListener);
                    }
                    //DatabaseReferenceでfirebase->contents領域(Firebaseに質問を保存するところ)->ジャンルと
                    // と進んだジャンル領域にmGenreRefをていぎしますよ
                    mGenreRef = mDatabaseReference.child(Const.ContentsPATH).child(String.valueOf(mGenre));
                    //Firebaseに対してそのジャンルの質問のデータの変化を受け取るように先ほど作成した
                    //一番最初に設定したやつ(一番上)
                    mGenreRef.addChildEventListener(mEventListener);
                }





                //boolean型だから
                //faulseだったらどうなるの？プログラムが何もしなかったと認識します。といっても特に変わったことが起きるわけではありません
                //メニューが選択された時にプログラム内で何らかの処理を行ったので true を返しています
                return true;
            }
        });




        // Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // ListViewの準備
        mListView = (ListView) findViewById(R.id.listView);
        //新しいQuestionsListAdapterをmAdapterとする
        mAdapter = new QuestionsListAdapter(this);
        //この行の意味は？
        mQuestionArrayList = new ArrayList<Question>();
        //アダプタが内部で管理しているデータに変更が生じた後に、
        // アダプターを通じて ListView に再描画を促している
        mAdapter.notifyDataSetChanged();

        //リスナー
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Questionのインスタンスを渡して質問詳細画面を起動する
                Intent intent = new Intent(getApplicationContext(), QuestionDetailActivity.class);
                //questionがQuestionのインスタンス
                //これをQuestionDetailActivity.classに渡す？
                intent.putExtra("question", mQuestionArrayList.get(position));
                startActivity(intent);
            }
        });


        /*
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
    }










    @Override
    //オプションメニュー
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //???
        return true;
    }

    @Override
    //ジャンルで選んだやつのid
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            //SettingActivity.classにgetApplicationContext()を渡す
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
            return true;
        }
        //これはなに？これは、スーパークラスの onOptionsItemSelected() を呼んでいるという意味になります。
        //MainActivity は AppCompatActivity を継承しています。
        // なので、ここで「スーパークラス」というのは、つまりAppCompatActivity ということになります。
        // AppCompatActivity は、onOptionsItemSelected() というメソッドを持っています。
        // MainActivity は、onOptionsItemSelected() というメソッドを「上書き」しています。
        // いわば、スーパークラスの処理を「ハック」してしまっているわけです。
        //MainActivity は onOptionsItemSelected() というメソッドでやりたいことをやるのですが、
        // スーパークラス側でもきっと何かやりたいことがあったでしょうから、
        // スーパークラスの  onOptionsItemSelected() を呼んであげて、
        // やろうとしていたことをさせてあげているんですね。
        //違う場面でもスーパークラスのメソッドを使いたい場合にはスーパークラス側でも
        // やらせてあげないといけない場合がある
        //抵の場合は、スーパークラスのメソッドを呼ぶ必要はありません
        return super.onOptionsItemSelected(item);
    }
}
