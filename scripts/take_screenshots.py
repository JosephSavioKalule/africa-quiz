#!/usr/bin/env python3
"""
Automated screenshot capture for the Africa Quiz Android app.

Uses ADB to interact with a running emulator: launches the app,
navigates through each screen (Home, Quiz, Answer Feedback, Results),
and saves PNG screenshots to the project root.

Prerequisites:
    - Android SDK with platform-tools (adb) and emulator
    - A configured AVD (Android Virtual Device)
    - The app built via ./gradlew assembleDebug

Usage:
    python scripts/take_screenshots.py

    Options:
        --avd NAME        AVD name (default: auto-detect first available)
        --output-dir DIR  Where to save screenshots (default: screenshots/)
        --skip-boot       Skip emulator boot (assumes already running)
        --install         Install/reinstall the APK before capturing
"""

import argparse
import os
import subprocess
import sys
import time
import xml.etree.ElementTree as ET

# ── Paths ──────────────────────────────────────────────────────────────────────

# Resolve the project root (one level up from scripts/)
PROJECT_ROOT = os.path.normpath(os.path.join(os.path.dirname(__file__), ".."))
SDK_DIR = os.path.expanduser("~/Library/Android/sdk")
ADB = os.path.join(SDK_DIR, "platform-tools", "adb")
EMULATOR = os.path.join(SDK_DIR, "emulator", "emulator")
APP_PACKAGE = "com.example.africaquiz"
MAIN_ACTIVITY = f"{APP_PACKAGE}/.MainActivity"
APK_PATH = os.path.join(
    PROJECT_ROOT, "app", "build", "outputs", "apk", "debug", "app-debug.apk"
)

# ── ADB helpers ────────────────────────────────────────────────────────────────


def adb(*args: str) -> str:
    """Run an ADB command and return its stdout."""
    result = subprocess.run(
        [ADB, *args], capture_output=True, text=True, timeout=30
    )
    return result.stdout.strip()


def adb_shell(*args: str) -> str:
    """Run a command on the device via 'adb shell'."""
    return adb("shell", *args)


def tap(x: int, y: int):
    """Simulate a screen tap at pixel coordinates (x, y)."""
    adb_shell("input", "tap", str(x), str(y))


def screencap(filepath: str):
    """Capture the screen and save as PNG to a local file."""
    raw = subprocess.run(
        [ADB, "exec-out", "screencap", "-p"],
        capture_output=True, timeout=15
    )
    with open(filepath, "wb") as f:
        f.write(raw.stdout)
    print(f"  Saved: {filepath}")


def get_screen_size() -> tuple[int, int]:
    """Return the device screen dimensions as (width, height)."""
    # Output looks like: "Physical size: 1080x2424"
    output = adb_shell("wm", "size")
    size_str = output.split(":")[-1].strip()
    w, h = size_str.split("x")
    return int(w), int(h)


def find_button_center(text: str) -> tuple[int, int] | None:
    """
    Dump the UI hierarchy via uiautomator, search for a node whose text
    matches `text`, walk up to the nearest clickable parent, and return
    the center of its bounding box.

    Returns None if the element isn't found.
    """
    # Dump UI tree to a temporary file on device
    adb_shell("uiautomator", "dump", "/sdcard/ui.xml")
    xml_str = adb_shell("cat", "/sdcard/ui.xml")

    root = ET.fromstring(xml_str)

    # Build a child-to-parent map so we can walk upward
    parent_map = {child: parent for parent in root.iter() for child in parent}

    for node in root.iter("node"):
        if node.attrib.get("text") == text:
            # Walk up to find the clickable container (the actual tap target)
            target = node
            while target in parent_map:
                if target.attrib.get("clickable") == "true":
                    break
                target = parent_map[target]

            bounds = target.attrib.get("bounds", "")
            # bounds format: "[left,top][right,bottom]"
            parts = bounds.replace("][", ",").strip("[]").split(",")
            if len(parts) == 4:
                left, top, right, bottom = map(int, parts)
                return (left + right) // 2, (top + bottom) // 2

    return None


# ── Emulator management ───────────────────────────────────────────────────────


def list_avds() -> list[str]:
    """List available Android Virtual Devices."""
    result = subprocess.run(
        [EMULATOR, "-list-avds"], capture_output=True, text=True
    )
    return [line.strip() for line in result.stdout.splitlines() if line.strip()]


def is_emulator_running() -> bool:
    """Check if any emulator device is connected via ADB."""
    output = adb("devices")
    return "emulator" in output


def boot_emulator(avd_name: str):
    """Start the emulator in the background and wait for it to fully boot."""
    print(f"Booting emulator: {avd_name}")
    subprocess.Popen(
        [EMULATOR, "-avd", avd_name, "-no-audio", "-no-boot-anim"],
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
    )

    # Poll until the device reports boot complete
    for i in range(90):
        time.sleep(2)
        result = adb_shell("getprop", "sys.boot_completed")
        if result.strip() == "1":
            print(f"  Emulator booted after {(i + 1) * 2}s")
            return
    print("  WARNING: Emulator may not have fully booted (timeout)")


# ── Screenshot sequences ──────────────────────────────────────────────────────


def capture_home(output_dir: str):
    """
    Capture the Home screen.
    Assumes the app is freshly launched and showing the home screen.
    """
    print("\n[1/4] Capturing Home screen...")
    time.sleep(2)
    screencap(os.path.join(output_dir, "01_home.png"))


