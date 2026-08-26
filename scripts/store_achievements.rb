#!/usr/bin/env ruby
# frozen_string_literal: true

# Create the Learn Math certificate achievements on Google Play Games and Apple Game Center.
#
# fastlane cannot do this: it has no achievement support for either store (checked against
# 2.238.0). Both stores do have an API, and this uses the credentials fastlane already relies on:
#
#   Google  fastlane/play-store-key.json, scope androidpublisher, against the Google Play Games
#           Services Publishing API (gamesconfiguration.googleapis.com).
#   Apple   ~/.appstoreconnect/private_keys/AuthKey_$ASC_KEY_ID.p8 plus $ASC_KEY_ID and
#           $ASC_ISSUER_ID, against the App Store Connect API.
#
# Run it with fastlane's bundled gems (googleauth, jwt), which need no extra install:
#
#   GEM_HOME="$HOME/.local/share/fastlane/4.0.0" \
#   GEM_PATH="$HOME/.local/share/fastlane/4.0.0:/opt/homebrew/Cellar/fastlane/2.238.0/libexec" \
#     /opt/homebrew/opt/ruby/bin/ruby scripts/store_achievements.rb
#
# Dry run by default: it prints what it would create and writes nothing. Pass --execute to write.
# Safe to re-run either way; anything already on a store is skipped, matched by its immutable id
# (Apple) or its en-US title (Google, which assigns ids itself and has nothing else stable).
#
# After creating on Google it rewrites the blank achievementCert* placeholders in play_games.xml
# with the console-assigned ids. That paste-back step is the one documented way this silently
# half-lands: a blank id compiles and no-ops, so the medal simply never unlocks.

require "json"
require "net/http"
require "openssl"
require "optparse"
require "rexml/document"
require "googleauth"
require "jwt"

ROOT = File.expand_path("..", __dir__)

PLAY_APP_ID = "743664291129"          # play_games_app_id in play_games.xml
ASC_BUNDLE_ID = "com.inspiredandroid.braincup"

# Game Center caps an app at 1000 points and 380 were in use as of 2026-08-26, so 22 x 10 fits with
# 400 to spare. Play Games has no cap, but it does reject any pointValue that is not a multiple of
# five, so 5 is the floor there (0, 1 and 2 all come back 400 "not within range or is not a
# multiple of five").
GAME_CENTER_POINTS = 10
PLAY_GAMES_XP = 5

# Highest sortRank live on Play Games as of 2026-08-26 (Triple Vision). New rows continue past it.
PLAY_SORT_RANK_BASE = 46

