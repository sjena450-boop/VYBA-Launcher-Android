# Phase 2F — Home Grid Drag Reordering

## Implemented
- Home edit mode now attaches a drag gesture to each pinned app tile.
- Dragging horizontally moves an app one grid position; dragging vertically moves it by the configured grid-column count.
- The target index is bounded to the currently visible home page so a drag does not unexpectedly move an app to another page.
- Reordering calls the ViewModel persistence method and is stored through DataStore.
- Edit-mode helper text explains that apps can be dragged to rearrange.

## Interaction notes
- Drag reorder is enabled only while Home Edit Mode is active.
- Normal tap-to-launch and long-press app actions remain the default outside edit mode.
- This is a directional slot-reorder interaction, not a freeform pixel-position layout.
- Folder creation and cross-page drag/drop are not included yet.

## Validation
- GitHub Actions is triggered on each commit. Check the latest workflow run for test and APK build results.
- Physical device testing is still required for gesture feel and touch interaction.
