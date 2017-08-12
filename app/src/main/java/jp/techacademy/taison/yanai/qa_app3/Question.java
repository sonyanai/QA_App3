package jp.techacademy.taison.yanai.qa_app3;

/**
 * Created by taiso on 2017/08/04.
 */

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {

    private String mTitle;//Firebaseから取得したタイトル
    private String mBody;//Firebaseから取得した質問本文
    private String mName;//Firebaseから取得した質問者の名前
    private String mUid;//Firebaseから取得した質問者のUID
    private String mQuestionUid;//Firebaseから取得した質問のUID
    private int mGenre;//質問のジャンル
    private byte[] mBitmapArray;//Firebaseから取得した画像をbyte型の配列にしたもの
    private ArrayList<Answer> mAnswerArrayList;//Firebaseから取得した質問のモデルクラスであるAnswerのArrayList


    public Question(String title, String body, String name, String uid, String questionUid, int genre, byte[] bytes, ArrayList<Answer> answers) {
        mTitle = title;
        mBody = body;
        mName = name;
        mUid = uid;
        mQuestionUid = questionUid;
        mGenre = genre;
        mBitmapArray = bytes.clone();
        mAnswerArrayList = answers;
    }



    //Firebaseから質問のTitleを取得してmTitleに入れて返す
    public String getTitle() {
        return mTitle;
    }

    //Firebaseから質問のBodyを取得してmBodyに入れて返す
    public String getBody() {
        return mBody;
    }

    //Firebaseから質問のNameを取得してmNameに入れて返す
    public String getName() {
        return mName;
    }

    //あとで使うと思います。
    // Questionオブジェクトの質問IDを取得しないといけませんよね。
    //Firebaseから質問者のUidを取得してmUidに入れて返す
    public String getUid() {
        return mUid;
    }

    //Firebaseから質問の質問idを取得してmQuestionUidに入れて返す
    public String getQuestionUid() {
        return mQuestionUid;
    }

    //Firebaseから質問のGenreを取得してmGenreに入れて返す
    public int getGenre() {
        return mGenre;
    }

    //Firebaseから画像を取得してbyte型の配列にしたものをmBitmapArrayに入れて返す
    public byte[] getImageBytes() {
        return mBitmapArray;
    }

    //Firebaseから取得した質問のモデルクラスであるAnswerのArrayListを取得して
    // mAnswerArrayListに入れて返す
    ///モデルクラスって何？
    public ArrayList<Answer> getAnswers() {
        return mAnswerArrayList;
    }

}