def capture_quiz(output_dir: str):
    """
    Tap 'Start Quiz' and capture the Quiz screen with the 3D globe
    and the first question.
    """
    print("\n[2/4] Navigating to Quiz screen...")
    center = find_button_center("Start Quiz")
    if center:
        tap(*center)
    else:
        # Fallback: tap center-bottom area where button typically is
        w, h = get_screen_size()
        tap(w // 2, int(h * 0.69))

    # Wait for globe.gl to render in the WebView
    time.sleep(5)
    screencap(os.path.join(output_dir, "02_quiz.png"))


def capture_answer_feedback(output_dir: str):
    """
    Tap the first answer choice to trigger feedback (green/red buttons
    and globe animation), then capture the screen.
    """
    print("\n[3/4] Capturing answer feedback...")

    # Dump UI to find the first answer button below the question
    adb_shell("uiautomator", "dump", "/sdcard/ui.xml")
    xml_str = adb_shell("cat", "/sdcard/ui.xml")
    root = ET.fromstring(xml_str)

    # Collect all clickable buttons — answer buttons are the ones in the
    # lower half of the screen with text content
    _, screen_h = get_screen_size()
    answer_buttons = []
    for node in root.iter("node"):
        bounds = node.attrib.get("bounds", "")
        text = node.attrib.get("text", "")
        clickable = node.attrib.get("clickable", "")
        if clickable == "true" and text and bounds:
            parts = bounds.replace("][", ",").strip("[]").split(",")
            if len(parts) == 4:
                top = int(parts[1])
                # Answer buttons are in the lower portion of the screen
                if top > screen_h * 0.5:
                    answer_buttons.append((text, parts))

    if answer_buttons:
        # Tap the first answer button
        parts = answer_buttons[0][1]
        left, top, right, bottom = map(int, parts)
        cx, cy = (left + right) // 2, (top + bottom) // 2
        print(f"  Tapping answer: '{answer_buttons[0][0]}' at ({cx}, {cy})")
        tap(cx, cy)
    else:
        # Fallback: tap roughly where the first answer button should be
        w, h = get_screen_size()
        tap(w // 2, int(h * 0.62))

    # Wait for the feedback animation and globe movement
    time.sleep(2)
    screencap(os.path.join(output_dir, "03_answer_feedback.png"))


def capture_results(output_dir: str):
    """
    Answer all remaining questions (by tapping the first choice each time)
    and capture the Results screen at the end.
    """
    print("\n[4/4] Completing quiz for Results screen...")

    # We already answered Q1 above; auto-advance takes 2s per question.
    # Answer questions 2–10 by tapping the first available answer.
    for q in range(2, 11):
        # Wait for auto-advance to next question
        time.sleep(3)

        # Tap first answer button (approximate location)
        w, h = get_screen_size()
        tap(w // 2, int(h * 0.62))
        print(f"  Answered question {q}/10")
        time.sleep(1)

    # Wait for navigation to Results screen
    time.sleep(4)
    screencap(os.path.join(output_dir, "04_results.png"))


# ── Main ──────────────────────────────────────────────────────────────────────


def main():
    parser = argparse.ArgumentParser(
        description="Capture screenshots of the Africa Quiz app"
    )
    parser.add_argument(
        "--avd",
        help="AVD name to boot (default: first available)",
    )
    parser.add_argument(
        "--output-dir",
        default=os.path.join(PROJECT_ROOT, "screenshots"),
        help="Directory to save screenshots (default: screenshots/)",
    )
    parser.add_argument(
        "--skip-boot",
        action="store_true",
        help="Skip emulator boot (assumes already running)",
    )
    parser.add_argument(
        "--install",
        action="store_true",
        help="Install/reinstall the debug APK before capturing",
    )
    args = parser.parse_args()

    # Ensure output directory exists
    os.makedirs(args.output_dir, exist_ok=True)

    # ── Step 1: Boot emulator if needed ────────────────────────────────────
    if not args.skip_boot and not is_emulator_running():
        avd = args.avd
        if not avd:
            avds = list_avds()
            if not avds:
                print("ERROR: No AVDs found. Create one in Android Studio.")
                sys.exit(1)
            avd = avds[0]
        boot_emulator(avd)
    else:
        print("Emulator already running, skipping boot.")

    # ── Step 2: Install APK if requested ───────────────────────────────────
    if args.install:
        if not os.path.exists(APK_PATH):
            print(f"ERROR: APK not found at {APK_PATH}")
            print("Run ./gradlew assembleDebug first.")
            sys.exit(1)
        print(f"Installing {APK_PATH}...")
        adb("install", "-r", APK_PATH)

    # ── Step 3: Launch the app ─────────────────────────────────────────────
    print("Launching Africa Quiz...")
    adb_shell("am", "force-stop", APP_PACKAGE)
    time.sleep(1)
    adb_shell("am", "start", "-n", MAIN_ACTIVITY)
    time.sleep(2)

    # ── Step 4: Capture each screen ────────────────────────────────────────
    capture_home(args.output_dir)
    capture_quiz(args.output_dir)
    capture_answer_feedback(args.output_dir)
    capture_results(args.output_dir)

    print(f"\nAll screenshots saved to {args.output_dir}/")


if __name__ == "__main__":
    main()
