package server.roomManager;

import java.util.Comparator;

public class NCRoomDescriptionNameComparator implements Comparator<NCRoomDescription> {
    @Override
    public int compare(NCRoomDescription a, NCRoomDescription b) {
        return a.roomName.compareTo(b.roomName);
    }
}