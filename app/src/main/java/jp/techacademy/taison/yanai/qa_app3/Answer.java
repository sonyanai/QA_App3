package jp.techacademy.taison.yanai.qa_app3;

/**
 * Created by taiso on 2017/08/04.
 */


import java.io.Serializable;

public class Answer implements Serializable {
    private String mBody;//Firebaseから取得した回答本文
    private String mName;//Firebaseから取得した回答者の名前
    private String mUid;//Firebaseから取得した回答者のUID
    private String mAnswerUid;//Firebaseから取得した回答のUID

    public Answer(String body, String name, String uid, String answerUid) {
        mBody = body;
        mName = name;
        mUid = uid;
        mAnswerUid = answerUid;
    }

    //Firebaseから回答本文を取得してmBodyにいれて返す
    public String getBody() {
        return mBody;
    }

    //Firebaseから回答者の名前を取得してmNameにいれて返す
    public String getName() {
        return mName;
    }

    //今は使われていませんが、あとで使うと思います。
    // Questionオブジェクトの回答IDを取得しないといけませんよね。
    //Firebaseから回答者のUidを取得してmUidにいれて返す
    public String getUid() {
        return mUid;
    }

    //Firebaseから回答のUidを取得してmAnswerUidにいれて返す
    public String getAnswerUid() {
        return mAnswerUid;
    }
}
