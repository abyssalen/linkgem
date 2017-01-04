package com.github.shaigem.linkgem.ui.main.explorer;

import com.github.shaigem.linkgem.fx.ThemeTitledToolbar;
import com.github.shaigem.linkgem.model.item.BookmarkItem;
import com.github.shaigem.linkgem.model.item.FolderItem;
import com.github.shaigem.linkgem.model.item.Item;
import com.github.shaigem.linkgem.model.item.ItemType;
import com.github.shaigem.linkgem.repository.FolderRepository;
import com.github.shaigem.linkgem.sort.SortOrder;
import com.github.shaigem.linkgem.sort.impl.MergeSortingRoutine;
import com.github.shaigem.linkgem.ui.events.*;
import com.github.shaigem.linkgem.ui.main.MainWindowPresenter;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import org.sejda.eventstudio.annotation.EventListener;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

import static org.sejda.eventstudio.StaticStudio.eventStudio;

/**
 * Created on 2016-12-25.
 */
public class FolderExplorerPresenter implements Initializable {

    public enum ExplorerAction {
        ADD_FOLDER, ADD_BOOKMARK, DELETE
    }

    private MainWindowPresenter mainWindowPresenter;

    private ObjectProperty<FolderItem> viewingFolder;

    @Inject
    private FolderRepository folderRepository;

    @FXML
    StackPane toolbarPane;
    @FXML
    StackPane itemsView;

    @FXML
    TableView<Item> itemTableView;
    @FXML
    TableColumn<Item, String> nameColumn;
    @FXML
    TableColumn<Item, String> descriptionColumn;
    @FXML
    TableColumn<Item, String> locationColumn;
    @FXML
    TableColumn<Item, ItemType> typeColumn;

    @FXML
    MenuItem addBookmarkMenuItem;
    @FXML
    MenuItem addFolderMenuItem;

    private final static String DEFAULT_PLACEHOLDER_TEXT = "Folder contains no items";

    private Label placeholder;
    private ThemeTitledToolbar toolbar;
    private MenuButton viewSettingsMenuButton;

