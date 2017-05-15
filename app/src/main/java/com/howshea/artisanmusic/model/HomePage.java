package com.howshea.artisanmusic.model;

import java.util.List;

/**
 * Created by haipo
 * on 2016/10/28.
 *
 */

public class HomePage {



//    {"creatives": [{
//        "url": "https://pic2.zhimg.com/v2-337d33a455bd5512f8445e1acbbbb15d.jpg",
//        "start_time": 1494832483,
//        "impression_tracks": ["https://sugar.zhihu.com/track?vs=1&ai=4301&ut=&cg=2&ts=1494832483.85&si=a7f78a326dd94dc2ad09bbd678fc3d74&lu=0&hn=ad-engine.ad-engine.ad1fb37b&at=impression&pf=PC&az=11&sg=38ff6d130b36d3a6c3d934cd193d7fe8"],
//        "type": 0,
//        "id": "4301"
//    }]}



    private List<CreativesBean> creatives;

    List<CreativesBean> getCreatives() {
        return creatives;
    }

    public void setCreatives(List<CreativesBean> creatives) {
        this.creatives = creatives;
    }

    public static class CreativesBean {
        /**
         * url : https://pic2.zhimg.com/v2-337d33a455bd5512f8445e1acbbbb15d.jpg
         * start_time : 1494832483
         * impression_tracks : ["https://sugar.zhihu.com/track?vs=1&ai=4301&ut=&cg=2&ts=1494832483.85&si=a7f78a326dd94dc2ad09bbd678fc3d74&lu=0&hn=ad-engine.ad-engine.ad1fb37b&at=impression&pf=PC&az=11&sg=38ff6d130b36d3a6c3d934cd193d7fe8"]
         * type : 0
         * id : 4301
         */

        private String url;
//        private int start_time;
//        private int type;
//        private String id;
//        private List<String> impression_tracks;

        public String getUrl() {
            return url;
        }
    }

}
