# Security Policy

Kashmir Voyagers is a small, fully offline cash-tracking app. This document explains what's in scope for security reports, how the app currently protects user data, and how to report a vulnerability responsibly.

## Supported Versions

| Version | Supported |
|---|---|
| Latest commit on `main` | ✅ |
| Latest tagged release | ✅ |
| Older releases | ❌ (please update before reporting) |

As a small project without a long-term release branch strategy yet, security fixes are applied to `main` and the most recent tagged release only.

## How the App Handles Data Today

Understanding the current architecture helps when assessing what counts as a vulnerability:

- **Fully offline** — the app makes no network calls. There is no backend, no analytics SDK, and no telemetry.
- **Local-only storage**:
  - Cash entries are stored in a local Room (SQLite) database (`kashtrack_db`), private to the app's data directory.
  - Profile data (name, password hash/salt) is stored in `SharedPreferences`, private to the app.
  - Profile photos and per-entry photos are copied into the app's private internal storage (`getFilesDir()`), not external/shared storage.
- **Password handling** — when a password is set, it is **never stored in plain text**. It is salted with a fresh random 16-byte salt and hashed using **PBKDF2WithHmacSHA256** (12,000 iterations, 256-bit key) before being persisted. See [`PasswordUtils.java`](app/src/main/java/com/inam/kashtrack/PasswordUtils.java).
- **App-launch lock** — if a password is set, the app challenges for it once per "cold start" (i.e., after the process has been fully killed and relaunched), not on every quick app-switch.
- **File sharing** — PDF exports and camera-captured photos are exposed to other apps only via a scoped `FileProvider` with `grantUriPermissions`, not by exposing raw file paths or making files world-readable.

## Known Limitations / Non-Goals

To set expectations, the following are **known limitations**, not necessarily bugs, though we're happy to receive PRs improving them:

- `SharedPreferences` is not currently wrapped with `EncryptedSharedPreferences` / the Android Keystore — on a rooted or compromised device, the stored password hash/salt could be read from disk (though the password itself is never stored, only its salted hash).
- The local SQLite database and photo files are not encrypted at rest beyond the OS-level app-sandboxing Android already provides.
- The app does not currently detect rooted devices, hooked processes, or screen-recording while sensitive screens are open.
- There is no remote wipe / multi-device sync feature, since the app is intentionally offline-only.

If your report relates to one of the above, please still open it — we track these as improvement issues, just with lower urgency than an active exploit.

## Reporting a Vulnerability

**Please do not open a public GitHub issue for security vulnerabilities.**

Instead, report it privately using one of these channels:

1. **Preferred**: Open a [GitHub Security Advisory](https://github.com/inamcodes/KashmirVoyagers/security/advisories/new) on this repository (this is private between you and the maintainer until resolved).
2. **Alternative**: Contact the maintainer directly via their GitHub profile: [@inamcodes](https://github.com/inamcodes).

When reporting, please include:

- A clear description of the vulnerability and its potential impact.
- Step-by-step reproduction instructions (device/emulator, Android version, app version/commit hash).
- Any proof-of-concept code, logs, or screenshots that help demonstrate the issue.
- Whether you believe the issue is being actively exploited (it shouldn't be possible given the app is offline, but please flag it if you have reason to think otherwise).

### What's In Scope

- Bypassing the password/app-lock without knowing the password.
- Extracting another user's cash entries, profile data, or photos without physical/ADB access to that specific device.
- Any flaw in the password hashing/verification logic (`PasswordUtils.java`).
- Path traversal, injection, or other flaws in how photo files or PDF files are created, named, or shared via `FileProvider`.
- Any crash that can be triggered by malicious/malformed input from outside the app (e.g. a crafted file shared into the app, if such a feature exists in your version).

### What's Out of Scope

- Attacks that require a rooted device, physical access with USB debugging already enabled, or a device already compromised by other malware — Android's app sandboxing assumes trust at that level.
- Social engineering attacks against the maintainer or contributors.
- Denial of service against your own local device (e.g. filling up storage).
- Issues in third-party libraries (AndroidX, Material Components, Room) — please report those upstream, though we're happy to be notified so we can update our dependency versions.

## Response Process

1. We aim to acknowledge new reports within **5 business days**.
2. We'll work with you to understand and confirm the issue, and may ask for more details.
3. Once confirmed, we'll aim to ship a fix and credit you (if you'd like) in the release notes, unless you prefer to remain anonymous.
4. We follow a coordinated disclosure approach: please give us reasonable time to release a fix before publicly disclosing details.

## Recognition

We don't currently run a paid bug bounty program, but valid reports will be credited in the changelog/release notes (with your permission) as thanks for helping keep the app's users safe.

Thank you for helping keep Kashmir Voyagers and its users secure! 🏔️