# unit id, sub-topic (as it appears in the description), title, icon basename.
# Mirrors the certificates table in media/achievements/README.md.
CERTIFICATES = [
  ["arithmetic-counting",         "Counting and first sums",      "Counting Cadet",     "49_learn_arithmetic_counting"],
  ["arithmetic-multiplication",   "Multiplication and division",  "Times Table Titan",  "50_learn_arithmetic_multiplication"],
  ["arithmetic-fractions",        "Fractions",                    "Piece of the Pie",   "51_learn_arithmetic_fractions"],
  ["arithmetic-decimals",         "Decimals",                     "Point Taken",        "52_learn_arithmetic_decimals"],
  ["arithmetic-negatives",        "Negative numbers",             "Below Zero",         "53_learn_arithmetic_negatives"],
  ["arithmetic-ratio",            "Ratio and proportion",         "In Proportion",      "54_learn_arithmetic_ratio"],
  ["arithmetic-percent",          "Percentages",                  "Hundred Percent",    "55_learn_arithmetic_percent"],
  ["arithmetic-standard-form",    "Standard form",                "Powers of Ten",      "56_learn_arithmetic_standard_form"],
  ["arithmetic-surds",            "Surds",                        "Root Cause",         "57_learn_arithmetic_surds"],
  ["arithmetic-bounds",           "Rounding and bounds",          "Within Bounds",      "58_learn_arithmetic_bounds"],
  ["geometry-flat-shapes",        "Flat shapes",                  "Corner Counter",     "59_learn_geometry_flat_shapes"],
  ["geometry-solid-shapes",       "Solid shapes",                 "Solid Thinking",     "60_learn_geometry_solid_shapes"],
  ["geometry-angles",             "Angles and turns",             "Full Turn",          "61_learn_geometry_angles"],
  ["geometry-quadrilaterals",     "Triangles and quadrilaterals", "Sorted by Sides",    "62_learn_geometry_quadrilaterals"],
  ["geometry-symmetry",           "Symmetry",                     "Mirror Image",       "63_learn_geometry_symmetry"],
  ["geometry-perimeter-and-area", "Perimeter and area",           "Fence and Floor",    "64_learn_geometry_perimeter_and_area"],
  ["geometry-pythagoras",         "Pythagoras' theorem",          "Hypotenuse Hero",    "65_learn_geometry_pythagoras"],
  ["geometry-circles",            "Circles",                      "Round Numbers",      "66_learn_geometry_circles"],
  ["geometry-volume",             "Volume and surface area",      "Filled to the Brim", "67_learn_geometry_volume"],
  ["geometry-similarity",         "Similarity and scale",         "Scaled Up",          "68_learn_geometry_similarity"],
  ["geometry-transformations",    "Transformations",              "Slide, Flip, Turn",  "69_learn_geometry_transformations"],
  ["geometry-circle-theorems",    "Circle theorems",              "Full Circle",        "70_learn_geometry_circle_theorems"],
].map do |unit_id, sub_topic, title, icon|
  {
    unit_id: unit_id,
    title: title,
    before: "Earn the #{sub_topic} certificate",
    after: "Earned the #{sub_topic} certificate!",
    vendor_id: "achievement.cert_#{unit_id.tr('-', '_')}",
    xml_key: "achievementCert" + unit_id.tr("-", "_").split("_").map(&:capitalize).join,
    icon: File.join(ROOT, "media/achievements/png/#{icon}.png"),
  }
end

# --- Preflight -------------------------------------------------------------

# The Kotlin list is what the app actually fires on, so a table that has drifted from it would
# create achievements nothing unlocks. Cheaper to catch here than in the store.
def verify_against_kotlin
  path = File.join(ROOT, "composeApp/src/commonMain/kotlin/com/inspiredandroid/braincup/learn/LearnStoreAchievements.kt")
  body = File.read(path)[/certifiedUnitIds[^(]*\(([^)]*)\)/m, 1].to_s
  kotlin = body.scan(/"([^"]+)"/).flatten
  mine = CERTIFICATES.map { |c| c[:unit_id] }
  return if kotlin == mine

  abort("CERTIFICATES has drifted from LearnStoreAchievements.kt\n" \
        "  only in Kotlin: #{(kotlin - mine).inspect}\n" \
        "  only here:      #{(mine - kotlin).inspect}")
end

def verify_icons
  missing = CERTIFICATES.reject { |c| File.exist?(c[:icon]) }
  return if missing.empty?

  abort("missing icons (run: python3 media/achievements/generate.py)\n" +
        missing.map { |c| "  #{c[:icon]}" }.join("\n"))
end

# --- HTTP ------------------------------------------------------------------

# Both stores return the odd 5xx or 429 under load; an image upload 503 is what killed the first
# run of this script part-way through. Retry those, never a 4xx (a bad request stays bad).
RETRYABLE = [408, 429, 500, 502, 503, 504].freeze

