package com.rulaibao.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rulaibao.R;
import com.rulaibao.activity.SettingActivity;
import com.rulaibao.activity.WebActivity;
import com.rulaibao.common.Urls;

import java.util.ArrayList;

/**
 * 首页协议弹框 dialog
 */
public class showAgressmentSureDialog extends Dialog implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

    private Context mContext;
    private LayoutInflater inflater;
    private LayoutParams lp;
    private int percentageH = 4;
    private int percentageW = 8;
    private TextView txtConfirm = null;
    private TextView txtCancel = null;
    private TextView txtInfo = null;
    private TextView tvUnbindInfo2 = null;
    private TextView tv_title = null;
    private ImageView iv_delete;
    private String info = null;

    ArrayList<OnCancelListener> m_arrCancelListeners = new ArrayList<OnCancelListener>();
    ArrayList<OnDismissListener> m_arrDismissListeners = new ArrayList<OnDismissListener>();
    private OnExitChanged onChanged = null;

    public showAgressmentSureDialog(Context context, OnExitChanged onChanged, String info) {
        super(context, R.style.Dialog);
        this.mContext = context;
        this.onChanged = onChanged;
        this.info = info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.dialog_show_agressment, null);
        setContentView(mView);
        // 设置window属性
        lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.dimAmount = 0.6f; // 去背景遮盖
        lp.alpha = 1.0f;
        int[] wh = initWithScreenWidthAndHeight(mContext);
        lp.width = wh[0] - wh[0] / percentageW;
        lp.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);

        setCanceledOnTouchOutside(false);
        setOnDismissListener(this);
        setOnCancelListener(this);

        initView(mView);
    }

    private void initView(View mView) {
        iv_delete = (ImageView) mView.findViewById(R.id.iv_delete);
        txtConfirm = (TextView) mView.findViewById(R.id.dialog_confirm);
        txtCancel = (TextView) mView.findViewById(R.id.dialog_cancel);
        txtInfo = (TextView) mView.findViewById(R.id.tv_unbind_info);
        tvUnbindInfo2 = (TextView) mView.findViewById(R.id.tv_unbind_info_2);
        tv_title = (TextView) mView.findViewById(R.id.tv_title);
        txtInfo.setText(mContext.getString(R.string.agressment_info_sure));

        iv_delete.setOnClickListener(cancelListener);
        txtConfirm.setOnClickListener(confirmListener);
        txtCancel.setOnClickListener(cancelListener);

        tv_title.setText("温馨提示");

        SpannableString agreement = new SpannableString("如不同意《如来保会员服务协议》和《隐私政策》，很遗憾我们将无法提供服务。");
        agreement.setSpan(new MyClickableSpan("《如来保会员服务协议》"), 4, 15, SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
        agreement.setSpan(new MyClickableSpan("《隐私政策》"), 16, 22, SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
        tvUnbindInfo2.setText(agreement);
        tvUnbindInfo2.setMovementMethod(LinkMovementMethod.getInstance());
    }

    class MyClickableSpan extends ClickableSpan {

        private String text;

        public MyClickableSpan(String text) {
            this.text = text;
        }

        @Override
        public void onClick(@NonNull View view) {
            if("《如来保会员服务协议》".equals(text)){
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("type", WebActivity.WEB_TYPE_SIGN_AGREEMENT);
                intent.putExtra("title", mContext.getResources().getString(R.string.setting_service_agreement));
                intent.putExtra("url", Urls.URL_SERVICE_AGREEMENT);
                mContext.startActivity(intent);
            }else if("《隐私政策》".equals(text)){
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("type", WebActivity.WEB_TYPE_SIGN_AGREEMENT);
                intent.putExtra("title", mContext.getResources().getString(R.string.setting_service_private_agreement));
                intent.putExtra("url", Urls.URL_SERVICE_PRIVACY_AGREEMENT);
                mContext.startActivity(intent);
            }
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(Color.parseColor("#ff11bbee"));
            ds.setUnderlineText(false);
        }
    }

    private View.OnClickListener confirmListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onChanged.onConfirm();
            onDismiss();
        }

    };

    private View.OnClickListener cancelListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onChanged.onCancel();
            onDismiss();
        }
    };

    private void ondismiss() {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (m_arrDismissListeners != null) {
            for (int x = 0; x < m_arrDismissListeners.size(); x++)
                m_arrDismissListeners.get(x).onDismiss(dialog);
        }
        ondismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (m_arrCancelListeners != null) {
            for (int x = 0; x < m_arrDismissListeners.size(); x++)
                m_arrCancelListeners.get(x).onCancel(dialog);
        }
    }

    public void addListeners(OnCancelListener c, OnDismissListener d) {
        m_arrDismissListeners.add(d);
        m_arrCancelListeners.add(c);
    }

    public void removeListeners(OnCancelListener c, OnDismissListener d) {
        m_arrDismissListeners.remove(d);
        m_arrCancelListeners.remove(c);
    }

    private void onDismiss() {
        if (this.isShowing()) {
            this.dismiss();
        }

    }

    /**
     * 获取当前window width,height
     *
     * @param context
     * @return
     */
    private static int[] initWithScreenWidthAndHeight(Context context) {
        int[] wh = new int[2];
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        wh[0] = dm.widthPixels;
        wh[1] = dm.heightPixels;
        return wh;
    }

    public interface OnExitChanged {
        public void onConfirm();

        public void onCancel();

    }

}