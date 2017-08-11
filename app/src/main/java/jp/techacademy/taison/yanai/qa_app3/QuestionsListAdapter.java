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

import java.util.ArrayList;
//BaseAdapterクラスを引き継いだQuestionsListAdapterクラスを作成
public class QuestionsListAdapter extends BaseAdapter{
    //mLayoutInflaterを宣言，定義
    private LayoutInflater mLayoutInflater = null;
    //Questionを入れたArrayListであるmQuestionArrayListを定義
    private ArrayList<Question> mQuestionArrayList;

    //リスナーの登録
    public QuestionsListAdapter(Context context) {
        //???
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    //mQuestionArrayListに入っている質問の総数を取得
    public int getCount() {
        return mQuestionArrayList.size();
    }

    @Override
    //タップされたpositionのObject(質問)を取得？？
    public Object getItem(int position) {
        return mQuestionArrayList.get(position);
    }

    @Override
    //タップされた質問idのpositionを取得？？
    public long getItemId(int position) {
        return position;
    }

    @Override
    //リスト用に自分で作成したレイアウト？？
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            //convertView(適応されているレイアウトのこと？)になにも入ってなかった時の処理
            convertView = mLayoutInflater.inflate(R.layout.list_questions, parent, false);
        }

        TextView titleText = (TextView) convertView.findViewById(R.id.titleTextView);
        //titleTextにはmQuestionArrayListから取得したpositionのTitleを渡す？？
        titleText.setText(mQuestionArrayList.get(position).getTitle());


        TextView nameText = (TextView) convertView.findViewById(R.id.nameTextView);
        //nameTextにmQuestionArrayListから取得したpositionのNameを渡す？？
        nameText.setText(mQuestionArrayList.get(position).getName());

        TextView resText = (TextView) convertView.findViewById(R.id.resTextView);
        //resNumにmQuestionArrayListから取得したpositionのgetAnswersの数を渡す？？
        int resNum = mQuestionArrayList.get(position).getAnswers().size();
        //resNumをint型からString型に直してresTextに渡す
        resText.setText(String.valueOf(resNum));

        //byte[]のbytesにmQuestionArrayListから取得したpositionのImageBytesを取得して渡す
        byte[] bytes = mQuestionArrayList.get(position).getImageBytes();
        //nytesの文字数が0でなかった時の処理
        if (bytes.length != 0) {
            //なにしてん？
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length).copy(Bitmap.Config.ARGB_8888, true);
            //convertView.findViewByIdのconvertViewはimageViewに必要？？
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            //imageをimageViewに渡す
            imageView.setImageBitmap(image);
        }
        //どゆこと？
        return convertView;
    }
    //上で作ったやつをメソッドにしてどこからでも呼び出せるようにしている
    public void setQuestionArrayList(ArrayList<Question> questionArrayList) {
        //questionArrayListってどっからきたん？？
        mQuestionArrayList = questionArrayList;
    }
}
