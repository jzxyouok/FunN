package github.funn.utils.instrumentation;

import com.facebook.drawee.view.SimpleDraweeView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.SimpleDraweeControllerBuilder;

import github.funn.utils.FrescoHelper;


/**
 * {@link SimpleDraweeView} with instrumentation.
 * 重写SimpleDraweeView方法，增加图片的状态提示
 */

public class InstrumentedDraweeView extends SimpleDraweeView implements Instrumented {
    private Instrumentation mInstrumentation;
    private ControllerListener<Object> mListener;

    public InstrumentedDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init();
    }

    public InstrumentedDraweeView(Context context) {
        super(context);
        init();
    }

    public InstrumentedDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InstrumentedDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mInstrumentation = new Instrumentation(this);
        mListener = new BaseControllerListener<Object>() {
            @Override
            public void onSubmit(String id, Object callerContext) {
                mInstrumentation.onStart();
            }

            @Override
            public void onFinalImageSet(String id, @Nullable Object imageInfo, @Nullable Animatable animatable) {
                mInstrumentation.onSuccess();
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                mInstrumentation.onFailure();
            }

            @Override
            public void onRelease(String id) {
                mInstrumentation.onCancellation();
            }
        };
    }

    @Override
    public void initInstrumentation(String tag, PerfListener perfListener) {
        mInstrumentation.init(tag, perfListener);
    }

    @SuppressLint("WrongCall")
    @Override
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        mInstrumentation.onDraw(canvas);
    }

    @Override
    public void setImageURI(Uri uri, @Nullable Object callerContext) {
        SimpleDraweeControllerBuilder controllerBuilder = FrescoHelper.getSimpleDraweeControllerBuilder(getControllerBuilder(), uri, callerContext, getController());
        if (controllerBuilder instanceof AbstractDraweeControllerBuilder) {
            ((AbstractDraweeControllerBuilder<?, ?, ?, ?>) controllerBuilder).setControllerListener(mListener);
        }
        setController(controllerBuilder.build());
    }

    public ControllerListener<Object> getListener() {
        return mListener;
    }
}