def request(method, url, token:, body: nil, content_type: "application/json", headers: {}, attempt: 1)
  uri = URI(url)
  req = Net::HTTPGenericRequest.new(method, !body.nil?, true, uri)
  req["Authorization"] = "Bearer #{token}" if token
  req["Content-Type"] = content_type if body
  headers.each { |k, v| req[k] = v }
  req.body = body.is_a?(String) ? body : JSON.generate(body) if body
  res = Net::HTTP.start(uri.host, uri.port, use_ssl: true) { |h| h.request(req) }
  parsed = res.body.to_s.empty? ? {} : (JSON.parse(res.body) rescue res.body)
  unless res.code.to_i.between?(200, 299)
    if RETRYABLE.include?(res.code.to_i) && attempt < 5
      sleep(2**attempt)
      return request(method, url, token: token, body: body, content_type: content_type,
                     headers: headers, attempt: attempt + 1)
    end
    raise "#{method} #{uri.path} -> HTTP #{res.code}\n#{JSON.pretty_generate(parsed) rescue parsed}"
  end
  parsed
end

# --- Apple -----------------------------------------------------------------

class GameCenter
  HOST = "https://api.appstoreconnect.apple.com"

  def initialize
    kid = ENV.fetch("ASC_KEY_ID")
    iss = ENV.fetch("ASC_ISSUER_ID")
    pem = File.read(File.expand_path("~/.appstoreconnect/private_keys/AuthKey_#{kid}.p8"))
    now = Time.now.to_i
    @token = JWT.encode({ iss: iss, iat: now, exp: now + 1200, aud: "appstoreconnect-v1" },
                        OpenSSL::PKey::EC.new(pem), "ES256", { kid: kid, typ: "JWT" })
  end

  def detail_id
    @detail_id ||= begin
      apps = get("/v1/apps?filter%5BbundleId%5D=#{ASC_BUNDLE_ID}")
      app_id = apps.dig("data", 0, "id") or raise "no app for #{ASC_BUNDLE_ID}"
      get("/v1/apps/#{app_id}/gameCenterDetail").dig("data", "id") or raise "no gameCenterDetail"
    end
  end

  def existing
    @existing ||= begin
      out = {}
      url = "#{HOST}/v1/gameCenterDetails/#{detail_id}/gameCenterAchievements?limit=200"
      while url
        b = request("GET", url, token: @token)
        b["data"].each { |a| out[a.dig("attributes", "vendorIdentifier")] = a["id"] }
        url = b.dig("links", "next")
      end
      out
    end
  end

  def points_used
    total = 0
    url = "#{HOST}/v1/gameCenterDetails/#{detail_id}/gameCenterAchievements?limit=200"
    while url
      b = request("GET", url, token: @token)
      total += b["data"].sum { |a| a.dig("attributes", "points").to_i }
      url = b.dig("links", "next")
    end
    total
  end

  def create(cert)
    achievement = post("/v1/gameCenterAchievements", {
      data: {
        type: "gameCenterAchievements",
        attributes: {
          referenceName: cert[:title],
          vendorIdentifier: cert[:vendor_id],
          points: GAME_CENTER_POINTS,
          showBeforeEarned: true,
          repeatable: false,
        },
        relationships: {
          gameCenterDetail: { data: { type: "gameCenterDetails", id: detail_id } },
        },
      },
    }).dig("data", "id")

    localization = post("/v1/gameCenterAchievementLocalizations", {
      data: {
        type: "gameCenterAchievementLocalizations",
        attributes: {
          locale: "en-US",
          name: cert[:title],
          beforeEarnedDescription: cert[:before],
          afterEarnedDescription: cert[:after],
        },
        relationships: {
          gameCenterAchievement: { data: { type: "gameCenterAchievements", id: achievement } },
        },
      },
    }).dig("data", "id")

    upload_image(localization, cert[:icon])
    achievement
  end

  private

  # Reserve, PUT the bytes to each operation the reservation hands back, then commit.
  def upload_image(localization_id, path)
    bytes = File.binread(path)
    reservation = post("/v1/gameCenterAchievementImages", {
      data: {
        type: "gameCenterAchievementImages",
        attributes: { fileName: File.basename(path), fileSize: bytes.bytesize },
        relationships: {
          gameCenterAchievementLocalization: {
            data: { type: "gameCenterAchievementLocalizations", id: localization_id },
          },
        },
      },
    })
    image_id = reservation.dig("data", "id")
    reservation.dig("data", "attributes", "uploadOperations").each do |op|
      headers = (op["requestHeaders"] || []).to_h { |h| [h["name"], h["value"]] }
      chunk = bytes.byteslice(op["offset"].to_i, op["length"].to_i)
      request(op["method"], op["url"], token: nil, body: chunk,
              content_type: headers["Content-Type"] || "image/png", headers: headers)
    end
    request("PATCH", "#{HOST}/v1/gameCenterAchievementImages/#{image_id}", token: @token, body: {
      data: { type: "gameCenterAchievementImages", id: image_id, attributes: { uploaded: true } },
    })
  end

  def get(path) = request("GET", "#{HOST}#{path}", token: @token)
  def post(path, body) = request("POST", "#{HOST}#{path}", token: @token, body: body)
