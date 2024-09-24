package musicalreservation.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import musicalreservation.MusicalApplication;
import musicalreservation.domain.IteEdited;
import musicalreservation.domain.ItemCreated;
import musicalreservation.domain.ItemDeleted;

@Entity
@Table(name = "Item_table")
@Data
//<<< DDD / Aggregate Root
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Date startDate;

    private Date endDate;

    @PostPersist
    public void onPostPersist() {
        ItemCreated itemCreated = new ItemCreated(this);
        itemCreated.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate() {
        IteEdited iteEdited = new IteEdited(this);
        iteEdited.publishAfterCommit();
    }

    @PostRemove
    public void onPostRemove() {
        ItemDeleted itemDeleted = new ItemDeleted(this);
        itemDeleted.publishAfterCommit();
    }

    @PreRemove
    public void onPreRemove() {}

    public static ItemRepository repository() {
        ItemRepository itemRepository = MusicalApplication.applicationContext.getBean(
            ItemRepository.class
        );
        return itemRepository;
    }

    public void createItem() {
        //implement business logic here:

    }

    public void editItem() {
        //implement business logic here:

    }

    public void deleteItem() {
        //implement business logic here:

    }
}
//>>> DDD / Aggregate Root
