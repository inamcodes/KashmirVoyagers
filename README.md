<div align="center">
<table border="0" style="border: none; background: transparent;">
  <tr style="border: none; background: transparent;">
    <!-- Left Column: Image -->
    <td valign="center" style="border: none; padding-right: 20px;">
      <img width="248" height="357" alt="Kashmir_app_icon" src="https://github.com/user-attachments/assets/cedf77e8-cdf8-42d2-a767-c3f64cff9f7a" />
    </td>
    <!-- Right Column: Text Content -->
    <td valign="center" align="left" style="border: none;">
      <h1>🏔️ Kashmir Voyagers — Cash Book App</h1>
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
- 📄 **Export to PDF** — Save or share your cash book as a PDF
- 🧭 **Simple Navigation** — Cash view and Profile (Coming Soon)

---

## 📸 Screenshots

<img width="1080" height="2400" alt="kashmirVoyagers_app_ss" src="https://github.com/user-attachments/assets/1964ed78-26fc-4fbd-a71a-f5676571392c" />

---

## 🛠️ Tech Stack

| | |
|---|---|
| **Language** | Java |
| **Platform** | Android |
| **Min SDK** | Android 7.0+ |
| **UI** | XML Layouts + Material Design Components |
| **Build** | Gradle (Kotlin DSL) |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest version recommended)
- JDK 11 or higher
- Android device or emulator (API 24+)

### Installation

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

This is a personal project built for a specific trip, but contributions are welcome!

### How to Contribute

1. **Fork** the repository
2. **Create** a new branch
```bash
git checkout -b feature/your-feature-name
```
3. **Make** your changes
4. **Commit** with a clear message
```bash
git commit -m "Add: brief description of your change"
```
5. **Push** to your branch
```bash
git push origin feature/your-feature-name
```
6. **Open a Pull Request** on GitHub

### Ideas for Contributions

- 📊 Charts/graphs for expense visualization
- 🗂️ Category tagging for entries (Food, Transport, Hotel etc.)
- 🌙 Dark mode support
- ☁️ Cloud backup / Google Drive sync
- 👤 Profile screen (currently "Coming Soon")
- 🌍 Multi-currency support
- 🔔 Daily reminder notifications

### Guidelines

- Keep it **simple** — this app is meant to be lightweight
- Follow existing **code style** and naming conventions
- Test on a **real device** before submitting a PR
- One feature per pull request please

---

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👤 Author

**inamcodes**  
GitHub: [@inamcodes](https://github.com/inamcodes)

---

> Built with ❤️ for Kashmir travelers. Safe travels, bhai! 🏔️
