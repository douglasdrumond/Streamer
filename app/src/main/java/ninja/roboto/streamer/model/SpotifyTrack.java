package ninja.roboto.streamer.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class SpotifyTrack implements Parcelable {
    private String mId;
    private String mName;
    private String mAlbumArtUrl;

    public SpotifyTrack(String id, String name, String albumArtUrl) {
        mId = id;
        mName = name;
        mAlbumArtUrl = albumArtUrl;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getAlbumArtUrl() {
        return mAlbumArtUrl;
    }

    protected SpotifyTrack(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        mAlbumArtUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mAlbumArtUrl);
    }

    public static final Parcelable.Creator<SpotifyTrack> CREATOR = new Parcelable.Creator<SpotifyTrack>() {
        @Override
        public SpotifyTrack createFromParcel(Parcel in) {
            return new SpotifyTrack(in);
        }

        @Override
        public SpotifyTrack[] newArray(int size) {
            return new SpotifyTrack[size];
        }
    };
}
