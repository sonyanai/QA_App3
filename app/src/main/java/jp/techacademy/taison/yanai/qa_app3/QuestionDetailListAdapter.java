package jp.techacademy.taison.yanai.qa_app3;

/**
 * Created by taiso on 2017/08/04.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class QuestionDetailListAdapter extends BaseAdapter {
    //質問の時は0
    private final static int TYPE_QUESTION = 0;
    //回答の時は1
    private final static int TYPE_ANSWER = 1;

    private LayoutInflater mLayoutInflater = null;
    private Question mQustion;

    //Adapterですよ
    public QuestionDetailListAdapter(Context context, Question question) {
        //LayoutInflaterというAndroidの機能を使うために実行しています。
        // これを使うと、XMLのレイアウトからJavaで使える様にViewオブジェクトを生成してくれます。
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mQustion = question;
    }

    @Override
    //質問に対する回答の総数を取得して1加えたのを返す？？？
    public int getCount() {
        return 1 + mQustion.getAnswers().size();
    }

    @Override
    //どういう状況尾話？？？
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_QUESTION;
        } else {
            return TYPE_ANSWER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return mQustion;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (getItemViewType(position) == TYPE_QUESTION) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_question_detail, parent, false);
            }
            String body = mQustion.getBody();
            String name = mQustion.getName();

            TextView bodyTextView = (TextView) convertView.findViewById(R.id.bodyTextView);
            bodyTextView.setText(body);

            TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            nameTextView.setText(name);

            ////byte[]のbytesにmQuestionから取得したImageBytesを取得して渡す
            byte[] bytes = mQustion.getImageBytes();
            //bytesの文字数が0でなかった時の処理
            if (bytes.length != 0) {
                //byte配列からBitmapオブジェクトを生成している処理です
                //メソッドの意味や引数などはドキュメントを参考にしてください。
                //一旦はbyte配列からBitmapの作成にこれを使うのだと覚えておいて問題ないです。
                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length).copy(Bitmap.Config.ARGB_8888, true);
                //convertView.findViewByIdのconvertViewはimageViewに必要？？
                //convertViewには、前回returnしたViewオブジェクトが入っています。
                // つまり、最初の１回目はnullなので、その場合に新しくViewを生成(inflate)しています。
                ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                //imageをimageViewに渡す
                imageView.setImageBitmap(image);
            }
        } else {
            //convertViewには、前回returnしたViewオブジェクトが入っています。
            // つまり、最初の１回目はnullなので、その場合に新しくViewを生成(inflate)しています。
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_answer, parent, false);
            }

            //???
            Answer answer = mQustion.getAnswers().get(position - 1);
            //回答から取得したBody(回答の内容)をbodyに渡す
            String body = answer.getBody();
            //回答から取得した回答者の名前をnameに渡す
            String name = answer.getName();

            TextView bodyTextView = (TextView) convertView.findViewById(R.id.bodyTextView);
            bodyTextView.setText(body);

            TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            nameTextView.setText(name);
        }
        //convertViewがnullの場合に、生成したViewをconvertViewにいれているので返す必要があります
        return convertView;
    }
}