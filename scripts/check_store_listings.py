#!/usr/bin/env python3
"""Verify every supported app language has a complete Play Store listing.

Adding a language to the app touches locales_config.xml, composeResources and
PlayStoreLocales.kt, but the Play listing only appears once the supply folder
under fastlane/metadata/android/ exists, holds the listing copy, has screenshots
copied into it and has been pushed with 'fastlane android upload_listing'. Every
one of those is a separate manual step, so a language ships in the app while its
store page stays English. This check fails the build instead.

Reads the canonical locale list from androidApp/src/main/res/xml/locales_config.xml,
the resource-locale -> supply-folder mapping from PlayStoreLocales.kt, and the
listing itself from fastlane/metadata/android/.

Exit code 0 when every language has a complete listing; 1 otherwise.

Examples:
  ./scripts/check_store_listings.py
  ./scripts/check_store_listings.py --json
  ./scripts/check_store_listings.py --live      # also diff against the live Play listing
  ./gradlew checkStoreListings
"""

from __future__ import annotations

import argparse
import json
import os
import sys
from dataclasses import dataclass, field
from pathlib import Path
import re

REPO_ROOT = Path(__file__).resolve().parents[1]

DEFAULT_LOCALES_CONFIG = REPO_ROOT / "androidApp/src/main/res/xml/locales_config.xml"
DEFAULT_PLAY_LOCALES = (
    REPO_ROOT
    / "screenshotTests/src/test/kotlin/com/inspiredandroid/braincup/screenshots/PlayStoreLocales.kt"
)
DEFAULT_METADATA_DIR = REPO_ROOT / "fastlane/metadata/android"
DEFAULT_PLAY_KEY = REPO_ROOT / "fastlane/play-store-key.json"

PACKAGE_NAME = "com.inspiredandroid.braincup"

LOCALE_NAME_PATTERN = re.compile(r'<locale\s+android:name="([^"]+)"')
PLAY_LOCALE_PATTERN = re.compile(r'arrayOf\(\s*"([^"]+)"\s*,\s*"([^"]+)"\s*\)')

# Play Console's hard limits on listing copy. supply fails the upload mid-way through the
# locale list when one overruns, leaving the listing half updated.
TEXT_FILES = {
    "title.txt": 30,
    "short_description.txt": 80,
    "full_description.txt": 4000,
}

# Play needs at least two phone screenshots before a listing can be published; the tablet
# set is what stops the Play tablet tab from falling back to the English images.
MIN_PHONE_SCREENSHOTS = 2
MIN_TABLET_SCREENSHOTS = 1

# The codes edits.listings.update accepts, probed against the API on 2026-08-27. Play is not
# consistent about region suffixes - de-DE and cs-CZ are required, bg-BG and sk-SK are refused
# in favour of bare bg and sk - and it answers a plain 400 for a wrong one, which aborts supply
# part-way through the locale list after it has already uploaded other languages. Checking the
# folder name here turns that into a failed check before anything is sent.
PLAY_LISTING_LANGUAGES = frozenset(
    """
    af am ar az-AZ be bg bn-BD ca cs-CZ da-DK de-DE el-GR en-AU en-CA en-GB en-IN en-SG en-US
    en-ZA es-419 es-ES es-US et eu-ES fa fa-IR fi-FI fil fr-CA fr-FR gl-ES gu he-IL hi-IN hr
    hu-HU hy-AM id is-IS it-IT iw-IL ja-JP ka-GE kk km-KH kn-IN ko-KR ky-KG lo-LA lt lv mk-MK
    ml-IN mn-MN mr-IN ms ms-MY my-MM nb-NO ne-NP nl-NL no-NO pa pl-PL pt-BR pt-PT rm ro ru-RU
    si-LK sk sl sq sr sv-SE sw ta-IN te-IN th tl tr-TR uk ur vi zh-CN zh-HK zh-TW zu
    """.split(),
)

# App languages Play publishes no store listing for at all. Their copy is parked under
# fastlane/metadata/android-unsupported/ so supply never sees it; see the README there.
PLAY_UNSUPPORTED_LANGUAGES = {
    "ga": "Play publishes no Irish store listing ('ga' and 'ga-IE' are both refused)",
}


