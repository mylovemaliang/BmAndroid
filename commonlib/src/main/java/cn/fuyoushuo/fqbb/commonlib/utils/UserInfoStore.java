package cn.fuyoushuo.fqbb.commonlib.utils;

import java.io.Serializable;

public class UserInfoStore implements Serializable {

        private String sessionId;

        private String token;

        private String userId;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }