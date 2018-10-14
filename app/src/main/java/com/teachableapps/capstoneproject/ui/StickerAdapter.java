package com.teachableapps.capstoneproject.ui;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.teachableapps.capstoneproject.R;
import com.teachableapps.capstoneproject.data.database.Sticker;

import java.util.List;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerViewHolder> {
    private static final String LOG_TAG = StickerAdapter.class.getSimpleName();

    private List<Sticker> mStickerList;
    private final Context mContext;
    final private ListItemClickListener mOnClickListener;


    public interface ListItemClickListener {
        void OnListItemClick(int stickerPos);
    }

    public StickerAdapter(List<Sticker> stickers, ListItemClickListener listener, Context context) {

        mStickerList = stickers;

        mOnClickListener = listener;
        mContext = context;
    }

    @NonNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.sticker;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new StickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mStickerList == null ? 0 : mStickerList.size();
    }

    public void updateSticker(int pos, Sticker sticker) {
        mStickerList.set(pos, sticker);
        notifyDataSetChanged();
    }

    public class StickerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView stickerItemView;

        public StickerViewHolder(View itemView) {
            super(itemView);

            stickerItemView = itemView.findViewById(R.id.iv_sticker);
            stickerItemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            Sticker stickerItem = mStickerList.get(listIndex);
            stickerItemView = itemView.findViewById(R.id.iv_sticker);
            if(stickerItem.getImageSrc().length()>0) {
                int id = mContext.getResources().getIdentifier(stickerItem.getImageSrc(), "drawable", mContext.getPackageName());
                stickerItemView.setImageResource(id);

                // gray images - waiting for approval
                if(!stickerItem.isConfirmed()) {
                    ColorMatrix matrix = new ColorMatrix();
                    matrix.setSaturation(0);
                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                    stickerItemView.setColorFilter(filter);
                }

            } else {
                stickerItemView.setImageResource(R.drawable.border_image);
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            // pass position to activity for processing
            mOnClickListener.OnListItemClick(clickedPosition);
        }

    }
}
