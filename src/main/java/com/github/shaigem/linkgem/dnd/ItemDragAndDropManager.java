package com.github.shaigem.linkgem.dnd;

import com.github.shaigem.linkgem.model.item.Item;
import javafx.collections.FXCollections;
import javafx.scene.input.DataFormat;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * A drag and drop manager which holds items that are being dragged.
 *
 * @author Ronnie Tran
 */
public class ItemDragAndDropManager {

    /**
     * The data format that will be used when dragging items.
     */
    public static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    private List<Item> items;

    @PostConstruct
    public void init() {
        items = FXCollections.observableArrayList();
    }

    /**
     * Adds a item to the drag list.
     *
     * @param item the item to add.
     */
    public void addItemToDragList(Item item) {
        items.add(item);
    }

    public Item getFirstItem() {
        return items.get(0);
    }

    public boolean hasItemsOnDragList() {
        return !items.isEmpty();
    }

    public void onDropComplete() {
        clearItems();
    }

    public void onDragFailure() {
        clearItems();
    }

    private void clearItems() {
        items.clear();
    }
}
