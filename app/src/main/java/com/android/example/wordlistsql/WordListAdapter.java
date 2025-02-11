package com.android.example.wordlistsql;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder> {

    class WordViewHolder extends RecyclerView.ViewHolder {
        public final TextView wordItemView;
        Button delete_button;
        Button edit_button;

        public WordViewHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.word);
            delete_button = itemView.findViewById(R.id.delete_button);
            edit_button = itemView.findViewById(R.id.edit_button);
        }
    }

    private static final String TAG = WordListAdapter.class.getSimpleName();
    public static final String EXTRA_ID = "ID";
    public static final String EXTRA_WORD = "WORD";
    public static final String EXTRA_POSITION = "POSITION";

    private final LayoutInflater mInflater;
    private Context mContext;
    private WordListOpenHelper mDB;

    public WordListAdapter(Context context, WordListOpenHelper db) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mDB = db;
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.wordlist_item, parent, false);
        return new WordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final WordViewHolder holder, int position) {
        WordItem current = mDB.query(position);
        holder.wordItemView.setText(current.getWord());

        holder.delete_button.setOnClickListener(new MyButtonOnClickListener(current.getId(), current.getWord()) {
            @Override
            public void onClick(View v) {
                int deleted = mDB.delete(id);
                if (deleted > 0) {
                    notifyItemRemoved(holder.getAdapterPosition());
                }
            }
        });

        holder.edit_button.setOnClickListener(new MyButtonOnClickListener(current.getId(), current.getWord()) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditWordActivity.class);
                intent.putExtra(EXTRA_ID, id);
                intent.putExtra(EXTRA_POSITION, holder.getAdapterPosition());
                intent.putExtra(EXTRA_WORD, word);
                ((Activity) mContext).startActivityForResult(intent, MainActivity.WORD_EDIT);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (int) mDB.count();
    }
}