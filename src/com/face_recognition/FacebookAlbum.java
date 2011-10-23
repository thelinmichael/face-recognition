package com.face_recognition;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a Facebook album.
 * 
 * Contains Facebook album id and the album name.
 * 
 * Objects are parcleable so that an album can be sent via an intent.
 * 
 */
public class FacebookAlbum implements Parcelable  {
	
	private String name;		
	private String id;
	
	public FacebookAlbum(String name, String id) {
		this.name = name;
		this.id   = id;
	}
	
	/* Set & get functionality */
	public String getName() { return this.name; }
	public void setName(String name) { this.name = name; }
	public String getId() { return this.id; }
	public void setId(String id) { this.id = id; }
	
	public String toString() {
		return this.name;
	}
	
	
	/* Parcelable interface stuff */
	public FacebookAlbum(Parcel in) {
		String[] data = new String[2];
			in.readStringArray(data);
				this.name = data[0];
				this.id   = data[1];
	}

	/* Part of the interface Parcelable */
	@Override
	public int describeContents() {
		return 0;
	}

	/* Part of the interface Parcelable */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { this.name, this.id});	
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public FacebookAlbum createFromParcel(Parcel in) {
			return new FacebookAlbum(in); 
		}
		
		public FacebookAlbum[] newArray(int size) {
			return new FacebookAlbum[size];
		}
	};
}
