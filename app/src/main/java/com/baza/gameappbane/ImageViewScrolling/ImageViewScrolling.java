package com.baza.gameappbane.ImageViewScrolling;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baza.gameappbane.R;

public class ImageViewScrolling extends FrameLayout {

    private static int ANIMATION_DUR = 150;
    ImageView current_image, next_image;

    int last_result=0, old_value=0;

    IEventEnd eventEnd;

    public void setEventEnd(IEventEnd eventEnd) {
        this.eventEnd = eventEnd;
    }

    public ImageViewScrolling(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ImageViewScrolling(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.image_view_scrolling, this);
        current_image = getRootView().findViewById(R.id.current_image);
        next_image = getRootView().findViewById(R.id.next_image);

        next_image.setTranslationY(getHeight());
    }

    public void setValueRandom(final int image, final int rotate_count){
        current_image.animate().translationY(-getHeight()).setDuration(ANIMATION_DUR).start();
        next_image.setTranslationY(next_image.getHeight());
        next_image.animate().translationY(0)
                .setDuration(ANIMATION_DUR)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setImage(current_image, old_value%9); //coz we have 6 images, so we will mod for 6
                        current_image.setTranslationY(0);
                        if(old_value != rotate_count){
                            setValueRandom(image, rotate_count);
                            old_value++;
                        }
                        else {
                            last_result = 0;
                            old_value = 0;
                            setImage(next_image, image);
                            eventEnd.eventEnd(image%9, rotate_count);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

    private void setImage(ImageView image_view, int value) {
        if(value == Util.BOTTOMGREEN)
            image_view.setImageResource(R.drawable.bottomgreenw);
        else if(value == Util.MIDDLEGREEN)
            image_view.setImageResource(R.drawable.middlegreenw);
        else if(value == Util.TOPGREEN)
            image_view.setImageResource(R.drawable.topgreenw);
        else if(value == Util.BOTTOMYELLOW)
            image_view.setImageResource(R.drawable.bottomyelloww);
        else if(value == Util.MIDDLEYELLOW)
            image_view.setImageResource(R.drawable.middleyelloww);
        else if(value == Util.TOPYELLOW)
            image_view.setImageResource(R.drawable.topyelloww);
        else if(value == Util.BOTTOMPURPLE)
            image_view.setImageResource(R.drawable.bottompurplew);
        else if(value == Util.MIDDLEPURPLE)
            image_view.setImageResource(R.drawable.middlepurplew);
        else
            image_view.setImageResource(R.drawable.toppurplew);


        //Set tag for image for use to compare result
        image_view.setTag(value);
        last_result = value;
    }

    public int getValue(){
        return Integer.parseInt(next_image.getTag().toString());
    }
}
