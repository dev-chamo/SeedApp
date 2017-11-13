package com.chamodev.seedapp.util;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Koo on 2016. 10. 26..
 */

public abstract class CustomCallback<R> implements Callback<R> {

    public abstract void onSuccess(JSONObject resultJSON);

    public abstract void onFailure(int errorCode, String errorMessage);

    public abstract void onThrowable(Throwable t);

    public abstract void onFinish();

    @Override
    public void onResponse(Call<R> call, Response<R> response) {
        if (response.isSuccessful()) {
            try {
                String result = ((ResponseBody) response.body()).string();
                JSONObject resultJSON = new JSONObject(result);

                if (!resultJSON.has("error")) {
                    onSuccess(resultJSON);
                } else {
                    int errorCode = resultJSON.getInt("error");
                    if (errorCode == 0) {
                        onSuccess(resultJSON);
                    } else {
                        String message = "Server Error";
                        if (resultJSON.has("msg")) {
                            message = resultJSON.getString("msg");
                        }
                        onFailure(errorCode, message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            onFailure(response.code(), response.errorBody().toString());
        }
        onFinish();
    }

    @Override
    public void onFailure(Call<R> call, Throwable t) {
        onThrowable(t);
        onFinish();
    }
}
