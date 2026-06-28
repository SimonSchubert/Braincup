import Foundation
import GameKit
import UIKit
import ComposeApp

/// Bridges PlayGamesBridge (Kotlin) callbacks to Apple GameKit.
/// Mirrors the Android playStore PlayGamesAchievements wiring.
final class GameCenterBridge: NSObject {

    static let shared = GameCenterBridge()

    private var isAuthenticated: Bool { GKLocalPlayer.local.isAuthenticated }
    private var pendingAuthVC: UIViewController?

    /// Wire callbacks and kick off Game Center authentication.
    /// Safe to call once at app launch (before the first Compose screen renders).
    func start() {
        wireBridgeCallbacks()

        GKLocalPlayer.local.authenticateHandler = { [weak self] viewController, _ in
            guard let self = self else { return }
            if let viewController = viewController {
                // GameKit needs us to present the sign-in UI.
                self.pendingAuthVC = viewController
                self.presentPendingAuthIfReady()
            } else if GKLocalPlayer.local.isAuthenticated {
                self.onAuthenticated()
            }
            // Errors are silently ignored — players can opt out, and the bridge
            // simply no-ops if not authenticated.
        }
    }

    // MARK: - Callback wiring

    private func wireBridgeCallbacks() {
        let bridge = PlayGamesBridge.shared

        bridge.onGoldMedal = { [weak self] gameType in
            self?.reportAchievement(id: GameCenterIds.achievement(for: gameType), percent: 100)
        }

        bridge.onTotalScore = { [weak self] total in
            let raw = Double(truncating: total)
            let target = Double(GameCenterIds.mindMarathonerTarget)
            let percent = min(100.0, raw * 100.0 / target)
            self?.reportAchievement(
                id: GameCenterIds.achievementMindMarathoner,
                percent: percent,
                showsBanner: percent >= 100
            )
        }

        bridge.onStreak = { [weak self] streak in
            // Trigger only the Iron Streak threshold; Kotlin guards on >= 30 too.
            guard Int(truncating: streak) >= 30 else { return }
            self?.reportAchievement(id: GameCenterIds.achievementIronStreak, percent: 100)
        }

        bridge.onSudokuTierProgress = { [weak self] difficulty, solved in
            let count = Double(Int(truncating: solved))
            let target = Double(GameCenterIds.sudokuTierTarget)
            let percent = min(100.0, count * 100.0 / target)
            self?.reportAchievement(
                id: GameCenterIds.sudokuTierAchievement(forName: difficulty.name),
                percent: percent,
                showsBanner: percent >= 100
            )
        }

        bridge.onMatchstickRiddlesProgress = { [weak self] solved in
            let count = Double(Int(truncating: solved))
            let target = Double(Int(MatchstickRiddles.shared.count))
            let percent = min(100.0, count * 100.0 / target)
            self?.reportAchievement(
                id: GameCenterIds.achievementMatchstickMaster,
                percent: percent,
                showsBanner: percent >= 100
            )
        }

        bridge.onSubmitScore = { [weak self] gameType, score in
            guard let id = GameCenterIds.leaderboard(for: gameType) else { return }
            self?.submitScore(Int(truncating: score), leaderboardID: id)
        }

        bridge.onSubmitTotalXp = { [weak self] totalXp in
            self?.submitScore(Int(truncating: totalXp), leaderboardID: GameCenterIds.leaderboardBrainCup)
        }

        bridge.onShowLeaderboard = { [weak self] gameType in
            guard let id = GameCenterIds.leaderboard(for: gameType) else { return }
            self?.presentLeaderboard(id: id)
        }

        bridge.onShowBrainCup = { [weak self] in
            self?.presentLeaderboard(id: GameCenterIds.leaderboardBrainCup)
        }
    }

    // MARK: - Post-auth

    private func onAuthenticated() {
        restoreAchievements()
        syncBrainCupXp()
    }

