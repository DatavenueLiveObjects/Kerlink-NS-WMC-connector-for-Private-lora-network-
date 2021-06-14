/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.kerlink;

public class KerlinkProperties {

    private String baseUrl;
    private String login;
    private String password;
    private int pageSize;
    private int loginInterval;
    private String kerlinkAccountName;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getKerlinkAccountName() {
        return kerlinkAccountName;
    }

    public void setKerlinkAccountName(String kerlinkAccountName) {
        this.kerlinkAccountName = kerlinkAccountName;
    }

    public int getLoginInterval() {
        return loginInterval;
    }

    public void setLoginInterval(int loginInterval) {
        this.loginInterval = loginInterval;
    }
}
