package com.zyta.zflikz.model;

public class ConversationMessage {
    private String movieId;
    private String postDate;
    private String postMessage;
    private String postAuthor;
    private String postAuthorImageUrl;


    public ConversationMessage() {
    }

    public ConversationMessage(String movieId, String postDate, String postMessage, String postAuthor,String postAuthorImageUrl) {
        this.movieId = movieId;
        this.postDate = postDate;
        this.postMessage = postMessage;
        this.postAuthor = postAuthor;
        this.postAuthorImageUrl = postAuthorImageUrl;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostMessage() {
        return postMessage;
    }

    public void setPostMessage(String postMessage) {
        this.postMessage = postMessage;
    }

    public String getPostAuthor() {
        return postAuthor;
    }

    public void setPostAuthor(String postAuthor) {
        this.postAuthor = postAuthor;
    }

    public String getPostAuthorImageUrl() {
        return postAuthorImageUrl;
    }

    public void setPostAuthorImageUrl(String postAuthorImageUrl) {
        this.postAuthorImageUrl = postAuthorImageUrl;
    }
}
