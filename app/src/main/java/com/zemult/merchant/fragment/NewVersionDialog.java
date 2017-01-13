package com.zemult.merchant.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_AppInfo;


public class NewVersionDialog extends DialogFragment {
    private M_AppInfo updateBean;
    private UpdateCallback updateCallback;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.updateBean = (M_AppInfo)args.getSerializable("appinfo");
    }

    public interface UpdateCallback {
        public void onUpload();

        public void onCancel();
    }

    public void setUpdateCallback(UpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_new_version, null);
        ListView lvDetial = (ListView) view.findViewById(R.id.lvDetial);
        TextView tvSize = (TextView) view.findViewById(R.id.tvSize);
        TextView tvVersion = (TextView) view.findViewById(R.id.tvVersion);
        String[] detials=updateBean.note.split("\\n");
        if(detials==null){
            detials[0]="暂无介绍";
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.itemview_update_detial, detials);
        lvDetial.setAdapter(adapter);
        tvSize.setText("大小:" + updateBean.fileSize);
        tvVersion.setText("版本:v" + updateBean.getVersion());
        builder.setView(view)
                .setTitle("有新的客户端")
                .setPositiveButton("朕准了",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (updateCallback != null) {
                                    updateCallback.onUpload();
                                }
                            }
                        })
                .setNegativeButton("就不要",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (updateCallback != null) {
                                    updateCallback.onCancel();
                                }
                            }
                        });
        return builder.create();
    }
}
