package com.example.piusin.event.OnBoardingPackage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.piusin.event.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewHelp extends Fragment {
    View view;
    private WebView webView;

    public WebViewHelp() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.webview_layout, container, false);
        webView = view.findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(HelpUrls.home_url);
        return view;
    }

}
