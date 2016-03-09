package fr.gerdevstudio.runningapp;

import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by NasTV on 08/01/2016.
 */
public class UiUtils {

    public static void showFab(final FloatingActionButton fab) {
        // Measure the View
        fab.requestLayout();
        ViewTreeObserver vto = fab.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //You should be able to get the width and height over here.

                fab.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                fab.animate().cancel();

                // Prepare the View for the animation
                fab.setVisibility(View.VISIBLE);
                fab.setAlpha(0f);
                fab.setTranslationY(fab.getHeight() + 32);


                // Start the animation
                fab.setVisibility(View.VISIBLE);
                fab.animate().translationY(0)
                        .setInterpolator(new DecelerateInterpolator(2))
                        .alpha(1.0f)
                        .setDuration(500);
            }
        });
    }

    public static void hideFab(final FloatingActionButton fab) {
        fab.requestLayout();
        // Measure the View
        ViewTreeObserver vto = fab.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //You should be able to get the width and height over here.
                fab.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Start the animation
                fab.setTranslationY(0);

                fab.animate().translationY(fab.getHeight() + 32)
                        .setInterpolator(new AccelerateInterpolator(2))
                        .setDuration(500)
                        .alpha(0f)
                        .setDuration(300);
            }
        });
    }

    public static void appear(final View view, final int duration) {
        view.requestLayout();

        // Measure the View
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //You should be able to get the width and height over here.

                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Prepare the View for the animation
                view.setVisibility(View.VISIBLE);
                view.setAlpha(0f);
                view.setScaleY(0);
                view.setScaleX(0);

                // Start the animation
                view.animate().scaleX(1).scaleY(1)
                        .setInterpolator(new DecelerateInterpolator(2))
                        .alpha(1.0f)
                        .setDuration(duration);
            }
        });
    }
}