def resource_to_config_locale(resource_locale: str) -> str:
    """PlayStoreLocales.kt uses Android resource qualifiers ('zh-rTW'), locales_config.xml
    uses BCP-47 ('zh-TW')."""
    return resource_locale.replace("-r", "-")


@dataclass
class ListingReport:
    play_locale: str
    resource_locale: str
    exists: bool
    missing_text: list[str] = field(default_factory=list)
    overlong_text: list[str] = field(default_factory=list)
    phone_screenshots: int = 0
    tablet_screenshots: int = 0

    @property
    def screenshot_issues(self) -> list[str]:
        issues = []
        if self.phone_screenshots < MIN_PHONE_SCREENSHOTS:
            issues.append(
                f"{self.phone_screenshots} phone screenshot(s), need {MIN_PHONE_SCREENSHOTS}",
            )
        if self.tablet_screenshots < MIN_TABLET_SCREENSHOTS:
            issues.append(
                f"{self.tablet_screenshots} tablet screenshot(s), need {MIN_TABLET_SCREENSHOTS}",
            )
        return issues

    @property
    def ok(self) -> bool:
        return (
            self.exists
            and not self.missing_text
            and not self.overlong_text
            and not self.screenshot_issues
        )


@dataclass
class CheckResult:
    unmapped_locales: list[str]
    unsupported_locales: list[str]
    bad_codes: list[str]
    orphan_folders: list[str]
    listings: list[ListingReport]
    live_locales: list[str] | None = None
    live_error: str | None = None

    @property
    def not_published(self) -> list[str]:
        if self.live_locales is None:
            return []
        return [r.play_locale for r in self.listings if r.play_locale not in self.live_locales]

    @property
    def ok(self) -> bool:
        return (
            not self.unmapped_locales
            and not self.bad_codes
            and not self.orphan_folders
            and all(r.ok for r in self.listings)
        )

    def issue_count(self) -> int:
        return (
            len(self.unmapped_locales)
            + len(self.bad_codes)
            + len(self.orphan_folders)
            + sum(1 for r in self.listings if not r.ok)
        )


def load_supported_locales(path: Path) -> list[str]:
    return LOCALE_NAME_PATTERN.findall(path.read_text(encoding="utf-8"))


def load_play_locale_map(path: Path) -> list[tuple[str, str]]:
    """Returns (resource locale, supply folder) pairs in declaration order."""
    return PLAY_LOCALE_PATTERN.findall(path.read_text(encoding="utf-8"))


def count_pngs(directory: Path) -> int:
    if not directory.is_dir():
        return 0
    return sum(1 for entry in directory.iterdir() if entry.suffix.lower() == ".png")


def check_listing(metadata_dir: Path, resource_locale: str, play_locale: str) -> ListingReport:
    folder = metadata_dir / play_locale
    report = ListingReport(
        play_locale=play_locale,
        resource_locale=resource_locale,
        exists=folder.is_dir(),
    )
    if not report.exists:
        return report

    for name, limit in TEXT_FILES.items():
        file = folder / name
        if not file.is_file():
            report.missing_text.append(name)
            continue
        text = file.read_text(encoding="utf-8").strip()
        if not text:
            report.missing_text.append(name)
        elif len(text) > limit:
            report.overlong_text.append(f"{name} is {len(text)} chars, limit {limit}")

    report.phone_screenshots = count_pngs(folder / "images/phoneScreenshots")
    report.tablet_screenshots = count_pngs(folder / "images/tenInchScreenshots")
    return report


