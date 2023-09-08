package com.example.myapplication.data.db.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks")
data class StockListing(
  @PrimaryKey(autoGenerate = true) val id : Int = 0,
  val symbol : String,
  val name : String,
  val exchange : String,
) : Parcelable {
  constructor(parcel: Parcel) : this(
    parcel.readInt(),
    parcel.readString()!!,
    parcel.readString()!!,
    parcel.readString()!!
  )
  override fun describeContents(): Int {
    return 0
  }

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeInt(id)
    dest.writeString(symbol)
    dest.writeString(name)
    dest.writeString(exchange)
  }

  companion object CREATOR : Parcelable.Creator<StockListing> {
    override fun createFromParcel(parcel: Parcel): StockListing {
      return StockListing(parcel)
    }

    override fun newArray(size: Int): Array<StockListing?> {
      return arrayOfNulls(size)
    }
  }
}
