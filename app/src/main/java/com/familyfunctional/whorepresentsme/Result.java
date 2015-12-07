package com.familyfunctional.whorepresentsme;


import java.util.ArrayList;
import java.util.List;

public class Result {
    public List<Member> results = new ArrayList<>();

    public Result() {
        results = new ArrayList<Member>();
    }

    public Result(List<Member> results) {
        this.results = results;
    }

    public List<Member> getResults() {
        return results;
    }

    public void setResults(List<Member> results) {
        this.results = results;
    }
}
