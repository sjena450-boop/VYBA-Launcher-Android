# Phase 2N — Home Folder Tile Previews

## Delivered
- Home-screen folder tiles now show up to three small app icons from their contents.
- The existing folder icon remains visible beneath the preview row.
- Preview icons are resolved from the launcher's current installed-app inventory.
- Missing or unavailable app icons use a neutral placeholder tile.
- Folder opening, drag-and-drop targeting, folder naming, and folder contents are unchanged.

## Validation
- GitHub Actions runs the repository unit tests and Debug APK build for this change.
- Physical-device gesture and visual QA remains a separate required check.
