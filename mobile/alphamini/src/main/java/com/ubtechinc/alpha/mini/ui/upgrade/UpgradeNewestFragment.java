package com.ubtechinc.alpha.mini.ui.upgrade;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseFragment;

/**
 * Created by ubt on 2018/4/8.
 */

public class UpgradeNewestFragment extends BaseFragment {

    public static final String ARG_VERSION = "ARG_VERSION";

    private TextView currentVersionView;

    private String version = "V1.0";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upgrade_newest, null, false);
    }

    private void init() {
        currentVersionView = getActivity().findViewById(R.id.current_version);
        Bundle args = getArguments();
        if(args != null){
            version = args.getString(ARG_VERSION, "V1.0");
        }
        currentVersionView.setText(getString(R.string.upgrade_current_version, version));
    }

    public void setVersion(String version) {
        this.version = version;
        if(currentVersionView != null){
            currentVersionView.setText(getString(R.string.upgrade_current_version, version));
        }
    }
}
