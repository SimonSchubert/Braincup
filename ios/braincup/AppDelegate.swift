//
//  AppDelegate.swift
//  braincup
//
//  Created by Simon Schubert on 03.10.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import UIKit
import SwiftUI
import shared

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        return true
    }

    // MARK: UISceneSession Lifecycle

    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }

}

extension SwiftUI.Color {
    init(hex: Int) {
        let components = (
            R: Double((hex >> 16) & 0xff) / 255,
            G: Double((hex >> 08) & 0xff) / 255,
            B: Double((hex >> 00) & 0xff) / 255
        )
        self.init(
            .sRGB,
            red: components.R,
            green: components.G,
            blue: components.B,
            opacity: 1
        )
    }
}

extension shared.Shape {
    func getChar() -> String {
        switch self {
            case shared.Shape.square:
                return "■"
            case shared.Shape.triangle:
                return "▲"
            case shared.Shape.circle:
                return "●"
            case shared.Shape.heart:
                return "♥"
            default:
                return ""
        }
    }
}

extension shared.Color {
    func getColor() -> SwiftUI.Color {
        switch self {
            case shared.Color.red:
                return Color.red
            case shared.Color.green:
                return Color.green
            case shared.Color.blue:
                return Color.blue
            case shared.Color.purple:
                return Color.purple
            default:
                return Color.black
        }
    }
}

extension GameType {
    func getMedalResource(score: Int32) -> String {
        let scoreTable = self.getScoreTable()
        if(score > scoreTable.get(index: 0) as! Int) {
            return "icons8-medal_first_place"
        }
        if(score > scoreTable.get(index: 1) as! Int) {
            return "icons8-medal_second_place"
        }
        return "icons8-medal_third_place"
    }
}




