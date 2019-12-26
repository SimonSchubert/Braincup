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
    
    init(hex: String) {
        let hexSanitized = hex.replacingOccurrences(of: "#", with: "")

        var rgb: UInt64 = 0

        var r: CGFloat = 0.0
        var g: CGFloat = 0.0
        var b: CGFloat = 0.0
        var a: CGFloat = 1.0

        Scanner(string: hexSanitized).scanHexInt64(&rgb)
        
        let length = hexSanitized.count

        if length == 6 {
            r = CGFloat((rgb & 0xFF0000) >> 16) / 255.0
            g = CGFloat((rgb & 0x00FF00) >> 8) / 255.0
            b = CGFloat(rgb & 0x0000FF) / 255.0
        } else if length == 8 {
            r = CGFloat((rgb & 0xFF000000) >> 24) / 255.0
            g = CGFloat((rgb & 0x00FF0000) >> 16) / 255.0
            b = CGFloat((rgb & 0x0000FF00) >> 8) / 255.0
            a = CGFloat(rgb & 0x000000FF) / 255.0
        }

        self.init(red: Double(r), green: Double(g), blue: Double(b), opacity: Double(a))
    }
}

extension Array {
    func chunked(into size: Int) -> [[Element]] {
        return stride(from: 0, to: count, by: size).map {
            Array(self[$0 ..< Swift.min($0 + size, count)])
        }
    }
}

extension shared.Figure {
    func draw(size: Int32) -> some View {
        return self.shape.draw(size: size, color: self.color, rotation: self.rotation)
    }
}

extension shared.Shape {
    func draw(size: Int32, color: shared.Color, rotation: Int32) -> some View {
        return Path { p in
            var movedToStart = false
            self.getPaths().forEach { group in
                let x = (group.component1() as! Float32) * Float32(size)
                let y = (group.component2() as! Float32) * Float32(size)
                let point = CGPoint(x: Int(x), y: Int(y))
                if(!movedToStart) {
                    p.move(to: point)
                    movedToStart = true
                } else {
                    p.addLine(to: point)
                }
            }
        }.fill(color.getColor()).rotationEffect(Angle(degrees: Double(rotation)), anchor: UnitPoint(x: 0.5, y: 0.5)).frame(width: CGFloat(size), height: CGFloat(size)).padding(.all, 4)
    }
}

extension shared.Color {
    func getColor() -> SwiftUI.Color {
        return Color(hex: self.getHex())
    }
}

extension shared.GameType {
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
    
    func getImageResource() -> String {
        switch self {
            case GameType.sherlockCalculation:
                return "icons8-search"
            case GameType.colorConfusion:
                return "icons8-fill_color"
            case GameType.chainCalculation:
                return "icons8-edit_link"
            case GameType.mentalCalculation:
                return "icons8-math"
            case GameType.heightComparison:
                return "icons8-height"
            case GameType.fractionCalculation:
                return "icons8-divide"
            case GameType.riddle:
                return "icons8-questions"
            case GameType.pathFinder:
                return "icons8-hard_to_find"
            case GameType.anomalyPuzzle:
                return "icons8-telescope"
            default:
                return ""
        }
    }
}




