# Security Policy

## Supported Versions

The following versions of Kashmir Voyagers are currently supported with security updates:

| Version | Supported |
|---|---|
| Latest (main branch) | ✅ Yes |
| Older debug builds | ❌ No |

Since this is an actively developed single-version app, always use the **latest build from the main branch** for the most secure and stable experience.

---

## Scope

This security policy covers:

- **The Android app** (Java source code, layouts, resources)
- **Local data storage** on the user's device
- **PDF export** functionality
- **Any future features** added to the app

---

## 🛡️ Security Practices in This App

Kashmir Voyagers is designed with simplicity and privacy in mind:

- ✅ **No internet permission** — the app works fully offline
- ✅ **No user accounts or login** — no credentials are stored
- ✅ **No third-party analytics or tracking SDKs**
- ✅ **All data stored locally** on the device only
- ✅ **No sensitive permissions** requested (no camera, contacts, location etc.)
- ✅ **PDF exports saved locally** — not uploaded anywhere

---

## Reporting a Vulnerability

If you discover a security vulnerability in Kashmir Voyagers, please **do not open a public GitHub Issue** as this could expose the vulnerability to others before it is fixed.

### How to Report

Please report security vulnerabilities **privately** by:

1. Going to the [GitHub Security Advisories](https://github.com/inamcodes/KashmirVoyagers/security/advisories/new) page
2. Or contacting the maintainer directly via GitHub: **[@inamcodes](https://github.com/inamcodes)**

### What to Include in Your Report

Please provide as much of the following as possible:

- **Type of vulnerability** (e.g. data exposure, insecure storage, code injection)
- **Location** — which file, class, or feature is affected
- **Steps to reproduce** the vulnerability
- **Potential impact** — what could an attacker do with this?
- **Suggested fix** (optional but appreciated)

---

## ⏱️ Response Timeline

| Action | Timeframe |
|---|---|
| Acknowledgement of report | Within **48 hours** |
| Initial assessment | Within **5 days** |
| Fix released (if confirmed) | Within **14 days** |
| Public disclosure | After fix is released |

---

## 🔒 Data & Privacy

Kashmir Voyagers does **not** collect, transmit, or store any personal data outside of the user's device.

- All cash entries are stored **locally** using Android's local storage
- No data is sent to any server or third party
- Uninstalling the app removes all data from the device

This app is intended for **personal, offline use** by individual travelers.

---

## Responsible Disclosure

We kindly ask that you:

- Give us reasonable time to fix the issue before any public disclosure
- Avoid accessing, modifying, or deleting data that isn't yours
- Act in good faith — we appreciate security researchers who help make open-source software safer

In return, we will:

- Acknowledge your contribution in the fix/release notes
- Work with you to understand and resolve the issue quickly

---

## Contact

**Maintainer:** [@inamcodes](https://github.com/inamcodes)  
**Security Advisories:** [GitHub Security Tab](https://github.com/inamcodes/KashmirVoyagers/security)

---

> Thank you for helping keep Kashmir Voyagers safe for all travelers. 🏔️
