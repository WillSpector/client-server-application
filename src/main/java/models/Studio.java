package models;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Класс Студия, содержит её название и адрес.
 */

public class Studio implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String studioName;
    private final String address;

    public Studio(String studioName, String address) {
        this.studioName = studioName;
        this.address = address;
    }


    public String getStudioName() {
        return studioName;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Studio name = " + studioName + "  " +
                "Studio address = " + address;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Studio studio = (Studio) obj;
        return studioName.equals(studio.studioName) && address.equals(studio.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studioName, address);
    }


}
