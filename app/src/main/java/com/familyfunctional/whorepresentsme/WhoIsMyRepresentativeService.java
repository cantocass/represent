package com.familyfunctional.whorepresentsme;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface WhoIsMyRepresentativeService {

    @GET("/getall_mems.php?output=json")
    Call<Result> getAllMembersByZip(@Query("zip") String zip);
}