def check_store_listings(
    locales_config: Path,
    play_locales_file: Path,
    metadata_dir: Path,
) -> CheckResult:
    supported = load_supported_locales(locales_config)
    mapping = load_play_locale_map(play_locales_file)

    mapped_resource_locales = {resource_to_config_locale(res) for res, _ in mapping}
    unmapped = [
        loc
        for loc in supported
        if loc not in mapped_resource_locales and loc not in PLAY_UNSUPPORTED_LANGUAGES
    ]
    unsupported = [loc for loc in supported if loc in PLAY_UNSUPPORTED_LANGUAGES]
    bad_codes = sorted({play for _, play in mapping if play not in PLAY_LISTING_LANGUAGES})

    mapped_folders = {play for _, play in mapping}
    existing_folders = (
        {entry.name for entry in metadata_dir.iterdir() if entry.is_dir()}
        if metadata_dir.is_dir()
        else set()
    )
    orphans = sorted(existing_folders - mapped_folders)

    listings = [
        check_listing(metadata_dir, resource, play)
        for resource, play in sorted(mapping, key=lambda pair: pair[1])
    ]
    return CheckResult(
        unmapped_locales=unmapped,
        unsupported_locales=unsupported,
        bad_codes=bad_codes,
        orphan_folders=orphans,
        listings=listings,
    )


def fetch_live_locales(key_file: Path) -> list[str]:
    """Lists the languages the Play listing is actually published in.

    Signs the service-account JWT by hand so the check has no dependency beyond
    'cryptography'; the fastlane gems are not on the path outside a lane.
    """
    import base64
    import time
    import urllib.parse
    import urllib.request

    from cryptography.hazmat.primitives import hashes, serialization
    from cryptography.hazmat.primitives.asymmetric import padding

    def b64(raw: bytes) -> bytes:
        return base64.urlsafe_b64encode(raw).rstrip(b"=")

    info = json.loads(key_file.read_text(encoding="utf-8"))
    now = int(time.time())
    header = b64(json.dumps({"alg": "RS256", "typ": "JWT"}).encode())
    claim = b64(
        json.dumps(
            {
                "iss": info["client_email"],
                "scope": "https://www.googleapis.com/auth/androidpublisher",
                "aud": "https://oauth2.googleapis.com/token",
                "exp": now + 3600,
                "iat": now,
            },
        ).encode(),
    )
    signing_input = header + b"." + claim
    key = serialization.load_pem_private_key(info["private_key"].encode(), password=None)
    signature = key.sign(signing_input, padding.PKCS1v15(), hashes.SHA256())

    token_request = urllib.request.Request(
        "https://oauth2.googleapis.com/token",
        data=urllib.parse.urlencode(
            {
                "grant_type": "urn:ietf:params:oauth:grant-type:jwt-bearer",
                "assertion": (signing_input + b"." + b64(signature)).decode(),
            },
        ).encode(),
    )
    with urllib.request.urlopen(token_request, timeout=30) as response:
        token = json.load(response)["access_token"]

    base = f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{PACKAGE_NAME}"

    def call(method: str, path: str) -> dict:
        request = urllib.request.Request(base + path, method=method)
        request.add_header("Authorization", f"Bearer {token}")
        with urllib.request.urlopen(request, timeout=30) as response:
            raw = response.read()
        return json.loads(raw) if raw else {}

    # Reading listings needs an edit, and an edit left dangling blocks the next one, so it is
    # always deleted again. Nothing is committed, so this stays read-only.
    edit_id = call("POST", "/edits")["id"]
    try:
        listings = call("GET", f"/edits/{edit_id}/listings")
    finally:
        call("DELETE", f"/edits/{edit_id}")
    return sorted(entry["language"] for entry in listings.get("listings", []))


