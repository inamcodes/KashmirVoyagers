# 🤝 Contributing to Kashmir Voyagers

First off, thank you for taking the time to contribute! 🎉  
This is a simple open-source Android app built for travelers — and every contribution, big or small, is appreciated.

---

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [How to Contribute](#how-to-contribute)
- [Reporting Bugs](#reporting-bugs)
- [Suggesting Features](#suggesting-features)
- [Pull Request Process](#pull-request-process)
- [Coding Guidelines](#coding-guidelines)
- [Commit Message Format](#commit-message-format)
- [Ideas & Roadmap](#ideas--roadmap)

---

## 📜 Code of Conduct

By participating in this project, you agree to keep it a welcoming and respectful space for everyone. Be kind, constructive, and considerate in all interactions.

---

## 🚀 Getting Started

1. **Fork** the repository on GitHub
2. **Clone** your fork locally
```bash
git clone https://github.com/YOUR_USERNAME/KashmirVoyagers.git
cd KashmirVoyagers
```
3. **Open** in Android Studio
4. **Sync** Gradle and make sure the project builds successfully
5. **Create** a new branch for your work
```bash
git checkout -b feature/your-feature-name
```

---

## 🛠️ How to Contribute

There are many ways to help:

| Type | Examples |
|---|---|
| 🐛 **Bug Fix** | Fix a crash, layout issue, or incorrect behavior |
| ✨ **New Feature** | Add a new screen, functionality, or improvement |
| 🎨 **UI/UX** | Improve design, animations, or accessibility |
| 📝 **Docs** | Improve README, add comments, write guides |
| 🌍 **Translation** | Add support for Urdu, Hindi, or other languages |
| ♻️ **Refactor** | Clean up code without changing behavior |
| 🧪 **Testing** | Add unit tests or UI tests |

---

## 🐛 Reporting Bugs

Found a bug? Please open a [GitHub Issue](https://github.com/inamcodes/KashmirVoyagers/issues) and include:

- **Device** name and Android version
- **Steps to reproduce** the bug
- **Expected behavior** vs what actually happened
- **Screenshots** if possible

### Bug Report Template

```
**Device:** Xiaomi Redmi X / Android 13
**Steps to reproduce:**
1. Open the app
2. Tap Cash In
3. ...

**Expected:** Entry is saved and balance updates
**Actual:** App crashes

**Screenshot:** (attach if available)
```

---

## 💡 Suggesting Features

Have an idea? Open a [GitHub Issue](https://github.com/inamcodes/KashmirVoyagers/issues) with the label `enhancement` and describe:

- **What** the feature does
- **Why** it would be useful for travelers
- **How** you imagine it working (rough idea is fine)

---

## 🔄 Pull Request Process

1. Make sure your branch is **up to date** with `master`
```bash
git fetch origin
git rebase origin/master
```

2. **Test** your changes on a real Android device if possible

3. **Push** your branch
```bash
git push origin feature/your-feature-name
```

4. Open a **Pull Request** on GitHub with:
   - A clear title describing the change
   - What you changed and why
   - Screenshots for any UI changes

5. Wait for review — feedback may be given, please be responsive

6. Once approved, your PR will be **merged** 🎉

---

## 🧹 Coding Guidelines

- Use **Java** (the project does not use Kotlin currently)
- Follow **Material Design** principles for UI components
- Keep the app **simple and lightweight** — it's meant for travelers with limited connectivity
- Use `@string/` resources for all text — no hardcoded strings
- Use `@color/` resources for all colors — no hardcoded hex in layouts
- Name variables and methods clearly — prefer readability over brevity
- Add comments for any logic that isn't immediately obvious

---

## ✍️ Commit Message Format

Use clear, prefixed commit messages:

```
Add: brief description of what was added
Fix: brief description of what was fixed
Update: brief description of what was changed
Remove: brief description of what was removed
Refactor: brief description of code cleanup
Docs: brief description of documentation changes
```

### Examples
```
Add: PDF export feature for cash entries
Fix: search text color now visible on light background
Update: bottom navigation simplified to Cash and Profile
Remove: duplicate string entries from colors.xml
Docs: add CONTRIBUTING.md
```

---

## 🗺️ Ideas & Roadmap

Looking for something to work on? Here are some ideas:

### 🟢 Good First Issues (Beginner Friendly)
- Add app icon with transparent background
- Add empty state illustration when no entries exist
- Improve search bar UI and filtering logic
- Add "Coming Soon" screen for Profile tab

### 🟡 Intermediate
- Category tagging for entries (Food 🍔, Transport 🚌, Hotel 🏨 etc.)
- Filter entries by date range
- Dark mode support
- Entry edit and delete functionality

### 🔴 Advanced
- Charts and graphs for expense visualization
- Google Drive / local backup and restore
- Multi-currency support with live exchange rates
- Notification reminders to log daily expenses
- Widget for home screen quick entry

---

## 📬 Contact

Have questions? Feel free to reach out via GitHub:  
**[@inamcodes](https://github.com/inamcodes)**

---

> Thank you for helping make Kashmir Voyagers better! 🏔️ Every contribution counts.
