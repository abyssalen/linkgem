package com.github.shaigem.linkgem.ui.events;

import com.github.shaigem.linkgem.model.item.FolderItem;

/**
 * Request to open a folder in the explorer.
 *
 * @author Ronnie Tran
 */
public class OpenFolderInExplorerRequest {

    private FolderItem folder;

    public OpenFolderInExplorerRequest(FolderItem folder) {
        this.folder = folder;
    }

    public FolderItem getFolder() {
        return folder;
    }
}
