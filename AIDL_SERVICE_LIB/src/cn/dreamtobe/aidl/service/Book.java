package cn.dreamtobe.aidl.service;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

	private String bookName;
	private int bookPrice;

	public Book() {

	}

	public Book(String bookName, int bookPrice) {
		this.bookName = bookName;
		this.bookPrice = bookPrice;
	}

	public Book(Parcel parcel) {
		bookName = parcel.readString();
		bookPrice = parcel.readInt();
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public int getBookPrice() {
		return bookPrice;
	}

	public void setBookPrice(int bookPrice) {
		this.bookPrice = bookPrice;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(bookName);
		parcel.writeInt(bookPrice);
	}

	public static final Parcelable.Creator<Book> CREATOR = new Creator<Book>() {
		public Book createFromParcel(Parcel source) {

			return new Book(source);
		}

		public Book[] newArray(int size) {
			return new Book[size];
		}
	};

}
