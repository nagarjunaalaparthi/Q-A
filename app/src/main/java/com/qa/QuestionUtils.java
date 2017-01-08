package com.qa;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Arjun on 1/8/2017.
 */

public class QuestionUtils {

    public static List<Question> getQuestions(Context context){
        Type collectionType = new TypeToken<List<Question>>(){}.getType();
        List<Question> questions = new Gson().fromJson(
                loadJSONFromAsset(context), collectionType);

        return questions;
    }

    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("questions.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
