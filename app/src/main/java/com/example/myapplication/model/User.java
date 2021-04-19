package com.example.myapplication.model;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.Serializable;

//@lombok.Data
public class User implements Serializable {


    /**
     * id : 1755
     * userId : 1578919786
     * name : 、流年伊人为谁笑
     * avatar : https://pipijoke.oss-cn-hangzhou.aliyuncs.com/CF921344169F71D572FCDF6D7A31CCE7.jpg
     * description : 小朋友,你是否有很多问号
     * likeCount : 9
     * topCommentCount : 10
     * followCount : 123
     * followerCount : 69
     * qqOpenId : FE41683AD4ECF91B7736CA9DB8104A5C
     * expires_time : 1596726031266
     * score : 1000
     * historyCount : 3573
     * commentCount : 568
     * favoriteCount : 23
     * feedCount : 10
     * hasFollow : true
     */

    public int id;
    public int userId;
    public String name;
    public String avatar;
    public String description;
    public int likeCount;
    public int topCommentCount;
    public int followCount;
    public int followerCount;
    public String qqOpenId;
    public long expires_time;
    public int score;
    public int historyCount;
    public int commentCount;
    public int favoriteCount;
    public int feedCount;
    public boolean hasFollow;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !(obj instanceof User))
            return false;
        User newUser = (User) obj;
        return TextUtils.equals(name, newUser.name)
                && TextUtils.equals(avatar, newUser.avatar)
                && TextUtils.equals(description, newUser.description)
                && likeCount == newUser.likeCount
                && topCommentCount == newUser.topCommentCount
                && followCount == newUser.followCount
                && followerCount == newUser.followerCount
                && qqOpenId == newUser.qqOpenId
                && expires_time == newUser.expires_time
                && score == newUser.score
                && historyCount == newUser.historyCount
                && commentCount == newUser.commentCount
                && favoriteCount == newUser.favoriteCount
                && feedCount == newUser.feedCount
                && hasFollow == newUser.hasFollow;
    }
}
