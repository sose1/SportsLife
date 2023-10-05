import android.os.Parcel
import android.os.Parcelable

class ParcelableLinkedHashMap<K, V>() : LinkedHashMap<K, V>(), Parcelable {

    constructor(parcel: Parcel) : this() {
        val size = parcel.readInt()
        for (i in 0 until size) {
            val key = parcel.readValue(null) as K
            val value = parcel.readValue(null) as V
            put(key, value)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(size)
        for ((key, value) in this) {
            parcel.writeValue(key)
            parcel.writeValue(value)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelableLinkedHashMap<String, String>> {
        override fun createFromParcel(parcel: Parcel): ParcelableLinkedHashMap<String, String> {
            return ParcelableLinkedHashMap(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableLinkedHashMap<String, String>?> {
            return arrayOfNulls(size)
        }
    }
}

class ParcelableMutableList<E>() :
    MutableList<ParcelableLinkedHashMap<String, String>> by mutableListOf(), Parcelable {

    constructor(parcel: Parcel) : this() {
        val size = parcel.readInt()
        for (i in 0 until size) {
            val map = parcel.readParcelable<ParcelableLinkedHashMap<String, String>>(
                ParcelableLinkedHashMap::class.java.classLoader
            )
            add(map!!)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(size)
        for (map in this) {
            parcel.writeParcelable(map, flags)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR :
        Parcelable.Creator<ParcelableMutableList<ParcelableLinkedHashMap<String, String>>> {
        override fun createFromParcel(parcel: Parcel): ParcelableMutableList<ParcelableLinkedHashMap<String, String>> {
            return ParcelableMutableList(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableMutableList<ParcelableLinkedHashMap<String, String>>?> {
            return arrayOfNulls(size)
        }
    }
}