    private BooleanProperty viewingFolderIsReadOnly;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewingFolder = new SimpleObjectProperty<>(folderRepository.getMasterFolder());
        viewingFolderIsReadOnly = new SimpleBooleanProperty();
        // TODO add more menu items
        addBookmarkMenuItem.disableProperty().bind(viewingFolderIsReadOnly);
        addFolderMenuItem.disableProperty().bind(viewingFolderIsReadOnly);
        initTable();
        initToolbar();
        initColumns();
        eventStudio().addAnnotatedListeners(this);
    }

    private void initTable() {
        placeholder = new Label(DEFAULT_PLACEHOLDER_TEXT);
        itemTableView.setPlaceholder(placeholder);
        itemTableView.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Item rowData = row.getItem();
                    if (rowData instanceof FolderItem) {
                        eventStudio().broadcast(new OpenFolderRequest((FolderItem) rowData));
                    }

                }
            });
            return row;
        });
        itemTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                eventStudio().broadcast(new ItemSelectionChangedEvent(newValue)));
    }

    private void initColumns() {
        nameColumn.setCellValueFactory(e -> e.getValue().nameProperty());
        locationColumn.setCellValueFactory(e -> {
            if (e.getValue() instanceof BookmarkItem) {
                final BookmarkItem bookmarkItem = (BookmarkItem) e.getValue();
                return bookmarkItem.locationProperty();
            }
            return null;
        });
        locationColumn.setCellFactory(column -> new TooltipTableCell());

        descriptionColumn.setCellValueFactory(e -> e.getValue().descriptionProperty());
        descriptionColumn.setCellFactory(column -> new TooltipTableCell());

        typeColumn.setCellValueFactory(e -> e.getValue().itemTypeProperty());

    }

    public void performAction(ExplorerAction action) {

        switch (action) {
            case ADD_BOOKMARK:
                onAddBookmarkAction();
                break;
            case ADD_FOLDER:
                onAddFolderAction();
                break;

        }
    }

    @FXML
    private void onAddBookmarkAction() {
        eventStudio().broadcast
                (new OpenItemDialogRequest(getViewingFolder(), new BookmarkItem("New Bookmark"), true));
    }

    @FXML
    private void onAddFolderAction() {
        eventStudio().broadcast
                (new OpenItemDialogRequest(getViewingFolder(), new FolderItem("New Folder"), true));
    }


    private FilteredList<Item> searchData;
    private SortedList<Item> sortedSearchData;

    @EventListener
    private void onSearchItemRequest(SearchItemRequest request) {
        final String searchTerm = request.getSearchTerm();
        if (searchData == null) {
            searchData = new FilteredList<>(folderRepository.getSearchFolder().getChildren(), p -> true);
            sortedSearchData = new SortedList<>(searchData);
            sortedSearchData.comparatorProperty().bind(itemTableView.comparatorProperty());
        }

        if (searchTerm.isEmpty()) { // open the master folder if search is empty
            eventStudio().broadcast(new OpenFolderRequest(folderRepository.getMasterFolder()));
            return;
        }
        searchData.setPredicate(item -> {
            if (searchTerm.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchTerm.toLowerCase();
            if (item.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (item instanceof BookmarkItem && ((BookmarkItem) item).getLocation().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            return false;
        });
        ObservableList<Item> items = folderRepository.getAllItems(folderRepository.getMasterFolder());
        folderRepository.getSearchFolder().getChildren().setAll(items);
        // open the search folder to show the results
        eventStudio().broadcast(new OpenFolderRequest(folderRepository.getSearchFolder()));
    }

    @EventListener
    private void onSelectedFolderChanged(SelectedFolderChangedEvent event) {
        FolderItem viewingFolder = event.getNewFolder();
        if (viewingFolder != null) {
            setViewingFolder(viewingFolder);
            final ObservableList<Item> children = viewingFolder.getChildren();
            final boolean isSearchFolder = viewingFolder == folderRepository.getSearchFolder();
            placeholder.setText(isSearchFolder ? "Search returned no results" : DEFAULT_PLACEHOLDER_TEXT);
            if (isSearchFolder) {
                itemTableView.setItems(sortedSearchData);
            } else {
                itemTableView.setItems(children);
            }
            System.out.println("Selected Folder Changed!");
        }
    }

    private void initToolbar() {
        toolbar = new ThemeTitledToolbar("Explorer");
        createLeftSectionToolbarItems();
        createRightSectionToolbarItems();
        toolbarPane.getChildren().addAll(toolbar);
    }

    private void createLeftSectionToolbarItems() {
        final Button editViewingFolderButton = new Button();
        editViewingFolderButton.visibleProperty().bind(viewingFolderIsReadOnly.not());
        editViewingFolderButton.setTooltip(new Tooltip("Edit the viewing folder"));
        editViewingFolderButton.setGraphic(GlyphsDude.createIcon(MaterialDesignIcon.PENCIL, "1.8em"));
        editViewingFolderButton.setOnAction(event -> eventStudio().broadcast(new OpenItemDialogRequest(getViewingFolder(), getViewingFolder(), false)));
        toolbar.getLeftSection().getChildren().addAll(editViewingFolderButton);
    }

    private void createRightSectionToolbarItems() {
        createViewSettingsMenu();
        //     final Button deleteButton = new Button();

        //    deleteButton.setGraphic(GlyphsDude.createIcon(MaterialDesignIcon.DELETE, "1.8em"));
        //    deleteButton.setContextMenu(new ContextMenu(new MenuItem("Delete All")));
        //   deleteButton.setOnAction(event -> performAction(ExplorerAction.DELETE));

        final Button addBookmarkButton = createFolderActionButton(GlyphsDude.createIcon(MaterialDesignIcon.BOOKMARK_PLUS, "1.8em"),
                "Add a new bookmark", ExplorerAction.ADD_BOOKMARK);

        final Button addFolderButton = createFolderActionButton
                (GlyphsDude.createIcon(MaterialDesignIcon.FOLDER_PLUS, "1.8em"),
                        "Add a new folder"
                        , ExplorerAction.ADD_FOLDER);

        toolbar.getRightSection().getChildren().addAll(addFolderButton, addBookmarkButton/*, deleteButton */, viewSettingsMenuButton);
    }

    /**
     * Create a button which performs folder actions such as adding an item to a folder.
     *
     * @param icon              the button's icon
     * @param tooltip           the tooltip text
     * @param explorerAction    the action
     * @param disableIfReadOnly indicate if this button should disable if the folder is read-only (cannot edit folder but can edit contents)
     * @return the button
     */
    private Button createFolderActionButton(Text icon, String tooltip, ExplorerAction explorerAction, boolean disableIfReadOnly) {
        final Button button = new Button();
        button.setGraphic(icon);
        button.setTooltip(new Tooltip(tooltip));
        button.setOnAction(event -> performAction(explorerAction));
        if (disableIfReadOnly) {
            button.disableProperty().bind(viewingFolderIsReadOnly);
        }
        return button;
    }

    private Button createFolderActionButton(Text icon, String tooltip, ExplorerAction explorerAction) {
        return this.createFolderActionButton(icon, tooltip, explorerAction, true);
    }

    private void createViewSettingsMenu() {
        viewSettingsMenuButton = new MenuButton();
        viewSettingsMenuButton.setGraphic(GlyphsDude.createIcon(MaterialDesignIcon.MENU, "1.8em"));

        final MenuItem sortAscendingMenuItem = new MenuItem("Sort by Ascending (A-Z)");
        sortAscendingMenuItem.setOnAction(event -> performManualSorting(SortOrder.ASCENDING));
        final MenuItem sortDescendingMenuItem = new MenuItem("Sort by Descending (Z-A)");
        sortDescendingMenuItem.setOnAction(event -> performManualSorting(SortOrder.DESCENDING));

        //TODO
        //     sortAscendingMenuItem.disableProperty().bind(Bindings.isEmpty(itemTableView.getItems()));
        //    sortDescendingMenuItem.disableProperty().bind(Bindings.isEmpty(itemTableView.getItems()));

        viewSettingsMenuButton.getItems().add(sortAscendingMenuItem);
        viewSettingsMenuButton.getItems().add(sortDescendingMenuItem);
        viewSettingsMenuButton.getItems().add(new SeparatorMenuItem());

        for (TableColumn<Item, ?> itemTableColumn : itemTableView.getColumns()) {
            final String columnName = itemTableColumn.getText();
            final CheckMenuItem checkMenuItem = new CheckMenuItem
                    ("Show " + columnName + " Column");
            checkMenuItem.setSelected(true);
            itemTableColumn.visibleProperty().bindBidirectional(checkMenuItem.selectedProperty());
            if (columnName.equals("Name")) {
                // we must always show the name column so don't allow users to hide it
                checkMenuItem.setDisable(true);
            }
            viewSettingsMenuButton.getItems().add(checkMenuItem);
        }
    }


    public void setViewingFolder(FolderItem viewingFolder) {
        this.viewingFolder.set(viewingFolder);
        viewingFolderIsReadOnly.bind(viewingFolder.readOnlyProperty());
        toolbar.getTitleLabel().textProperty().bind(viewingFolder.nameProperty());
    }

    public FolderItem getViewingFolder() {
        return viewingFolder.get();
    }

    public void setMainWindowPresenter(MainWindowPresenter mainWindowPresenter) {
        this.mainWindowPresenter = mainWindowPresenter;
    }

    public MainWindowPresenter getMainWindowPresenter() {
        return mainWindowPresenter;
    }

    private void performManualSorting(SortOrder order) {
        if (getViewingFolder().getChildren().isEmpty()) {
            // just in case!
            return;
        }
        Item[] itemsToSort = new Item[getViewingFolder().getChildren().size()];
        itemsToSort = getViewingFolder().getChildren().toArray(itemsToSort);
        MergeSortingRoutine mergeSortingRoutine = new MergeSortingRoutine();
        getViewingFolder().getChildren().setAll(mergeSortingRoutine.sort(order, itemsToSort));

    }

    private final static class TooltipTableCell extends TableCell<Item, String> {

        final Tooltip tooltip;

        TooltipTableCell() {
            tooltip = new Tooltip();
            tooltip.setMaxWidth(Screen.getPrimary().getVisualBounds().getWidth() / 3);
            tooltip.setWrapText(true);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setText(null);
                setGraphic(null);
                setTooltip(null);
            } else {
                setText(item);
                if (!item.isEmpty()) {
                    tooltip.setText(item);
                    setTooltip(tooltip);
                } else {
                    tooltip.setText("");
                    setTooltip(null);
                }
            }
        }
    }


}

