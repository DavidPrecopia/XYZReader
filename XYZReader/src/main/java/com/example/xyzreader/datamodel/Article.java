package com.example.xyzreader.datamodel;

import com.google.gson.annotations.SerializedName;

public class Article{

	private int id;

	private String title;
	private String author;
	@SerializedName("published_date")
	private String publishedDate;

	private String body;

	@SerializedName("thumb")
	private String thumbnail;
	@SerializedName("photo")
	private String photo;


	public Article(int id, String title, String author, String publishedDate, String body, String thumbnail, String photo) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.publishedDate = publishedDate;
		this.body = body;
		this.thumbnail = thumbnail;
		this.photo = photo;
	}


	public String getThumbnail(){
		return thumbnail;
	}

	public String getAuthor(){
		return author;
	}

	public String getPhoto(){
		return photo;
	}

	public int getId(){
		return id;
	}

	public String getTitle(){
		return title;
	}

	public String getBody(){
		return body;
	}

	public String getPublishedDate(){
		return publishedDate;
	}


	@Override
 	public String toString(){
		return 
			"Article{" + 
			",thumbnail = '" + thumbnail + '\'' +
			",author = '" + author + '\'' + 
			",photo = '" + photo + '\'' + 
			",id = '" + id + '\'' + 
			",title = '" + title + '\'' + 
			",body = '" + body + '\'' + 
			",published_date = '" + publishedDate + '\'' + 
			"}";
		}
}