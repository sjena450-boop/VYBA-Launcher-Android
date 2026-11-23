# Phase 2J — Add Apps to Existing Home Folders

## Scope
This phase completes the basic folder organization flow from Home Edit Mode.

## User flow
1. Enter Home Edit Mode.
2. Tap ungrouped pinned app tiles to select or deselect them.
3. Choose **Add to folder** when at least one app is selected and a folder already exists.
4. Choose a destination folder. Selected apps are added to that folder and disappear from the ungrouped home grid.
5. The updated folder membership is persisted through DataStore.

## Implementation
- `PreferencesRepository.addAppsToHomeFolder` validates that every selected package is still pinned, merges members without duplicates, and persists the folder list.
- `HomeViewModel.addAppsToHomeFolder` exposes the repository operation.
- `HomeScreen` adds the destination-folder picker dialog and clears the selection after the action.
- `VybaNavGraph` wires the callback.

## Guardrails
- No new dependency or server cost.
- Existing folder names, IDs, and members are preserved.
- Folder selection is available only when there are selected apps and at least one existing folder.
- This is a tap-to-select and move workflow; drag-and-drop directly onto a folder icon remains a later enhancement.
