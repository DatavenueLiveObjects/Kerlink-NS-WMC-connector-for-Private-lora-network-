package com.orange.lo.sample.kerlink2lo.lo.model;

import java.util.Map;

public class CommandRequestValue {

    private String req;
    private Map<String, String> arg;

    public String getReq() {
        return req;
    }

    public void setReq(String req) {
        this.req = req;
    }

    public Map<String, String> getArg() {
        return arg;
    }

    public void setArg(Map<String, String> arg) {
        this.arg = arg;
    }

    @Override
    public String toString() {
        return "CommandRequestValue [req=" + req + ", arg=" + arg + "]";
    }
}
