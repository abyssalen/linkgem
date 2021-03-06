package com.github.shaigem.linkgem.ui.events;

import com.github.shaigem.linkgem.model.item.Item;

/**
 * Request for deleting a folder.
 *
 * @author Ronnie Tran
 */
public class DeleteFolderRequest {

    private Item itemToDelete;

    public DeleteFolderRequest(Item itemToDelete) {
        this.itemToDelete = itemToDelete;
    }

    public Item getItemToDelete() {
        return itemToDelete;
    }
}
