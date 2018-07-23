package com.example.piusin.event.OnBoardingPackage;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.piusin.event.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewHelp extends Fragment {
    View view;
    private WebView webView;
    private  Bundle bundle;
    private String control;
    Context context;

    public WebViewHelp() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.webview_layout, container, false);
        webView = view.findViewById(R.id.webview);
        context = view.getContext();
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(HelpUrls.home_url);
        return view;
    }

}