end

# --- Google ----------------------------------------------------------------

class PlayGames
  # Canonical rootUrl from the discovery document. www.googleapis.com also routes these, but this
  # is the host the API actually advertises.
  BASE = "https://gamesconfiguration.googleapis.com/games/v1configuration"

  def initialize
    creds = ::Google::Auth::ServiceAccountCredentials.make_creds(
      json_key_io: File.open(File.join(ROOT, "fastlane/play-store-key.json")),
      scope: ["https://www.googleapis.com/auth/androidpublisher"],
    )
    creds.fetch_access_token!
    @token = creds.access_token
  end

  def all
    @all ||= request("GET", "#{BASE}/applications/#{PLAY_APP_ID}/achievements?maxResults=200",
                     token: @token)["items"] || []
  end

  # Google assigns the id, so an existing row can only be matched on its en-US title.
  def existing_by_title
    all.to_h do |a|
      d = a["published"] || a["draft"] || {}
      [d.dig("name", "translations")&.find { |t| t["locale"] == "en-US" }&.dig("value"), a["id"]]
    end
  end

  def next_sort_rank
    (all.map { |a| (a["published"] || a["draft"] || {})["sortRank"].to_i }.max || PLAY_SORT_RANK_BASE) + 1
  end

  # Idempotent and self-healing: creates what is missing, and repairs a row that exists but is
  # wrong or incomplete. A create followed by a failed icon upload leaves exactly that, which is
  # how the first run of this script ended, so re-running has to finish the job rather than skip it.
  # Returns [id, [actions]].
  def ensure(cert, sort_rank)
    id = existing_by_title[cert[:title]]
    actions = []

    if id.nil?
      id = request("POST", "#{BASE}/applications/#{PLAY_APP_ID}/achievements", token: @token, body: {
        achievementType: "STANDARD",
        initialState: "REVEALED",
        draft: {
          name: { translations: [{ locale: "en-US", value: cert[:title] }] },
          description: { translations: [{ locale: "en-US", value: cert[:before] }] },
          pointValue: PLAY_GAMES_XP,
          sortRank: sort_rank,
        },
      })["id"]
      actions << "created"
    end

    record = request("GET", "#{BASE}/achievements/#{id}", token: @token)
    draft = record["draft"] || {}

    if draft["pointValue"] != PLAY_GAMES_XP
      # The whole resource goes back, id included: a PUT missing it is rejected as
      # "achievement definition with ID  was not found".
      request("PUT", "#{BASE}/achievements/#{id}", token: @token,
              body: record.merge("draft" => draft.merge("pointValue" => PLAY_GAMES_XP)))
      actions << "xp #{draft['pointValue']}->#{PLAY_GAMES_XP}"
    end

    # Icons cannot be set from here. The Games Configuration API exposes only
    # achievementConfigurations and leaderboardConfigurations (checked against its discovery
    # document); the old imageConfigurations.upload is gone, and calling it just returns a bare
    # 503. Play Console is the only way to attach an achievement icon, so this reports which rows
    # still need one instead of pretending to handle it. Apple's API does support upload and this
    # script uses it there.
    actions << "NEEDS ICON" if draft["iconUrl"].to_s.empty?

    [id, actions]
  end
end

# --- play_games.xml --------------------------------------------------------

