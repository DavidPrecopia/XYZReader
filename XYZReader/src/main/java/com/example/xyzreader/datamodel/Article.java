package com.example.xyzreader.datamodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.example.xyzreader.database.DatabaseContract;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = DatabaseContract.TABLE_NAME_ARTICLES)
public class Article{

	@PrimaryKey
	@ColumnInfo(name = DatabaseContract.COLUMN_NAME_ID)
	private int id;

	private String title;
	private String author;

	@SerializedName("published_date")
	@ColumnInfo(name = DatabaseContract.COLUMN_NAME_PUBLISHED_DATE)
	private String publishedDate;

	private String body;

	@SerializedName("thumb")
	private String thumbnailUrl;
	@SerializedName("photoUrl")
	private String photoUrl;


	public Article(int id, String title, String author, String publishedDate, String body, String thumbnailUrl, String photoUrl) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.publishedDate = publishedDate;
		this.body = body;
		this.thumbnailUrl = thumbnailUrl;
		this.photoUrl = photoUrl;
	}


	public int getId(){
		return id;
	}

	public String getTitle(){
		return title;
	}

	public String getAuthor(){
		return author;
	}

	public String getPublishedDate(){
		return publishedDate;
	}

	public String getBody(){
		return body;
	}

	public String getThumbnailUrl(){
		return thumbnailUrl;
	}

	public String getPhotoUrl(){
		return photoUrl;
	}


	@Override
 	public String toString(){
		return 
			"Article{" + 
			",thumbnailUrl = '" + thumbnailUrl + '\'' +
			",author = '" + author + '\'' + 
			",photoUrl = '" + photoUrl + '\'' +
			",id = '" + id + '\'' + 
			",title = '" + title + '\'' + 
			",body = '" + body + '\'' + 
			",published_date = '" + publishedDate + '\'' + 
			"}";
		}
}