def print_human_report(result: CheckResult, quiet: bool) -> None:
    if result.unmapped_locales:
        print("Languages with no Play listing locale:")
        for locale in result.unmapped_locales:
            print(f"  [{locale}] add it to PlayStoreLocales.kt")
        print()

    if result.bad_codes:
        print("Supply folders Play would reject:")
        for code in result.bad_codes:
            print(f"  [{code}] not a Play store-listing language code")
        print()

    if result.orphan_folders:
        print("Listing folders no language maps to:")
        for folder in result.orphan_folders:
            print(f"  fastlane/metadata/android/{folder} is never rendered or mapped")
        print()

    needs_screenshots = []
    for report in result.listings:
        if report.ok:
            if not quiet:
                print(
                    f"[{report.play_locale}] OK "
                    f"({report.phone_screenshots} phone, {report.tablet_screenshots} tablet)",
                )
            continue

        if not report.exists:
            print(f"[{report.play_locale}] fastlane/metadata/android/{report.play_locale} missing")
            continue

        issues = []
        issues += [f"missing {name}" for name in report.missing_text]
        issues += report.overlong_text
        issues += report.screenshot_issues
        print(f"[{report.play_locale}] " + "; ".join(issues))
        if report.screenshot_issues:
            needs_screenshots.append(report.play_locale)

    if needs_screenshots:
        print()
        print("Render the missing images with:")
        print("  ./gradlew :screenshotTests:generateStoreScreenshots")

    if result.live_error:
        print()
        print(f"Live Play listing not checked: {result.live_error}")
    elif result.live_locales is not None:
        print()
        if result.not_published:
            print(f"Not yet on Play ({len(result.not_published)}):")
            print("  " + " ".join(result.not_published))
            print("Publish them with:")
            print("  bundle exec fastlane android upload_listing")
        else:
            print(f"All {len(result.live_locales)} listings are live on Play.")

    if result.unsupported_locales and not quiet:
        print()
        for locale in result.unsupported_locales:
            print(f"[{locale}] no store listing: {PLAY_UNSUPPORTED_LANGUAGES[locale]}")

    print()
    if result.ok:
        print(f"All {len(result.listings)} store listings are complete.")
    else:
        print(f"Store listing check failed ({result.issue_count()} issue(s)).")


def print_json_report(result: CheckResult) -> None:
    print(
        json.dumps(
            {
                "ok": result.ok,
                "unmapped_locales": result.unmapped_locales,
                "unsupported_locales": result.unsupported_locales,
                "bad_codes": result.bad_codes,
                "orphan_folders": result.orphan_folders,
                "live_locales": result.live_locales,
                "live_error": result.live_error,
                "not_published": result.not_published,
                "listings": [
                    {
                        "play_locale": r.play_locale,
                        "resource_locale": r.resource_locale,
                        "exists": r.exists,
                        "missing_text": r.missing_text,
                        "overlong_text": r.overlong_text,
                        "phone_screenshots": r.phone_screenshots,
                        "tablet_screenshots": r.tablet_screenshots,
                        "ok": r.ok,
                    }
                    for r in result.listings
                ],
                "issue_count": result.issue_count(),
            },
            indent=2,
            ensure_ascii=False,
        ),
    )


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--json", action="store_true", help="machine-readable report")
    parser.add_argument("--quiet", action="store_true", help="only print locales with issues")
    parser.add_argument(
        "--live",
        action="store_true",
        help="also list which listings are missing from the live Play Console entry",
    )
    parser.add_argument("--locales-config", type=Path, default=DEFAULT_LOCALES_CONFIG)
    parser.add_argument("--play-locales", type=Path, default=DEFAULT_PLAY_LOCALES)
    parser.add_argument("--metadata-dir", type=Path, default=DEFAULT_METADATA_DIR)
    parser.add_argument("--play-key", type=Path, default=DEFAULT_PLAY_KEY)
    return parser.parse_args()


def main() -> int:
    args = parse_args()

    for path in (args.locales_config, args.play_locales):
        if not path.is_file():
            raise SystemExit(f"File not found: {path}")

    result = check_store_listings(args.locales_config, args.play_locales, args.metadata_dir)

    if args.live:
        # The live diff is informational: the key is a release secret, so its absence on a
        # contributor machine or in CI must not turn into a failing check.
        if not args.play_key.is_file():
            result.live_error = f"{args.play_key} not found"
        else:
            try:
                result.live_locales = fetch_live_locales(args.play_key)
            except Exception as error:  # noqa: BLE001 - any API/auth failure is just "unknown"
                result.live_error = f"{type(error).__name__}: {error}"

    if args.json:
        print_json_report(result)
    else:
        print_human_report(result, quiet=args.quiet)

    return 0 if result.ok else 1


if __name__ == "__main__":
    sys.exit(main())
