<div align="center">
<table border="0" style="border: none; background: transparent;">
  <tr style="border: none; background: transparent;">
    <!-- Left Column: Image -->
    <td valign="center" style="border: none; padding-right: 20px;">
      <img width="248" height="357" alt="Kashmir_app_icon" src="https://github.com/user-attachments/assets/cedf77e8-cdf8-42d2-a767-c3f64cff9f7a" />
    </td>
    <!-- Right Column: Text Content -->
    <td valign="center" align="left" style="border: none;">
      <h1>ڪراچي جون رولاڪيون — Digital کيسو</h1>
      <p>A simple, lightweight Android app built with love for a brother heading on a trip to Kashmir.</p>
      <p>Designed to help travelers <strong>track cash in, cash out, and daily expenses</strong> — all in one clean screen.</p>
    </td>
  </tr>
</table>
</div>

---

## 📖 The Story

My brother was going on a trip to Kashmir and needed a dead-simple way to manage his expenses on the go. No spreadsheets, no complex finance apps — just a clean cash book he could open, log an entry, and close.

So I built this over a weekend: a single-screen Android app that does exactly what he needed and nothing more.

---

## 📱 Features
 
- 💰 **Cash In / Cash Out** — Log income and expenses with a single tap
- 🧾 **Cash in Hand** — See your running balance at a glance
- 📅 **Today's Balance** — Track how much you've spent today
- 🔍 **Search** — Quickly find past entries
- 📷 **Photo Attachments** — Attach a receipt photo to any entry via camera capture or gallery pick, then tap the entry to view it full-screen
- 📄 **Export to PDF** — Save or share your cash book as a clean, table-based PDF for a chosen date range
- 👤 **Profile** — Set your name and photo, all stored locally on-device
- 🔒 **Password Protection** — Optionally lock the app with a password; once set, you're asked for it on cold launch (not on quick app-switches), keeping your cash book private
- 🧭 **Simple Navigation** — Bottom nav for Cash and Profile (Stock / Bill / Staff / Expense coming soon)


---

## 🛠️ Tech Stack

| | |
|---|---|
| **Language** | Java |
| **Platform** | Android |
| **Min SDK** | Android 7.0+ |
| **Local Storage** | Room (SQLite) for entries, SharedPreferences + internal file storage for profile/photos |
| **UI** | XML Layouts + Material Design Components |
| **Build** | Gradle (Kotlin DSL) |


---

## 📸 Screenshots

<img width="1080" height="2400" alt="kashmirVoyagers_app_ss" src="https://github.com/user-attachments/assets/1964ed78-26fc-4fbd-a71a-f5676571392c" />

---


## 🚀 Getting Started

### Prerequisites
- Android Studio (latest version recommended)
- JDK 11 or higher
- Android device or emulator (API 24+)

### Installation

#### Method 1

1. [Download the latest APK release](https://github.com/inamcodes/KashmirVoyagers/releases/tag/main)

2. Install in your Android Device

3. Have Fun :)

#### Method 2

1. **Clone the repository**
```bash
git clone https://github.com/inamcodes/KashmirVoyagers.git
```

2. **Open in Android Studio**
   - File → Open → Select the cloned folder

3. **Build the project**
   - Build → Build Bundle(s)/APK(s) → Build APK(s)

4. **Run on device**
   - Connect your Android device via USB with USB Debugging enabled
   - Click the ▶ Run button in Android Studio

---

## 📂 Project Structure

```
KashmirVoyagers/
├── app/
│   └── src/
│       └── main/
│           ├── java/         # Java source files
│           ├── res/
│           │   ├── layout/   # XML layouts
│           │   ├── values/   # Colors, strings, themes
│           │   ├── drawable/ # Icons and backgrounds
│           │   └── mipmap/   # App launcher icons
│           └── AndroidManifest.xml
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🤝 Contributing
 
Contributions, bug reports, and feature ideas are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for setup steps, coding conventions, and how to submit a pull request.
 
This project follows a [Code of Conduct](CODE_OF_CONDUCT.md) — by participating, you're expected to uphold it.
 
## 🛡️ Reporting a Vulnerability
 
Found a security issue? Please **don't** open a public issue — see [SECURITY.md](SECURITY.md) for how to report it responsibly.

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👤 Author

**inamcodes**  
GitHub: [@inamcodes](https://github.com/inamcodes)

---

> Built with ❤️ for Kashmir travelers. Safe travels, bhai/behin! 🏔️
