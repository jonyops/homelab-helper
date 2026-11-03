# Homelab Helper

A small Android app for things the Home Assistant app can't do on its own. Home Assistant
sends it a command (an Android broadcast) and it acts on the phone. Each feature is its own
command, so new ones can be added later without changing the others.

It asks for no permissions and has no internet access. Features that need a permission add
it when they are added.

## Commands

| Action | Extras | What it does |
|--------|--------|--------------|
| `local.homelab.helper.CHIME` | `sound` (default `chores_done`) | Plays a built-in sound from `assets/chimes/` on the notification stream (follows notification volume, quiet on silent) |

## Install

1. Add this repository in Obtainium and install the latest release.
2. Open **Homelab Helper** once (Android does not deliver signals to an app that has never
   been opened). Tap **Play the chime** to check the volume, then close it.

## Home Assistant

Send a notification command to the phone with the Home Assistant app:

```yaml
action: notify.mobile_app_<phone>
data:
  message: command_broadcast_intent
  data:
    intent_package_name: local.homelab.helper
    intent_action: local.homelab.helper.CHIME
    intent_extras: "sound:chores_done"
```

`sound` names a file in `assets/chimes/` without `.mp3`. An unknown name plays nothing.

## Sounds

| Name | Used for |
|------|----------|
| `chores_done` | All of your chores are done |

To add one, put `<name>.mp3` (lowercase letters, digits and `_`) in `assets/chimes/`, bump
`version.txt`, build and release.

## Adding a feature

Add a receiver for a new action `local.homelab.helper.<NAME>` in `AndroidManifest.xml`, list
it in the Commands table, bump `version.txt`, build and release.

## Build

Releases build themselves: bump `version.txt`, commit, and push a tag such as `v1.1`. GitHub Actions
builds the APK, signs it with the key in the repository secrets `KEYSTORE_B64` and
`KEYSTORE_PASS`, and attaches it to a release, which Obtainium picks up.

To build by hand on Windows with the Android SDK command-line tools (build-tools 36, platform 36) and a JDK:

```powershell
./build.ps1
```

A local build signs with `signing.jks` + `signing.pass` next to the script (use a copy of the
release key, alias `helper`, so phones accept it as an update). Never commit them: every
update must be signed with the same key.
