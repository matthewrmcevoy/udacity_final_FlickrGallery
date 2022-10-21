package com.mrm.android.flikrtest.api

import com.mrm.android.flikrtest.oauth.CurrentUser
import org.json.JSONObject

fun parseAPIPhotosJsonResult(jsonResult: JSONObject): ArrayList<APIPhoto>{
   // val flikrFeedJson = jsonResult.getJSONObject("items")
    val test = jsonResult.getJSONArray("items")

    val photoList = ArrayList<APIPhoto>()
   // val photoJsonArray = flikrFeedJson.getJSONArray("items")

    var tagsList: List<String> = listOf()

    for(i in 0 until test.length()) {
        val photoJson = test.getJSONObject(i)
        val title = photoJson.getString("title")
        val media = photoJson.getJSONObject("media")
            .getString("m")
        val dateTaken = photoJson.getString("date_taken")
        //remove time / timezone
        val date = dateTaken.substring(0,10)
        val authorFlikr = photoJson.getString("author")
        //Remove nobody@flickr.com(" and ") from author
        val author = authorFlikr.substring(20,authorFlikr.length-2)
        var tagsString = photoJson.getString("tags")
        if(tagsString.isEmpty()){
            tagsString= "N/A"
        }
        //Convert string of tags to a list of string tags
        tagsList = tagsString.trim().splitToSequence(' ').filter{it.isNotEmpty()}.toList()
//        val height = photoJson.getString("height").toInt()
//        val width = photoJson.getString("width").toInt()

        val apiPhoto = APIPhoto(title, media,date,author, tagsList, CurrentUser.userName)
        photoList.add(apiPhoto)
    }


    return photoList
}

