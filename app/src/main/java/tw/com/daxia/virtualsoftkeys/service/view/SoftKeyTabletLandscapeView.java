package tw.com.daxia.virtualsoftkeys.service.view;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import tw.com.daxia.virtualsoftkeys.R;
import tw.com.daxia.virtualsoftkeys.common.SPFManager;
import tw.com.daxia.virtualsoftkeys.common.ScreenHepler;
import tw.com.daxia.virtualsoftkeys.common.ThemeHelper;
import tw.com.daxia.virtualsoftkeys.service.ServiceFloating;

/**
 * Created by daxia on 2017/5/3.
 */

public class SoftKeyTabletLandscapeView extends SoftKeyView {


    public SoftKeyTabletLandscapeView(ServiceFloating accessibilityService) {
        super(accessibilityService);
    }


    @Override
    void initBaseView() {
        LayoutInflater li = LayoutInflater.from(accessibilityService);
        this.baseView = li.inflate(R.layout.navigation_bar_t_l, null, true);
    }

    @Override
    void initBaseViewTheme() {
        this.softkeyBarHeight = ScreenHepler.dpToPixel(accessibilityService.getResources(), 48);
        int backgroundColor = SPFManager.getSoftKeyBarBgGolor(accessibilityService);
        this.baseView.setBackgroundColor(backgroundColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Set button
            int pressColor;
            if (ThemeHelper.isColorDark(backgroundColor)) {
                pressColor = Color.WHITE;
            } else {
                pressColor = Color.GRAY;
            }
            this.IB_button_home.setBackground(ThemeHelper.getPressedColorRippleDrawable(pressColor));
            this.IB_button_end.setBackground(ThemeHelper.getPressedColorRippleDrawable(pressColor));
            this.IB_button_start.setBackground(ThemeHelper.getPressedColorRippleDrawable(pressColor));
        } else {
            this.IB_button_home.setBackgroundResource(R.drawable.ic_sys_background);
            this.IB_button_start.setBackgroundResource(R.drawable.ic_sys_background);
            this.IB_button_end.setBackgroundResource(R.drawable.ic_sys_background);
        }
    }

    @Override
    void initImageButton() {
        if (reverseFunctionButton) {
            IB_button_start.setImageResource(R.drawable.ic_sysbar_recent);
            IB_button_end.setImageResource(R.drawable.ic_sysbar_back);
        } else {
            IB_button_start.setImageResource(R.drawable.ic_sysbar_back);
            IB_button_end.setImageResource(R.drawable.ic_sysbar_recent);
        }
    }

    @Override
    void initTouchEvent() {
        baseView.setOnTouchListener(new View.OnTouchListener() {
            private float firstSoftKeyTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                accessibilityService.hiddenSoftKeyBar(false);
                if (stylusOnlyMode) {
                    if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS) {
                        touchViewTouchEvent(event);
                    }
                } else {
                    touchViewTouchEvent(event);
                }
                return false;
            }

            private void touchViewTouchEvent(MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        firstSoftKeyTouchY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //Close the softKeyBar after swiping down more the 1/4 height
                        if ((event.getRawY() - firstSoftKeyTouchY) > (softkeyBarHeight / 4)) {
                            accessibilityService.hiddenSoftKeyBar(true);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                }
            }
        });
    }

    @Override
    public WindowManager.LayoutParams getLayoutParamsForLocation() {
        WindowManager.LayoutParams params;
        //Tablet Landscape is above the bottom screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    softkeyBarHeight,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    softkeyBarHeight,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        params.windowAnimations = android.R.style.Animation_InputMethod;
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.x = 0;
        params.y = 0;
        return params;
    }
}
