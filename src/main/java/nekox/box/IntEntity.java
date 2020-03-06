package nekox.box;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class IntEntity {

    @Id(assignable = true) public long id;

    public int value;

    public IntEntity() {}

    public IntEntity(int id, int value) {
        this.id = id;
        this.value = value;
    }

}
