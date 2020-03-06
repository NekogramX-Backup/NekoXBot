package nekox.box;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

import java.util.ArrayList;

@Entity
public class ForwardEntity {

    @Id public long id;

    public long from;

    public long to;

    public int[] ignoreList;

}
