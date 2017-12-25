package com.taigo.waimai;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.taigo.waimai.common.NToast;

public class LoadDialog extends Dialog {

    /**
     * LoadDialog
     */
    private static LoadDialog loadDialog;
    /**
     * canNotCancel, the mDialogTextView dimiss or undimiss flag
     */
    private boolean canNotCancel;
    /**
     * if the mDialogTextView don't dimiss, what is the tips.
     */
    private String tipMsg;

    private TextView mShowMessage;

    /**
     * the LoadDialog constructor
     *
     * @param ctx          Context
     * @param canNotCancel boolean
     * @param tipMsg       String
     */
    public LoadDialog(final Context ctx, boolean canNotCancel, String tipMsg, boolean isTranslucent) {
        super(ctx);

        this.canNotCancel = canNotCancel;
        this.tipMsg = tipMsg;
        this.getContext().setTheme(android.R.style.Theme_InputMethod);
        setContentView(R.layout.layout_dialog_loading);

        if (!TextUtils.isEmpty(this.tipMsg)) {
            mShowMessage = (TextView) findViewById(R.id.show_message);
            mShowMessage.setText(this.tipMsg);
        }

        Window window = getWindow();
        WindowManager.LayoutParams attributesParams = window.getAttributes();
        //设置窗体背景透明度
        if(isTranslucent) {
            attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            attributesParams.dimAmount = 0.5f;
        }
        attributesParams.alpha=0.7f;//设置内容透明度
        window.setAttributes(attributesParams);

        window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (canNotCancel) {
                NToast.shortToast(getContext(), tipMsg);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * show the mDialogTextView
     *
     * @param context
     */
    public static void show(Context context) {
        show(context, null, false,false);
    }
    //可让背景有半透明黑色遮罩
    public static void show(Context context, boolean isTranslucent) {
        show(context, null, false,true);
    }

    /**
     * show the mDialogTextView
     *
     * @param context Context
     * @param message String
     */
    public static void show(Context context, String message) {
        show(context, message, false,false);
    }

    /**
     * show the mDialogTextView
     *
     * @param context  Context
     * @param message  String, show the message to user when isCancel is true.
     * @param isCancel boolean, true is can't dimiss，false is can dimiss
     */
    private static void show(Context context, String message, boolean isCancel, boolean isTranslucent) {
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        if (loadDialog != null && loadDialog.isShowing()) {
            return;
        }
        loadDialog = new LoadDialog(context, isCancel, message,isTranslucent);
        loadDialog.show();
    }

    /**
     * dismiss the mDialogTextView
     */
    public static void dismiss(Context context) {
        try {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing()) {
                    loadDialog = null;
                    return;
                }
            }

            if (loadDialog != null && loadDialog.isShowing()) {
                Context loadContext = loadDialog.getContext();
                if (loadContext != null && loadContext instanceof Activity) {
                    if (((Activity) loadContext).isFinishing()) {
                        loadDialog = null;
                        return;
                    }
                }
                loadDialog.dismiss();
                loadDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadDialog = null;
        }
    }
}