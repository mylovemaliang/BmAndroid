package cn.fuyoushuo.fqbb.commonlib.utils;

import java.io.Serializable;

public class SeartchPo implements Serializable {

        public SeartchPo() {
        }

        /**
         *  参考 SearchCondition
         */
        String searchType = "";

        /**
         * 搜索词
         */
        String q = "";

        public String getSearchType() {
            return searchType;
        }

        public void setSearchType(String searchType) {
            this.searchType = searchType;
        }

        public String getQ() {
            return q;
        }

        public void setQ(String q) {
            this.q = q;
        }
    }