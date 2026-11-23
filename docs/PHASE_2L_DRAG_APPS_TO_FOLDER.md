# Phase 2L — Drag Apps onto Home Folders

## User flow
1. Turn on Home Edit Mode.
2. Long-press and drag an ungrouped pinned app tile.
3. Move it over a folder tile. The folder tile highlights while it is the current drop target.
4. Release to move the app into that folder.
5. Dragging an app elsewhere continues to use the existing adjacent-grid reorder behavior.

## Implementation
- Folder tiles publish their root-coordinate bounds through Compose layout callbacks.
- App tiles track their root position and current drag position.
- On drag release, the UI checks whether the pointer is inside a folder tile's bounds.
- A matching folder receives the package through the existing persisted add-to-folder action.
- The selected app is removed from the temporary selection set after a successful drop.

## Constraints
- Works with folders visible on the current first home page.
- Does not add a new dependency, backend, or paid service.
- Existing tap-to-select and Add to folder flow remains available as an alternative.
- Device-level gesture QA is still required; CI verifies compilation and unit tests, not touch behavior on every Android device.
