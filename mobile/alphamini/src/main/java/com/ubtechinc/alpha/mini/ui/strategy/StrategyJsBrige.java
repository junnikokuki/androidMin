package com.ubtechinc.alpha.mini.ui.strategy;

import android.webkit.JavascriptInterface;

/**
 * Created by junsheng.chen on 2018/6/8.
 */
public final class StrategyJsBrige {

    private IJsBridge mBridge;

    public StrategyJsBrige(IJsBridge bridge) {
        mBridge = bridge;
    }

    /*@JavascriptInterface
    public void showSource(String html) {
        getMetaMessage(html);
    }*/

    /*private void getMetaMessage(String html) {
        Document document = Jsoup.parse(html);
        Elements element = document.select("meta[name=homepage]");
        if (element.hasAttr("content")) {
            String content = element.get(0).attr("content");
            mBridge.onHomepageLoad(Boolean.valueOf(content));
        }

    }*/

    @JavascriptInterface
    public void makeSuggestions() {
        mBridge.makeSuggestion();
    }

    @JavascriptInterface
    public void contactService() {
        mBridge.contactService();
    }
}
