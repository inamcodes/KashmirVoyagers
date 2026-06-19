# Contributing to Kashmir Voyagers — Cash Book App

First off, thank you for considering contributing! 🏔️ This project started as a weekend build for one traveler, but it's now open for anyone who wants to improve it. This document explains how to get set up, how the codebase is organized, and how to submit changes.

By participating in this project, you agree to follow our [Code of Conduct](CODE_OF_CONDUCT.md).

---

## Table of Contents

1. [Ways to Contribute](#ways-to-contribute)
2. [Development Setup](#development-setup)
3. [Project Structure](#project-structure)
4. [Coding Guidelines](#coding-guidelines)
5. [Commit Message Convention](#commit-message-convention)
6. [Branching Strategy](#branching-strategy)
7. [Submitting a Pull Request](#submitting-a-pull-request)
8. [Reporting Bugs](#reporting-bugs)
9. [Suggesting Features](#suggesting-features)
10. [Testing Checklist](#testing-checklist)
11. [Areas Looking for Help](#areas-looking-for-help)
12. [License](#license)

---

## Ways to Contribute

- 🐛 Reporting bugs
- 💡 Suggesting features or UX improvements
- 📝 Improving documentation (README, code comments, this guide)
- 🧪 Writing tests (the project currently has none — a great first contribution!)
- 💻 Fixing issues or implementing features from the issue tracker
- 🌍 Translating strings (the app is currently English-only)

No contribution is too small. Fixing a typo in a string resource is just as welcome as a new feature.

---

## Development Setup

### Prerequisites

| Tool | Version |
|---|---|
| Android Studio | Latest stable |
| JDK | 11 or higher |
| Android SDK | API 24+ (compile/target SDK as configured in `build.gradle.kts`) |
| Git | Any recent version |

### Steps

1. **Fork** the repository on GitHub.
2. **Clone** your fork locally:
   ```bash
   git clone https://github.com/<your-username>/KashmirVoyagers.git
   cd KashmirVoyagers
   ```
3. **Add the upstream remote** so you can keep your fork in sync:
   ```bash
   git remote add upstream https://github.com/inamcodes/KashmirVoyagers.git
   ```
4. **Open the project** in Android Studio: `File → Open` → select the cloned folder, and let Gradle sync.
5. **Run the app** on an emulator or physical device (API 24+) to confirm everything builds before you start making changes.

---

## Project Structure

```
app/src/main/
├── java/com/inam/kashtrack/
│   ├── MainActivity.java          # Cash book screen + app-launch lock gate
│   ├── ProfileActivity.java       # Profile (name/photo/password) screen
│   ├── ProfileGateActivity.java   # Legacy per-visit profile gate (currently unused)
│   ├── PdfExportActivity.java     # Date-range PDF export + share
│   ├── PdfGenerator.java          # Pure android.graphics.pdf table renderer
│   ├── CashEntry.java             # Room entity for a single cash entry
│   ├── CashEntryAdapter.java      # RecyclerView adapter (date headers + entries)
│   ├── CashViewModel.java         # ViewModel backing the cash book screen
│   ├── CashRepository.java        # Room data access wrapper
│   ├── AppDatabase.java           # Room database + migrations
│   ├── ProfileRepository.java     # Local (offline) profile storage
│   ├── EntryPhotoUtils.java       # Per-entry photo capture/copy helpers
│   ├── PasswordUtils.java         # PBKDF2 password hashing/verification
│   ├── SwipeActionCallback.java   # Swipe-to-edit / swipe-to-delete gestures
│   └── DateUtils.java             # Date/time/amount formatting helpers
└── res/
    ├── layout/    # One XML file per screen/dialog/list item
    ├── values/    # strings.xml, colors.xml, themes.xml
    ├── drawable/  # Vector icons and shape backgrounds
    └── xml/       # FileProvider paths, backup rules
```

If you're adding a new screen or feature, please follow this same one-class-per-responsibility pattern (Activity for UI, Repository for storage, ViewModel for state) rather than putting everything into one Activity.

---

## Coding Guidelines

- **Language**: Java (the project does not currently use Kotlin — please keep new code in Java unless a migration is explicitly agreed on in an issue first).
- **Naming**:
  - Views: prefix by widget type, e.g. `btnSave`, `etDescription`, `tvDialogTitle`, `ivProfilePhoto`.
  - Classes: `PascalCase`; methods/fields: `camelCase`; constants: `UPPER_SNAKE_CASE`.
- **Formatting**: Use Android Studio's default Java formatter (`Code → Reformat Code`) before committing.
- **Comments**: Favor short Javadoc-style comments on public classes/methods explaining *why*, not just *what* (see existing files like `SwipeActionCallback.java` for the expected style).
- **No new dependencies without discussion**: This app intentionally has a small footprint (Room, AndroidX, Material Components). Open an issue before adding a new library.
- **Offline-first**: Don't introduce network calls or cloud dependencies — the whole point of this app is to work fully offline.
- **Resources, not hardcoded strings**: New user-facing text should go in `strings.xml`, not be hardcoded in Java, where reasonably possible.
- **Null-safety**: Guard against `null`/empty values the way the existing code does (e.g. `CashEntry.hasPhoto()`, `ProfileRepository.getCachedPhotoUrl()`).

---

## Commit Message Convention

Please use [Conventional Commits](https://www.conventionalcommits.org/) style where possible:

```
<type>(optional scope): <short summary>

<optional longer description>
```

Common types: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `style`.

Examples:
```
feat(entries): add camera/gallery photo attachment to cash entries
fix(pdf): correct running balance when entries span multiple pages
docs(readme): document the app-launch lock behavior
```

---

## Branching Strategy

- `main` is always buildable and reflects the latest released state.
- Create a feature branch off `main` for each change:
  ```bash
  git checkout -b feat/short-description
  ```
- Keep branches focused — one feature or fix per branch/PR makes review much easier.
- Rebase or merge `upstream/main` into your branch before opening a PR if it's gone stale.

---

## Submitting a Pull Request

1. Make sure the project **builds successfully** (`Build → Make Project`) and runs on a device/emulator.
2. Manually test the area you changed (see the [Testing Checklist](#testing-checklist) below).
3. Push your branch and open a PR against `main`.
4. In the PR description, include:
   - What the change does and why.
   - Screenshots/screen recordings for any UI change.
   - Any manual testing steps you performed.
   - Related issue number, if any (e.g. `Closes #12`).
5. Be responsive to review feedback — small, iterative commits are fine.
6. Once approved, a maintainer will merge the PR.

---

## Reporting Bugs

Please open an issue with:

- **Steps to reproduce** (be as specific as possible).
- **Expected behavior** vs **actual behavior**.
- **Device/OS version** (e.g. "Pixel 6, Android 14").
- **Screenshots or screen recordings**, if applicable.
- **Logcat output**, if the bug causes a crash.

> ⚠️ If the bug is a **security vulnerability** (e.g. a way to bypass the password lock, or to access another user's local data), please follow [SECURITY.md](SECURITY.md) instead of opening a public issue.

---

## Suggesting Features

Open an issue describing:

- The problem you're trying to solve (not just the feature itself).
- Who benefits from it (e.g. "travelers who split expenses with a group").
- Any UI mockups or sketches, if you have them.

Remember this app is intentionally minimal — features that add significant complexity or require network/cloud infrastructure may be declined to keep the app simple and offline-first.

---

## Testing Checklist

There's no automated test suite yet, so please manually verify the relevant rows below before submitting a PR:

- [ ] App builds and launches without crashing.
- [ ] Cash In / Cash Out entries can be added, edited (swipe left), and deleted (swipe right).
- [ ] Search filters the entry list correctly.
- [ ] Cash in Hand / Today's Balance totals update correctly after add/edit/delete.
- [ ] Attaching a photo (camera and gallery) to an entry works, and tapping the entry opens the photo viewer.
- [ ] PDF export generates a valid PDF for a selected date range and opens the share sheet.
- [ ] Profile name/photo can be edited and persist after restarting the app.
- [ ] Setting/changing/removing a password works as expected from the Profile screen.
- [ ] With a password set, force-closing and relaunching the app shows the lock screen; backgrounding and returning (without force-close) does **not**.

---

## Areas Looking for Help

- 🧪 Setting up unit/instrumentation tests (Room DAO tests, ViewModel tests, Espresso UI tests).
- 🌍 Localization (extracting remaining hardcoded strings, adding translations).
- 🧱 Implementing the placeholder bottom-nav sections (Stock, Bill, Staff, Expense).
- ♿ Accessibility improvements (content descriptions, larger touch targets, TalkBack support).
- 🔐 Migrating `SharedPreferences` profile storage to `EncryptedSharedPreferences`.

---

## License

By contributing, you agree that your contributions will be licensed under the same [MIT License](LICENSE) that covers the rest of the project.