# Fills in a blank <string name="achievementCert..."></string> in place, leaving the surrounding
# comments and formatting untouched. Rewriting the file through REXML would reflow all of it.
def write_xml_ids(assigned)
  path = File.join(ROOT, "androidApp/src/playStore/res/values/play_games.xml")
  xml = File.read(path)
  assigned.each do |key, id|
    pattern = /(<string name="#{Regexp.escape(key)}" translatable="false">)(\s*)(<\/string>)/
    unless xml =~ pattern
      warn "  ! #{key} is not a blank placeholder in play_games.xml, leaving it alone"
      next
    end
    xml = xml.sub(pattern, "\\1#{id}\\3")
  end
  File.write(path, xml)
end

# --- Main ------------------------------------------------------------------

options = { execute: false, store: "both" }
OptionParser.new do |o|
  o.banner = "Usage: ruby scripts/store_achievements.rb [options]"
  o.on("--execute", "Actually create them. Without this, nothing is written.") { options[:execute] = true }
  o.on("--store STORE", %w[apple google both], "apple, google or both (default both)") { |v| options[:store] = v }
end.parse!

verify_against_kotlin
verify_icons

mode = options[:execute] ? "EXECUTE" : "DRY RUN"
puts "#{mode}  store=#{options[:store]}  #{CERTIFICATES.size} certificates"
puts

if %w[apple both].include?(options[:store])
  apple = GameCenter.new
  have = apple.existing
  todo = CERTIFICATES.reject { |c| have.key?(c[:vendor_id]) }
  used = apple.points_used
  puts "Apple Game Center (detail #{apple.detail_id})"
  puts "  live: #{have.size}   points: #{used}/1000   after this run: #{used + todo.size * GAME_CENTER_POINTS}/1000"
  puts "  already present, skipping: #{CERTIFICATES.size - todo.size}" if todo.size < CERTIFICATES.size
  if (used + todo.size * GAME_CENTER_POINTS) > 1000
    abort("  would exceed the 1000-point cap; lower GAME_CENTER_POINTS")
  end
  todo.each do |c|
    if options[:execute]
      id = apple.create(c)
      puts "  created #{c[:vendor_id]}  (#{id})  #{c[:title]}"
    else
      puts "  would create #{c[:vendor_id]}  #{GAME_CENTER_POINTS}pt  #{c[:title].inspect}  #{c[:before].inspect}  icon=#{File.basename(c[:icon])}"
    end
  end
  puts
end

if %w[google both].include?(options[:store])
  google = PlayGames.new
  have = google.existing_by_title
  missing = CERTIFICATES.reject { |c| have.key?(c[:title]) }
  rank = google.next_sort_rank
  assigned = {}
  puts "Google Play Games (app #{PLAY_APP_ID})"
  puts "  live: #{google.all.size}   sortRank continues at #{rank}"
  puts "  already present: #{CERTIFICATES.size - missing.size}   to create: #{missing.size}"
  CERTIFICATES.each_with_index do |c, i|
    if options[:execute]
      id, actions = google.ensure(c, rank + i)
      assigned[c[:xml_key]] = id
      puts "  #{actions.empty? ? 'ok      ' : actions.join(' + ').ljust(8)} #{id}  #{c[:title]}"
    elsif have.key?(c[:title])
      puts "  present  #{have[c[:title]]}  #{c[:title]}  (would verify xp=#{PLAY_GAMES_XP} and icon)"
    else
      puts "  create   #{c[:xml_key]}  #{PLAY_GAMES_XP}xp  #{c[:title].inspect}  #{c[:before].inspect}  icon=#{File.basename(c[:icon])}"
    end
  end
  if options[:execute]
    write_xml_ids(assigned)
    puts "  wrote #{assigned.size} ids into play_games.xml"
  else
    puts "  would then write #{CERTIFICATES.size} ids into play_games.xml"
  end
  puts
end

puts options[:execute] ? "Done." : "Nothing was written. Re-run with --execute."