    private func restoreAchievements() {
        GKAchievement.loadAchievements { achievements, error in
            guard error == nil, let achievements = achievements else { return }
            let storage = MainViewControllerKt.createUserStorage()

            var toRestore = Set<UserStorage.Achievements>()
            for a in achievements where a.percentComplete >= 100 {
                if let mapped = GameCenterIds.userStorageAchievement(forGameCenterId: a.identifier) {
                    toRestore.insert(mapped)
                }
            }
            if !toRestore.isEmpty {
                storage.restoreUnlockedAchievements(achievements: toRestore)
            }

            // Restore partial Normal Sudoku tier progress: only the count is recoverable, so
            // convert percentComplete back to a solved count and seed local progress.
            let target = Double(GameCenterIds.sudokuTierTarget)
            for a in achievements {
                guard let difficulty = GameCenterIds.sudokuTier(forGameCenterId: a.identifier) else { continue }
                let count = Int((a.percentComplete / 100.0 * target).rounded())
                if count > 0 {
                    storage.restoreSudokuTierProgressIfHigher(difficulty: difficulty, remoteCount: Int32(count))
                }
            }

            // Restore Matchstick Riddles progress the same way: percentComplete back to a solved count.
            let matchstickTarget = Double(Int(MatchstickRiddles.shared.count))
            for a in achievements where a.identifier == GameCenterIds.achievementMatchstickMaster {
                let count = Int((a.percentComplete / 100.0 * matchstickTarget).rounded())
                if count > 0 {
                    storage.restoreMatchstickRiddlesProgressIfHigher(remoteCount: Int32(count))
                }
            }
        }
    }

    private func syncBrainCupXp() {
        GKLeaderboard.loadLeaderboards(IDs: [GameCenterIds.leaderboardBrainCup]) { boards, error in
            guard error == nil, let board = boards?.first else { return }
            board.loadEntries(for: .global, timeScope: .allTime, range: NSRange(location: 1, length: 1)) { localEntry, _, _, err in
                guard err == nil else { return }
                let remoteXp = Int(localEntry?.score ?? 0)
                let storage = MainViewControllerKt.createUserStorage()
                if remoteXp > 0 {
                    let restored = storage.restoreTotalXpIfHigher(remoteXp: Int32(remoteXp))
                    if restored {
                        PlayGamesBridge.shared.onTotalXpRestored?(KotlinInt(int: Int32(remoteXp)))
                    }
                }
                let localXp = Int(storage.getTotalXp())
                if localXp > 0 {
                    self.submitScore(localXp, leaderboardID: GameCenterIds.leaderboardBrainCup)
                }
            }
        }
    }

    // MARK: - GameKit primitives

    private func reportAchievement(id: String?, percent: Double, showsBanner: Bool = true) {
        guard let id = id, isAuthenticated else { return }
        let achievement = GKAchievement(identifier: id)
        achievement.percentComplete = percent
        achievement.showsCompletionBanner = showsBanner
        GKAchievement.report([achievement]) { _ in }
    }

    private func submitScore(_ score: Int, leaderboardID: String) {
        guard isAuthenticated else { return }
        GKLeaderboard.submitScore(
            score,
            context: 0,
            player: GKLocalPlayer.local,
            leaderboardIDs: [leaderboardID]
        ) { _ in }
    }

    private func presentLeaderboard(id: String) {
        guard isAuthenticated else { return }
        DispatchQueue.main.async { [weak self] in
            let vc = GKGameCenterViewController(leaderboardID: id, playerScope: .global, timeScope: .allTime)
            vc.gameCenterDelegate = self
            self?.present(vc)
        }
    }

    // MARK: - View-controller presentation

    private func presentPendingAuthIfReady() {
        DispatchQueue.main.async { [weak self] in
            guard let self = self, let vc = self.pendingAuthVC else { return }
            self.pendingAuthVC = nil
            self.present(vc) { [weak self] in
                self?.onAuthenticated()
            }
        }
    }

    private func present(_ viewController: UIViewController, completion: (() -> Void)? = nil) {
        guard let root = topMostViewController() else { return }
        root.present(viewController, animated: true, completion: completion)
    }

    private func topMostViewController() -> UIViewController? {
        let scene = UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .first(where: { $0.activationState == .foregroundActive })
            ?? UIApplication.shared.connectedScenes
                .compactMap { $0 as? UIWindowScene }
                .first
        guard let window = scene?.windows.first(where: { $0.isKeyWindow }) ?? scene?.windows.first else {
            return nil
        }
        var top = window.rootViewController
        while let presented = top?.presentedViewController {
            top = presented
        }
        return top
    }
}

extension GameCenterBridge: GKGameCenterControllerDelegate {
    func gameCenterViewControllerDidFinish(_ gameCenterViewController: GKGameCenterViewController) {
        gameCenterViewController.dismiss(animated: true)
    }
}
