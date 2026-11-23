# VYBA Launcher — Technical Audit & Phase 2 Plan

Audit date: 2026-11-23
Audited branch: main
Implementation branch: phase-2/launcher-reliability

## Current implementation verified in source

- Kotlin Android application using Jetpack Compose and Material 3.
- HOME/LAUNCHER activity declaration and default-home selection flow.
- Installed launcher-app discovery through PackageManager.
- Home pinned-app grid, four-entry dock, searchable app drawer and long-press actions.
- DataStore persistence for theme, icon shape, grid columns, labels, pinned/docked/hidden packages.
- Four color presets, light/dark/system modes, Android dynamic color, corner styles and icon clipping.
- Five procedural wallpaper presets with Home, Lock and Both apply actions.
- Hidden-app management and reset controls.
- Unit tests for app filtering and theme enum fallback.
- GitHub Actions workflow builds a debug APK and runs JVM unit tests.

## Findings and limitations

1. App refresh had no failure handling. A PackageManager exception could leave the loading state active indefinitely.
2. Drawer search matched labels only; users could not search by package identifier.
3. Installed-app inventory refresh is initial-load or manual only; package changes are not automatically observed.
4. Package discovery uses QUERY_ALL_PACKAGES. Review Google Play package-visibility policy before store distribution.
5. Android 15 private-space discovery and its separate locked/unlocked container are not implemented.
6. This audit does not establish physical-device or emulator QA. CI success proves only the configured automated build/tests.
7. Release signing depends on a locally supplied keystore; no production signing credentials are stored in the repository.

## Phase 2 sequence

### Phase 2A — Reliability (current branch)
- Make app refresh resilient to errors and cancellation.
- Allow search by app label or package name.
- Add regression coverage for package-name search.
- Keep changes isolated on a feature branch until CI passes.

### Phase 2B — Launcher lifecycle
- Refresh app inventory when returning to the launcher.
- Observe package add/remove/change broadcasts with lifecycle-safe registration.
- Reconcile stale pinned and dock entries without unexpectedly deleting user preferences.

### Phase 2C — Premium personalization
- Add home-screen pages and a controlled edit mode.
- Add user-managed folders and drag/reorder interactions.
- Add icon pack support after choosing a compatible icon-pack contract.
- Improve wallpaper preview and add user-image wallpaper import.

### Phase 2D — Android compatibility and release
- Evaluate Android 15 private-space support as a separately scoped feature; implement its separate container and lock-state behavior before declaring support.
- Test on real devices across supported API levels, screen sizes and OEM launchers.
- Review package visibility, accessibility, backup/restore, battery use and release signing.
- Produce and install-test a signed release APK.

## Safety of this work

No existing main-branch files are replaced. Phase 2A is developed on phase-2/launcher-reliability; merge only after branch CI succeeds and the diff is reviewed.
