import SwiftUI

@main
struct iOSApp: App {
    init() {
        GameCenterBridge.shared.start()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
