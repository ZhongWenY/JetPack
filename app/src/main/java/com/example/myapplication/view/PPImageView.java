package com.example.myapplication.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.myapplication.utils.PixUtils;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class PPImageView extends AppCompatImageView {
    public PPImageView(@NonNull Context context) {
        super(context);
    }

    public PPImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PPImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageUrl(String imageUrl){
        setImageUrl(this,imageUrl,false);
    }

    @BindingAdapter(value = {"image_url", "isCircle"})
    public static void setImageUrl(PPImageView view,String imageUrl, boolean isCircle){
        RequestBuilder<Drawable> builder = Glide.with(view).load(imageUrl);
        if(isCircle){
            builder.transform(new CircleCrop());
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if(layoutParams !=null && layoutParams.width>0&&layoutParams.height>0){
            builder.override(layoutParams.width,layoutParams.height);
        }
        builder.into(view);
    }
    public void bindData(int widthPx, int heightPx, int marginLeft, String imageUrl){

        bindData(widthPx,heightPx,marginLeft,PixUtils.getScreenWidth(),PixUtils.getScreenHeight(),imageUrl);

    }
    public void bindData(int widthPx, int heightPx, int marginLeft, int maxWidth, int maxHeight, String imageUrl){

        if(widthPx<=0 || heightPx<=0){
            Glide.with(this).load(imageUrl).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    int height = resource.getIntrinsicHeight();
                    int width = resource.getIntrinsicWidth();
                     setSize(width, height, marginLeft, maxWidth, maxHeight);

                     setImageDrawable(resource);
                }
            });
            return;
        }

        setSize(widthPx, heightPx, marginLeft, maxWidth, maxHeight);
        setImageUrl(this,imageUrl,false);

    }

    private void setSize(int width, int height, int marginLeft, int maxWidth, int maxHeight) {
        int finalWidth,finalHeight;
        if(width>height){
            finalWidth = maxWidth;
            finalHeight = (int)(height/(width*1.0f/finalWidth));
        }else{
             finalHeight = maxHeight;
             finalWidth =(int)(width/(height*1.0/finalHeight));
        }

        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(finalWidth,finalHeight);
        params.leftMargin = height>width? PixUtils.dp2px(marginLeft) :0;
        setLayoutParams(params);
    }

    public void setBlurImageUrl(String coverUrl, int radius) {
        Glide.with(this).load(coverUrl).override(50)
                .transform(new BlurTransformation())
                .dontAnimate()
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        setBackground(resource);
                    }
                });
    }


}
