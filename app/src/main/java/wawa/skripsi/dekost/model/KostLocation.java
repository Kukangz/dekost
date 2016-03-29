package wawa.skripsi.dekost.model;

/**
 * Created by Admin on 13/01/2016.
 */
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class KostLocation implements ClusterItem {
    public final String name;
    public final int profilePhoto;
    private final LatLng mPosition;
    public final int id;

    public KostLocation(int id, LatLng position, String name, int pictureResource) {
        this.name = name;
        profilePhoto = pictureResource;
        mPosition = position;
        this.id = id;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
